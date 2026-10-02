package com.example.kameleon_receipts_reader.model;

import java.math.BigDecimal;

/**
 * Separate position on a receipt.
 * @param name of an ordered item.
 * @param quantity how many times an item ordered.
 * @param unitPrice the original price of one unit before any discount
 * @param linePriceBeforeDiscount quantity × original unit price.
 * @param discount received.
 * @param linePrice the final amount actually paid for this line after discount.
 * @param unitPricePaid the effective price actually paid for one unit after discount
 */
public record Item(String name, int quantity, BigDecimal unitPrice, BigDecimal linePriceBeforeDiscount,
                   BigDecimal discount, BigDecimal linePrice, BigDecimal unitPricePaid) {
}
