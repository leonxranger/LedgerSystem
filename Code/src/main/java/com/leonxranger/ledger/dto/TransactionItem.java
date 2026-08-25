package com.leonxranger.ledger.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class TransactionItem {
    private String accountCode;
    private BigDecimal amount;
}
