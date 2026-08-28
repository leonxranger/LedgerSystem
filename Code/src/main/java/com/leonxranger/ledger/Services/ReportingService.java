package com.leonxranger.ledger.Services;

import com.leonxranger.ledger.Interface.AccountBalanceProjection;
import com.leonxranger.ledger.Repository.ReportRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReportingService {
    private final ReportRepository reportRepository;

    public ReportingService(ReportRepository reportRepository){
        this.reportRepository =reportRepository;
    }

    public List<AccountBalanceProjection> getBalanceSheet(LocalDateTime Date){
        return reportRepository.getBalanceSheet(Date);
    }

    public List<AccountBalanceProjection> getIncomeStatement(LocalDateTime StartDate , LocalDateTime EndDate){
       return  reportRepository.getIncomeStatement(StartDate,EndDate);
    };

}
