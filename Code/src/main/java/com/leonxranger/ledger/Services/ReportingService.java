package com.leonxranger.ledger.Services;

import com.leonxranger.ledger.Interface.AccountBalanceProjection;
import com.leonxranger.ledger.Repository.ReportRepository;
import com.leonxranger.ledger.Repository.TransactionItemRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportingService {
    private final ReportRepository reportRepository;
    private final TransactionItemRepository ti_repository;
    private final BalancedServiceCache BalanceCache;
    public ReportingService(ReportRepository reportRepository , TransactionItemRepository ti_repository ,BalancedServiceCache BalanceCache){
        this.reportRepository =reportRepository;
        this.ti_repository = ti_repository;
        this.BalanceCache = BalanceCache;

    }

    private void Bala() {
    }

    public List<AccountBalanceProjection> getBalanceSheet(LocalDateTime Date){
        return reportRepository.getBalanceSheet(Date);
    }

    public List<AccountBalanceProjection> getIncomeStatement(LocalDateTime StartDate , LocalDateTime EndDate){
       return  reportRepository.getIncomeStatement(StartDate,EndDate);
    };


    public BigDecimal getBalance(String AccountCode){
        BigDecimal CachedBalance = BalanceCache.getBalance(AccountCode);

        if(CachedBalance != null){
            return CachedBalance;
        }
            BigDecimal db_balance =  ti_repository.GetSingleAccountBalance(AccountCode);

            if(db_balance != null){
                BalanceCache.UpdateCache(AccountCode,db_balance);
            }

            return db_balance;
    }

}
