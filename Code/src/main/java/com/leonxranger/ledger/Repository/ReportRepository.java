package com.leonxranger.ledger.Repository;

import com.leonxranger.ledger.Interface.AccountBalanceProjection;
import com.leonxranger.ledger.entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<TransactionItem,Long> {
    //get account balance by doing sum of amount for all transaction items for a specific amounts
    @Query(value = "SELECT a.code as Account_Code, a.name AS Account_Name,SUM(ti.amount) AS Balance " +
                    "FROM transaction_items ti JOIN Transactions t ON ti.transaction_id = t.id "+
                    "JOIN accounts a on a.id = ti.account_id "+
                    "WHERE t.date <= :targetDate GROUP BY a.code,a.name ",
    nativeQuery = true)
    List<AccountBalanceProjection> getBalanceSheet(@Param("targetDate") LocalDateTime targetDate);


    @Query(value = "SELECT a.code AS Account_Code, a.name as Account_Name,SUM(ti.amount) as Balance" +
            "FROM transaction_items ti JOIN Transactions t ON ti.transaction_id = t.id "+
            "JOIN accounts a on a.id = ti.account_id"+
            "WHERE t.date >= :StartDate AND t.date <= :EndDate GROUP BY a.code,a.name",
    nativeQuery = true)
    //income statement sums amount strictly between a startdate and an end date
    List<AccountBalanceProjection> getIncomeStatement(@Param("startDate") LocalDateTime StartDate,
                                                      @Param("EndDate")LocalDateTime EndDate);
}
