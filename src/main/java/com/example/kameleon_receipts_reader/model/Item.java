package com.example.kameleon_receipts_reader.model;

import java.math.BigDecimal;

public record Item(String name, int quantity, BigDecimal unitPrice, BigDecimal linePriceBeforeDiscount,
                   BigDecimal discount, BigDecimal linePrice, BigDecimal unitPricePaid) {
}
