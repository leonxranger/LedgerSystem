package com.leonxranger.ledger.Repository;

import com.leonxranger.ledger.entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionItemRepository  extends JpaRepository<TransactionItem , Long> {
}
