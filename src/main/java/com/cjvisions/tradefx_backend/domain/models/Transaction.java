package com.cjvisions.tradefx_backend.domain.models;

import com.cjvisions.tradefx_backend.domain.constants.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @JsonIgnore
    @OneToOne(targetEntity = Bank.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @JsonIgnore
    @OneToOne(targetEntity = UserRegistrationInfo.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserRegistrationInfo user;

//    @OneToOne(targetEntity = Provider.class)
//    @JoinColumn(name = "provider_id")
//    private Provider provider;

    @Column(name = "provider")
    private String provider;

    @Column(name = "createdAt")
    private LocalDate createdAt;

    @Column(name = "updatedAt")
    private LocalDate updatedAt;

    @Column(name = "buying_amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
