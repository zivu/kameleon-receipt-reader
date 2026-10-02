package com.example.kameleon_receipts_reader.resource;

import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.service.ReceiptService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://angular-test-970a4.web.app", "http://localhost:4200"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    /**
     * This service calls Google Vision to recognize receipt's text
     * and transfers this data to AI for analysis.
     */
    private final ReceiptService receiptService;

    @PostMapping("/receipt")
    public Items uploadReceipt(@RequestParam @NonNull MultipartFile receipt) {
        log.info("Received receipt={}", receipt.getName());
        Items processedReceipt = receiptService.process(receipt);
        log.info("Receipt successfully processed={}", null != processedReceipt && null != processedReceipt.items());
        return processedReceipt;
    }

}
