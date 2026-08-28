package com.leonxranger.ledger.Interface;

import java.math.BigDecimal;

public interface AccountBalanceProjection {
     String getAccount_Code();
     String getAccount_Name();
     BigDecimal getBalance();
}
