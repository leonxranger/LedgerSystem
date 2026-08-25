package com.leonxranger.ledger.Services;

import com.leonxranger.ledger.Repository.AccountRepository;
import com.leonxranger.ledger.Repository.TransactionRepository;
import com.leonxranger.ledger.dto.Response_DTO.TransactionItemResponse;
import com.leonxranger.ledger.dto.Response_DTO.TransactionResponse;
import com.leonxranger.ledger.dto.TransactionRequest;
import com.leonxranger.ledger.entity.Accounts;
import com.leonxranger.ledger.entity.TransactionItem;
import com.leonxranger.ledger.entity.Transactions;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class TransactionService {


    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    //constructor
      public TransactionService(AccountRepository accountRepository , TransactionRepository transactionRepository){
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
    public Transactions recordTransaction(TransactionRequest request){
        try{
            List<TransactionItem> items = new ArrayList<>();
            for(var dtoItem : request.getItemlist()){
                Accounts hollowAccount  = new Accounts();
                hollowAccount.setCode(dtoItem.getAccountCode());

                TransactionItem item = new TransactionItem();
                item.setAccount(hollowAccount);
                item.setAmount(dtoItem.getAmount());

                items.add(item);

            }


            ValidateTransaction(items);

            for(TransactionItem item : items) {
                Accounts lockedAccount = accountRepository.findByCodeForUpdate(item.getAccount().getCode()).orElseThrow(() -> new RuntimeException("Account not Found"));

                item.setAccount(lockedAccount);
            }

            Transactions newtransaction = new Transactions();
            newtransaction.setDate(java.time.LocalDateTime.now());

            for(TransactionItem item : items){
                item.setTransaction(newtransaction);
            }

            newtransaction.setItems(items);

            return transactionRepository.save(newtransaction);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionResponse mapTodO(Transactions transaction){
          TransactionResponse response = new TransactionResponse();
          response.setDate(transaction.getDate());
          response.setDescription(transaction.getDescription());
          response.setId(transaction.getId());

          List<TransactionItemResponse> itemResponses = transaction.getItems().stream().map(item ->{
              TransactionItemResponse  itemresp = new TransactionItemResponse();
              itemresp.setId(item.getId());
              itemresp.setAccountCode(item.getAccount().getCode());
              itemresp.setAmount(item.getAmount());

              return  itemresp;
          }).toList();

          response.setItems(itemResponses);
          return response;

    }


}
