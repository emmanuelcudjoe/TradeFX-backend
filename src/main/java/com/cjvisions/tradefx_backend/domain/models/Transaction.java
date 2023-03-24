package com.cjvisions.tradefx_backend.domain.models;


import com.cjvisions.tradefx_backend.domain.constants.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "provider")
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @OneToOne(targetEntity = Bank.class)
    private Bank bank;

    @OneToOne(targetEntity = UserRegistrationInfo.class)
    private UserRegistrationInfo user;

    private LocalDate transactionDate;

    private TransactionStatus status;
}
