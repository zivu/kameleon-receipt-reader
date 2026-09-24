package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.model.Items;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final GoogleVisionService visionService;
    private final ChatGPTService gptService;

    public Items process(MultipartFile receipt) {
        String receiptString = visionService.process(receipt);
        return gptService.translate(receiptString);
    }

}
