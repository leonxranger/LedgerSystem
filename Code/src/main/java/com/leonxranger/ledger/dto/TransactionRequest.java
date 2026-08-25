package com.leonxranger.ledger.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TransactionRequest {
    private String description;
    private List<TransactionItem> itemlist;
}
