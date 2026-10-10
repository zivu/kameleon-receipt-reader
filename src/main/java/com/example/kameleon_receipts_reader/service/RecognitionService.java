package com.example.kameleon_receipts_reader.service;

import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Bridge to be implemented by underlying image processor service.
 */
public interface RecognitionService {

    /**
     * Main method to be called to read text from image.
     * @param receipt path file image.
     * @return recognized receipt text.
     */
    String process(@NonNull MultipartFile receipt);

}
