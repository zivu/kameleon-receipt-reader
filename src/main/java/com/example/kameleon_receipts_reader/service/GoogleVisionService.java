package com.example.kameleon_receipts_reader.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleVisionService {

    @SneakyThrows
    String process(MultipartFile receipt) {
        try(ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(receipt.getBytes());
            List<AnnotateImageRequest> request = new ArrayList<>();
            Image image = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest imageRequest = AnnotateImageRequest.newBuilder().setImage(image).addFeatures(feature).build();
            request.add(imageRequest);
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(request);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            for (var res : responses) {
                if (res.hasError()) {
                    log.error("Received error={}", res.getError().getMessage());
                }
                String visionResponse = res.getFullTextAnnotation().getText();
                log.debug("Received response: {}", visionResponse);
                return visionResponse;
            }
            return "";
        }
    }

}
