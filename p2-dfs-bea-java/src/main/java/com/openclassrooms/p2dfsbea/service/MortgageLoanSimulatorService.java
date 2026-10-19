package com.openclassrooms.p2dfsbea.service;

import java.math.BigDecimal;

public class MortgageLoanSimulatorService {
    private static final BigDecimal TAUX_ENDETTEMENT = new BigDecimal("0.35");

    public BigDecimal calculateMaximumMonthlyPayment(BigDecimal monthlyIncome, BigDecimal averageHousingExpenses, BigDecimal availableMonthlySavings) {
        BigDecimal debtRatioLimit = monthlyIncome.multiply(TAUX_ENDETTEMENT);
        BigDecimal remainingDebtCapacity = debtRatioLimit.subtract(averageHousingExpenses);
        
        return remainingDebtCapacity.min(availableMonthlySavings).max(BigDecimal.ZERO);
    }

}
