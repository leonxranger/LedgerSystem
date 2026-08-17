package com.leonxranger.ledger.Controllers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.leonxranger.ledger.entity.Accounts;
import com.leonxranger.ledger.Services.AccountService;
import com.leonxranger.ledger.entity.AccountType;
@RestController("/accounts")
public class AccountController {
    AccountService accountService;

    AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping("/")
    Accounts addAccount(String Code , String name ,  AccountType type){
        return accountService.createAccount(Code,name,type);
    }


    @GetMapping("/")
    public Page<Accounts> getAccount(
            @PathVariable String Code,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "50") int pageSize){
        return accountService.getAccounts(pageNumber ,pageSize);
    }

    @GetMapping("/{code}")
    public Accounts getAccountByCode(
            @PathVariable String code
    ){
        return accountService.getAccountbyID(code);

    }


}
