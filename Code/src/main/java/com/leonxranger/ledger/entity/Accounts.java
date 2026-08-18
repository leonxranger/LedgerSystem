package com.leonxranger.ledger.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_gen")
    @SequenceGenerator(name = "account_seq_gen", sequenceName = "accounts_seq", allocationSize = 50)
    long id;

    @Column(unique = true,nullable = false)//schema constraint to set when creating the table in sql
    String code;

    @Column(nullable = false)
    String Name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) //EQUITY -> 'EQUITY' NOT EQUITY->0,1,2...
    AccountType Type;

}
