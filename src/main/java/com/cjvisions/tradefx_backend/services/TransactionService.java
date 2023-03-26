package com.cjvisions.tradefx_backend.services;

import com.cjvisions.tradefx_backend.domain.constants.TransactionStatus;
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
import java.util.List;

@Service
public class TransactionService {

    private final int RATE = 10;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Transactional
    public Transaction saveTransaction(UserTransactionDTO transaction){

        var existingUser = userRegistrationRepository.findUserByEmail(transaction.userEmail());
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

        var newTransaction = new Transaction();
        newTransaction.setUser(existingUser);
        newTransaction.setAmount(transaction.amount() * RATE);
        newTransaction.setBank(bank);
        newTransaction.setCreatedAt(LocalDate.now());
        newTransaction.setUpdatedAt(LocalDate.now());
        newTransaction.setStatus(TransactionStatus.PENDING);
        newTransaction.setProvider(transaction.providerName());

        var savedTransaction = transactionRepository.save(newTransaction);
        if (savedTransaction != null){
            new UserSMSFeedback().sendMessageToUser(transaction.contact(),
                    "Your request has been received successfully. You will soon be notified about further developments");
            return savedTransaction;
        } else {
            new UserSMSFeedback().sendMessageToUser(transaction.contact(),
                    "Sorry, order could not be placed. Please wait while our technical team resolve the issue");
        }
        return null;
    }

    private Bank saveBank(Bank bank, UserTransactionDTO transaction, UserRegistrationInfo existingUser) {

        bank.setBankName(transaction.bank());
        bank.setAccountName(transaction.accountName());
        bank.setContact(transaction.contact());
        bank.setAccountNumber(transaction.accountNumber());
        bank.setUser(existingUser);
        bank.setBranch(transaction.branchName());
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
                    transaction.getCreatedAt());
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
                new UserSMSFeedback().sendMessageToUser("+233504809836", "Transaction fulfilled. Account has been debited.");
                new UserSMSFeedback().sendMessageToUser("+233504809836",
                        "From "+existingTransaction.getBank().getBankName() + ". An amount of $40 has been credited to your account");
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
}
