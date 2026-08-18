package com.leonxranger.ledger.Services;

import com.leonxranger.ledger.Repository.AccountRepository;
import com.leonxranger.ledger.entity.AccountType;
import com.leonxranger.ledger.entity.Accounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Accounts createAccount( String name , AccountType type){


        try{
            String code = "ACC-"+ UUID.randomUUID().toString();

            Accounts newAccount = new Accounts(0L,code,name,type);

            return accountRepository.save(newAccount);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Page<Accounts> getAccounts(int pageNumber, int pageSize){
        //creates a request for a specific page(eg-0,eg-50)
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        // Spring automatically adds "LIMIT 50 OFFSET 0" to the SQL query!
        return accountRepository.findAll(pageable);
    }

    public  Accounts getAccountbyID(String Code){
        return accountRepository.findByCode(Code).orElseThrow(() -> new RuntimeException("Account not found for code: " + Code));
    }


}
