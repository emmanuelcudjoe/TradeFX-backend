package com.cjvisions.tradefx_backend.repositories;

import com.cjvisions.tradefx_backend.domain.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<Transaction> findById(String transactionId);
}
