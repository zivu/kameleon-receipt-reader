package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.model.Items;

/**
 * Bridge used by underlying service which analyse receipt text and builds json describing name and a price.
 */
public interface AnalysisService {

    /**
     * Analyses receipt text.
     * @param textToBeAnalysed text retrieved from receipt image.
     * @return analysed receipt.
     */
    Items analyse(String textToBeAnalysed);

}
