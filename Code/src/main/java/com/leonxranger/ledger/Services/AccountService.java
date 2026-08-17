package com.leonxranger.ledger.Services;
import com.leonxranger.ledger.Repository.AccountRepository;
import com.leonxranger.ledger.entity.Accounts;
import com.leonxranger.ledger.entity.AccountType;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Accounts createAccount(String code , String name , AccountType type){
        Accounts newAccount = new Accounts(0L,code,name,type);

        return accountRepository.save(newAccount);
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
