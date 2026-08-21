package com.leonxranger.ledger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "transaction_seq_gen")
    @SequenceGenerator(name = "transaction_seq_gen" , sequenceName = "transaction_seq" ,allocationSize = 50)
    private long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "transaction" ,cascade = CascadeType.ALL)
    private List<TransactionItem> items;
}
