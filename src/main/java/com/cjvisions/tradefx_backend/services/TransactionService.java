package com.cjvisions.tradefx_backend.services;

import com.cjvisions.tradefx_backend.domain.constants.TransactionStatus;
import com.cjvisions.tradefx_backend.domain.dto.TransactionResponse;
import com.cjvisions.tradefx_backend.domain.dto.UpdateTransactionStatusDTO;
import com.cjvisions.tradefx_backend.domain.dto.UserTransactionDTO;
import com.cjvisions.tradefx_backend.domain.dto.UserTransactionHistoryDTO;
import com.cjvisions.tradefx_backend.domain.models.Bank;
import com.cjvisions.tradefx_backend.domain.models.Provider;
import com.cjvisions.tradefx_backend.domain.models.Transaction;
import com.cjvisions.tradefx_backend.domain.models.UserRegistrationInfo;
import com.cjvisions.tradefx_backend.repositories.BankRepository;
import com.cjvisions.tradefx_backend.repositories.ProviderRepository;
import com.cjvisions.tradefx_backend.repositories.TransactionRepository;
import com.cjvisions.tradefx_backend.repositories.UserRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final Double RATE = 10d;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Transactional
    public TransactionResponse saveTransaction(UserTransactionDTO transaction){

        Double amount = transaction.amount() / RATE;

        var existingUser = userRegistrationRepository.findByEmail(transaction.userEmail());
        var existingBank = bankRepository.findByAccountNumber(transaction.accountNumber());
//        Provider provider;
        Bank bank = new Bank();
        if (existingUser != null && existingBank == null){
            System.out.println("Bank does not exist");
            bank = saveBank(bank, transaction, existingUser);
            existingUser.addBank(bank);
            existingUser = userRegistrationRepository.save(existingUser);
        } else if (existingUser != null && existingBank != null){
            bank = existingBank;
        }

        if (amount > bank.getBalance()){
            return new TransactionResponse(400, "TRANSACTION ERROR!. The request amount exceeds your current balance in your account.");
        }

        if (amount > bank.getTransactionLimit()){
            return new TransactionResponse(400, "TRANSACTION ERROR!. The request amount exceeds the daily limit.");
        }

        bank.setBalance(bank.getBalance() - amount);
        bank = bankRepository.save(bank);

        var newTransaction = new Transaction();
        newTransaction.setUser(existingUser);

        newTransaction.setAmount(amount);
        newTransaction.setBank(bank);
        newTransaction.setCreatedAt(LocalDate.now());
        newTransaction.setUpdatedAt(LocalDate.now());
        newTransaction.setStatus(TransactionStatus.PENDING);
        newTransaction.setProvider(transaction.providerName());

        var savedTransaction = transactionRepository.save(newTransaction);
        if (savedTransaction != null){
            new UserSMSFeedback().sendMessageToUser(transaction.contact(),
                    "Your request has been received successfully. You will soon be notified about further developments");
            return new TransactionResponse(202, "Your request has been received successfully. You will soon be notified by text about further developments");

        }
        new UserSMSFeedback().sendMessageToUser(transaction.contact(),
                    "Sorry, request could not be processed. Please try again later while our technical team resolve the issue");
        return new TransactionResponse(400, "TRANSACTION ERROR!. Sorry, request could not be processed. Please try again later while our technical team resolve the issue");
    }

    private Bank saveBank(Bank bank, UserTransactionDTO transaction, UserRegistrationInfo existingUser) {

        bank.setBankName(transaction.bank());
        bank.setAccountName(transaction.accountName());
        bank.setContact(transaction.contact());
        bank.setAccountNumber(transaction.accountNumber());
        bank.setUser(existingUser);
        bank.setBranch(transaction.branchName());
        bank.setBalance(300000.00);
        bank.setTransactionLimit(5000.00);
        return bank;

    }

    private Provider findProviderByName(String name){
       return providerRepository.findByName(name);
    }

    public List<UserTransactionHistoryDTO> getAllTransactions() {

        return transactionRepository.findAll().stream().map(transaction -> {
            return new UserTransactionHistoryDTO(
                    transaction.getId(),
                    transaction.getProvider(),
                    transaction.getBank().getBankName(),
                    transaction.getStatus().toString(),
                    transaction.getCreatedAt(),
                    transaction.getAmount());
        }).toList();
    }

    public Transaction updateTransactionStatus(String transactionId, UpdateTransactionStatusDTO status) {
        var transaction = transactionRepository.findById(transactionId);
        Transaction existingTransaction = new Transaction();

        if (transaction.isPresent()){
           existingTransaction = transaction.get();
        } else {
            return null;
        }

        switch (status.status()) {
            case "fulfilled":
                existingTransaction.setStatus(TransactionStatus.FULFILLED);
                new UserSMSFeedback().sendMessageToUser("+233504809836",
                        "Transaction fulfilled. Account has been debited.");
                new UserSMSFeedback().sendMessageToUser("+233504809836",
                        "From "+existingTransaction.getBank().getBankName() +
                                ". An amount of $40 has been credited to your account");
                break ;
            case "pending":
                existingTransaction.setStatus(TransactionStatus.PENDING);
                break;
            case "failed":
                new UserSMSFeedback().sendMessageToUser("+233504809836",
                        "Transaction failed. We are unable to process transactions at this time. Please try again later");
                existingTransaction.setStatus(TransactionStatus.FAILED);
                break;
            default:
                new UserSMSFeedback().sendMessageToUser("+233504809836",
                        "Transaction failed. Please wait whiles our engineers resolve the problem");
                existingTransaction.setStatus(TransactionStatus.FAILED);
                break;

        }

        return transactionRepository.save(existingTransaction);

    }

    public List<UserTransactionHistoryDTO> getAllUserTransactions(String userId) {
        List<UserTransactionHistoryDTO> userTransactions = new ArrayList<>();
        var transactions = transactionRepository.findAll();

        if (transactions.size() > 0){
            userTransactions = transactions
                    .stream()
                    .filter(transaction -> transaction.getUser().getId().equals(userId))
                    .map(transaction -> new UserTransactionHistoryDTO(
                            transaction.getId(),
                            transaction.getProvider(),
                            transaction.getBank().getBankName(),
                            transaction.getStatus().toString(),
                            transaction.getCreatedAt(),
                            transaction.getAmount()
                    )).toList();
        }

        return userTransactions;
    }
}
