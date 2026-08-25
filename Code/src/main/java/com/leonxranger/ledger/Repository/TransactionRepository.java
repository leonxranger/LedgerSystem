package com.leonxranger.ledger.Repository;

import com.leonxranger.ledger.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions,Long> {
}
