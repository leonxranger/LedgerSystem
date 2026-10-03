package com.leonxranger.ledger.dto;
import lombok.Getter;
import lombok.Setter;
import com.leonxranger.ledger.entity.AccountType;
@Getter
@Setter
public class  AccountRequest {
    private String name;
    AccountType type;
}
