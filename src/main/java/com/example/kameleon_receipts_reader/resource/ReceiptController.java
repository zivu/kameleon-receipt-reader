package com.example.kameleon_receipts_reader.resource;

import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://angular-test-970a4.web.app", "http://localhost:4200"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService service;

    @PostMapping("/api/receipts")
    public Items uploadReceipts(Authentication authentication,
                                @RequestParam MultipartFile receipt) {
        return service.process(receipt);
    }

    @GetMapping("/api/auth/me")
    public Object me(Authentication authentication) {
        log.info("User is authenticated={}", authentication != null);
        return Map.of(
                "authenticated", authentication != null,
                "name", authentication != null ? authentication.getName() : ""
        );
    }

}
