package com.leonxranger.ledger.dto.Response_DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransactionItemResponse {
    private Long id;
    private String accountCode;
    private BigDecimal amount;

}
