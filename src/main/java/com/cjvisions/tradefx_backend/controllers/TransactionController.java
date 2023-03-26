package com.cjvisions.tradefx_backend.controllers;

import com.cjvisions.tradefx_backend.domain.dto.UpdateTransactionStatusDTO;
import com.cjvisions.tradefx_backend.domain.dto.UserTransactionDTO;
import com.cjvisions.tradefx_backend.domain.dto.UserTransactionHistoryDTO;
import com.cjvisions.tradefx_backend.domain.models.Transaction;
import com.cjvisions.tradefx_backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;



    @PostMapping("/buy-fx")
    public void saveTransaction(@RequestBody UserTransactionDTO userTransaction){
        System.out.println("Transaction " + userTransaction);
        transactionService.saveTransaction(userTransaction);
    }

    @GetMapping("/get-all-transactions")
    public List<UserTransactionHistoryDTO> getAllTransactions(){
        return transactionService.getAllTransactions();
    }

    @PostMapping("/transactions/{transactionId}")
    public Transaction updateTransactionStatus(@PathVariable String transactionId, @RequestBody UpdateTransactionStatusDTO status){
        System.out.println(status);
        return transactionService.updateTransactionStatus(transactionId, status);
    }
}
