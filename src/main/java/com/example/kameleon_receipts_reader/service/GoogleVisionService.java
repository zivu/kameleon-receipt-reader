package com.example.kameleon_receipts_reader.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Google Vision service responsible for OCR.
 */
@Slf4j
@Service
public class GoogleVisionService implements RecognitionService {

    /**
     * Fallback text in case there is no response.
     */
    public static final String NO_RESPONSE_RETURNED = "NO_RESPONSE_RETURNED";

    /**
     * Calls Google Vision service and returns a receipt visible text.
     * @param receipt file image.
     * @return text from provided receipt.
     */
    @Override
    public String process(@NonNull Path receipt) {
        try(var client = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> request = List.of(buildImageRequest(receipt));
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(request);
            return processResponse(response.getResponsesList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process provided receipt", e);
        }
    }

    private static String processResponse(List<AnnotateImageResponse> responses) {
        for (var res : responses) {
            if (res.hasError()) {
                log.error("Received error={}", res.getError().getMessage());
            }
            String visionResponse = res.getFullTextAnnotation().getText();
            log.debug("Received response={}", visionResponse);
            return visionResponse;
        }
        return NO_RESPONSE_RETURNED;
    }

    private static AnnotateImageRequest buildImageRequest(Path receipt) throws IOException {
        return AnnotateImageRequest.newBuilder()
                .setImage(buildImage(receipt))
                .addFeatures(buildDescription())
                .build();
    }

    private static Feature buildDescription() {
        return Feature.newBuilder()
                .setType(Feature.Type.DOCUMENT_TEXT_DETECTION)
                .build();
    }

    private static Image buildImage(Path receipt) throws IOException {
        return Image.newBuilder()
                .setContent(ByteString.copyFrom(Files.readAllBytes(receipt)))
                .build();
    }

}
