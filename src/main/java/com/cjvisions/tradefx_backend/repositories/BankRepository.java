package com.cjvisions.tradefx_backend.repositories;

import com.cjvisions.tradefx_backend.domain.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {

    Bank findByContact(String contact);

    Bank findByAccountName(String accountName);

    Bank findByAccountNumber(String accountNumber);
}
