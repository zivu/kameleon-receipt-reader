package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.entity.Receipt;
import com.example.kameleon_receipts_reader.entity.ReceiptItem;
import com.example.kameleon_receipts_reader.model.Item;
import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.repository.ReceiptRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

/**
 * This service processes image by colling Recognition Service and analyses receipt via Analysis Service.
 */
@Service
@RequiredArgsConstructor
public class ReceiptService {

    /**
     * Bridge to call underlying recognition service.
     */
    private final RecognitionService recognitionService;
    /**
     * Bridge to call underlying service for String analysis of receipt.
     */
    private final AnalysisService analysisService;
    private final ReceiptRepository receiptRepository;

    /**
     * Receives receiptPath photo and calculates the cost of a separate positions of a meal.
     * @param receiptPath image file.
     * @return analysed receiptPath which shows detailed information about pricing.
     */
    @Async
    public void process(@NonNull Path receiptPath, UUID uuid) {
        String receiptText = recognitionService.process(receiptPath);
        Items receiptItems = analysisService.analyse(receiptText);
        Receipt receipt = new Receipt();
        receipt.setId(uuid);
        receipt.setItems(receiptItems.items().stream()
                .map(item -> {
                    var receiptItem = new ReceiptItem();
                    receiptItem.setId(UUID.randomUUID());
                    receiptItem.setReceipt(receipt);
                    receiptItem.setName(item.name());
                    receiptItem.setQuantity(item.quantity());
                    receiptItem.setUnitPrice(item.unitPrice());
                    receiptItem.setLinePriceBeforeDiscount(item.linePriceBeforeDiscount());
                    receiptItem.setDiscount(item.discount());
                    receiptItem.setLinePrice(item.linePrice());
                    receiptItem.setUnitPricePaid(item.unitPricePaid());
                    return receiptItem;
                }).toList());
        receiptRepository.save(receipt);
    }

    public Items fetchReceipt(@NonNull UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() -> new EntityNotFoundException("Receipt not found"));
        return new Items(receipt.getItems()
                .stream()
                .map(entity ->
                        new Item(entity.getName(), entity.getQuantity(), entity.getUnitPrice(), entity.getLinePriceBeforeDiscount(),
                                entity.getDiscount(), entity.getLinePrice(), entity.getUnitPricePaid()))
                .toList());
    }


}
