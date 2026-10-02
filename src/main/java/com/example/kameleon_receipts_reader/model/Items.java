package com.example.kameleon_receipts_reader.model;

import java.util.List;

/**
 * Holder of receipt items (positions visible on a receipt)
 */
public record Items(List<Item> items) {
}
