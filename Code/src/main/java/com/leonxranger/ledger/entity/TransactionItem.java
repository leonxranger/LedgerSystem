package com.leonxranger.ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.leonxranger.ledger.entity.Transactions;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "transaction_items_seq")
    @SequenceGenerator(name = "transaction_items_seq" , sequenceName = "transaction_items_seq")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id" , nullable = false)
    private Transactions transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",nullable = false)
    private Accounts Account;

    private BigDecimal amount;

}
