package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.model.Items;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Receives receipt photo and calculates the cost of a separate positions of a meal.
     * @param receipt image file.
     * @return analysed receipt which shows detailed information about pricing.
     */
    public Items process(@NonNull MultipartFile receipt) {
        String receiptText = recognitionService.process(receipt);
        return analysisService.analyse(receiptText);
    }

}
