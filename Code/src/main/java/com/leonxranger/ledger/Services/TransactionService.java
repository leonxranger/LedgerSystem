package com.leonxranger.ledger.Services;

import com.leonxranger.ledger.Repository.AccountRepository;
import com.leonxranger.ledger.Repository.TransactionRepository;
import com.leonxranger.ledger.entity.Accounts;
import com.leonxranger.ledger.entity.TransactionItem;
import com.leonxranger.ledger.entity.Transactions;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;


@Service
public class TransactionService {


    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    //constructor
      public TransactionService(AccountRepository accountRepository ,Transactions transaction , TransactionRepository transactionRepository){
          this.accountRepository = accountRepository;
          this.transactionRepository = transactionRepository;
      }

    private void ValidateTransaction(List<TransactionItem> items){
        BigDecimal total = BigDecimal.ZERO;

        for(TransactionItem transactionItem: items){
            total =total.add(transactionItem.getAmount());
        }

        if (total.compareTo(total.ZERO) != 0){
            throw  new RuntimeException("Transaction is unbalanced! Off by:" + total);
        }
    }

    @Transactional
    public Transactions recordTransaction(List<TransactionItem> items , String Description){
        try{
            ValidateTransaction(items);

            for(TransactionItem item : items) {
                Accounts lockedAccount = accountRepository.findByCodeForUpdate(item.getAccount().getCode()).orElseThrow(() -> new RuntimeException("Account not Found"));

                item.setAccount(lockedAccount);
            }

            Transactions newtransaction = new Transactions();
            newtransaction.setDate(java.time.LocalDateTime.now());

            for(TransactionItem item : items){
                item.setTransactions(newtransaction);
            }

            newtransaction.setItems(items);

            return transactionRepository.save(newtransaction);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
