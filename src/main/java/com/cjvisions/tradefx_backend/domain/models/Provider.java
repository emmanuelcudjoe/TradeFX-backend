package com.cjvisions.tradefx_backend.domain.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "provider")
public class Provider {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "currencies")
    @OneToMany(targetEntity = Currency.class, orphanRemoval = false, cascade = CascadeType.DETACH)
    private Set<Currency> currencies;
}
