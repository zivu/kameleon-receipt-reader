package com.example.kameleon_receipts_reader.resource;

import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.service.ReceiptService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin(origins = {"https://angular-test-970a4.web.app", "http://localhost:4200", "https://zivu.github.io/kameleon-receipt-reader-frontend"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    /**
     * This service calls Google Vision to recognize receipt's text
     * and transfers this data to AI for analysis.
     */
    private final ReceiptService receiptService;

    @PostMapping
    public UUID uploadReceipt(@RequestParam @NonNull MultipartFile receipt) throws IOException {
        log.info("Received receipt={}", receipt.getName());
        UUID uuid = UUID.randomUUID();
        Path tempFile = Files.createTempFile("receipt-", ".tmp");
        receipt.transferTo(tempFile);
        receiptService.process(tempFile, uuid);
        return uuid;
    }

    @GetMapping("/{uuid}")
    public Items getReceipt(@PathVariable UUID uuid) {
        log.info("Fetching receipt by Id={}", uuid);
        Items items = receiptService.fetchReceipt(uuid);
        log.info("Receipt successfully fetched");
        return items;
    }

}
