package com.leonxranger.ledger.Controllers;

import com.leonxranger.ledger.Services.TransactionService;
import com.leonxranger.ledger.dto.Response_DTO.TransactionResponse;
import com.leonxranger.ledger.dto.TransactionRequest;
import com.leonxranger.ledger.entity.Accounts;
import com.leonxranger.ledger.entity.TransactionItem;
import com.leonxranger.ledger.entity.Transactions;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    TransactionService transactionService;
    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }



    @PostMapping
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request){

        List<TransactionItem> entityItems = new ArrayList<>();
            for(var dtoItem : request.getItemlist()){
                Accounts hollowAccount  = new Accounts();
                hollowAccount.setCode(dtoItem.getAccountCode());

                TransactionItem item = new TransactionItem();
                item.setAccount(hollowAccount);
                item.setAmount(dtoItem.getAmount());

                entityItems.add(item);

            }
            Transactions savedTransaction =  transactionService.recordTransaction(request );
            return  transactionService.mapTodO(savedTransaction);

    }
}
