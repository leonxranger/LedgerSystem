package com.leonxranger.ledger.Controllers;

import com.leonxranger.ledger.Interface.AccountBalanceProjection;
import com.leonxranger.ledger.Services.ReportingService;
import jakarta.persistence.MappedSuperclass;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private ReportingService reportingService;

    ReportController(ReportingService reportingService){
        this.reportingService = reportingService;
    }

    @GetMapping("/balanced-sheet")
    public List<AccountBalanceProjection> GetBalancedSheet(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime Date
            ){
        return  reportingService.getBalanceSheet(Date);
    }

    @GetMapping("/Income-Statement")
    public List<AccountBalanceProjection> IncomeStatement(
            @RequestParam("startdate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime StartDate,
            @RequestParam("enddate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime EndDate

    ){
        return reportingService.getIncomeStatement(StartDate, EndDate);
    }
}
