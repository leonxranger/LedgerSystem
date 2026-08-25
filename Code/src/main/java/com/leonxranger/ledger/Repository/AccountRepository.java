package com.leonxranger.ledger.Repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.leonxranger.ledger.entity.Accounts;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface AccountRepository extends JpaRepository<Accounts , Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Accounts a WHERE a.code = :code ")
    Optional<Accounts>findByCodeForUpdate(@Param("code") String code);

    Optional<Accounts> findByCode(String code);
}


