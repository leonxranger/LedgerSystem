package com.leonxranger.ledger.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.logging.Logger;

@Slf4j
@Service
public class ReconciliationService {
    private static final Logger logger = Logger.getLogger(ReconciliationService.class.getName());
    private final BalancedServiceCache balancedServiceCache;
    private final ReportingService reportingService;

    ReconciliationService(BalancedServiceCache balancedServiceCache ,ReportingService reportingService){
        this.balancedServiceCache = balancedServiceCache;
        this.reportingService = reportingService;
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void reconcileBalances(){
        logger.info("Starting nightly Ledger reconciliation...");


        Set<String> activeKeys = balancedServiceCache.GetAllKeys();
        if(activeKeys.isEmpty()){
            logger.severe("Empty Cache! Please perform a transaction first");
            return;
        }


        for(String rediscode : activeKeys){

            if (!rediscode.contains(":")) {
                logger.warning("Found malformed Redis key, skipping: " + rediscode);
                continue;
            }


            String accountCode = rediscode.split(":")[1];

            BigDecimal accountBalance = reportingService.getBalance(accountCode);
            if(accountBalance == null){
                accountBalance = BigDecimal.ZERO;
            }

            BigDecimal cachedBalance = balancedServiceCache.getBalance(accountCode);

            if(cachedBalance != null && accountBalance.compareTo(cachedBalance) != 0){
                logger.severe(String.format("CACHE DRIFT DETECTED for account %s. DB: %s, Cache: %s",
                        accountCode, accountBalance, cachedBalance));

                balancedServiceCache.UpdateCache(accountCode , accountBalance);

            }

        }

        logger.info("Nightly ledger reconciliation complete.");
    }


}
