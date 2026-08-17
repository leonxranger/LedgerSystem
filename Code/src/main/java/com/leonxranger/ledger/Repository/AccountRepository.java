package com.leonxranger.ledger.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.leonxranger.ledger.entity.Accounts;
import java.util.Optional;
public interface AccountRepository extends JpaRepository<Accounts , Long> {
    Optional<Accounts> findByCode(String code);
}
