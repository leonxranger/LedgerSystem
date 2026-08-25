package com.leonxranger.ledger.dto.Response_DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private LocalDateTime date;
    private String description;
    private List<TransactionItemResponse> items;
}
