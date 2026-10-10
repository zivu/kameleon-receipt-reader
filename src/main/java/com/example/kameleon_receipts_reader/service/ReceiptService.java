package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.entity.Receipt;
import com.example.kameleon_receipts_reader.entity.ReceiptItem;
import com.example.kameleon_receipts_reader.model.Item;
import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.repository.ReceiptRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * This service processes image by colling Recognition Service and analyses receipt via Analysis Service.
 */
@Slf4j
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
     * Receives uploadedReceipt photo and calculates the cost of a separate positions of a meal.
     * @param uploadedReceipt image file.
     */
    public void process(@NonNull MultipartFile uploadedReceipt, UUID uuid) {
        String receiptText = recognitionService.process(uploadedReceipt);
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
                        new Item(entity.getName(), entity.getQuantity(), entity.getUnitPricePaid()))
                .toList());
    }


}
