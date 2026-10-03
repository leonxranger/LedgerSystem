package com.leonxranger.ledger.Repository;

import com.leonxranger.ledger.entity.TransactionItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransactionItemRepository  extends JpaRepository<TransactionItem , Long> {

    @Query(value = "SELECT SUM(ti.amount)"+
                    "FROM accounts a JOIN transaction_items ti " +
                    "ON ti.account_id = a.id " +
                    "WHERE a.code = :accountCode " , nativeQuery = true)
    BigDecimal GetSingleAccountBalance(@Param("accountCode") String accountCode);
}
