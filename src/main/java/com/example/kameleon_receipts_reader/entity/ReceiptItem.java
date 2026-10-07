package com.example.kameleon_receipts_reader.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ReceiptItem {

    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal linePriceBeforeDiscount;
    private BigDecimal discount;
    private BigDecimal linePrice;
    private BigDecimal unitPricePaid;

}
