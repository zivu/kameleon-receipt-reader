package com.example.kameleon_receipts_reader.model;

import java.math.BigDecimal;

/**
 * Separate position on a receipt.
 * @param name of an ordered item.
 * @param quantity how many times an item ordered.
 * @param unitPricePaid the effective price actually paid for one unit after discount
 */
public record Item(String name, int quantity, BigDecimal unitPricePaid) {
}
