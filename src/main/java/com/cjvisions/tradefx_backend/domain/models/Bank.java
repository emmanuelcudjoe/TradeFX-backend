package com.cjvisions.tradefx_backend.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "contact")
    private String contact;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL,  targetEntity = UserRegistrationInfo.class)
    @JoinColumn(name = "user_accounts")
    private UserRegistrationInfo userRegistrationInfo;

}
