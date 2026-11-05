package com.openclassrooms.p2dfsbea.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MortgageLoanSimulatorService {
    private static final BigDecimal TAUX_ENDETTEMENT = new BigDecimal("0.35");
    private static final MathContext PRECISION = new MathContext(20);

    // Calcule la mensualité maximale possible selon le revenu, le plafond d'endettement et l'épargne disponible.
    public BigDecimal calculateMaximumMonthlyPayment(BigDecimal monthlyIncome, BigDecimal availableMonthlySavings) {
        BigDecimal debtRatioLimit = monthlyIncome.multiply(TAUX_ENDETTEMENT);

        return debtRatioLimit.min(availableMonthlySavings).max(BigDecimal.ZERO);
    }

    // Estime le capital empruntable selon la mensualité, le taux annuel en pourcentage et la durée en années.
    public BigDecimal calculateBorrowingCapacity(BigDecimal monthlyPayment, BigDecimal annualRatePercent, int durationYears) {
        if (monthlyPayment == null || monthlyPayment.signum() <= 0) {
            throw new IllegalArgumentException("La mensualité doit être supérieure à zéro.");
        }
        if (annualRatePercent == null || annualRatePercent.signum() < 0) {
            throw new IllegalArgumentException("Le taux annuel doit être positif ou nul.");
        }
        if (durationYears <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à zéro.");
        }

        int numberOfPayments = Math.multiplyExact(durationYears, 12);

        if (annualRatePercent.signum() == 0) {
            return monthlyPayment.multiply(BigDecimal.valueOf(numberOfPayments)).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRatePercent.divide(new BigDecimal("100"), PRECISION).divide(new BigDecimal("12"), PRECISION);
        BigDecimal discountFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(monthlyRate, PRECISION).pow(numberOfPayments, PRECISION), PRECISION);
        BigDecimal principal = monthlyPayment.multiply(BigDecimal.ONE.subtract(discountFactor, PRECISION), PRECISION).divide(monthlyRate, PRECISION);

        return principal.setScale(2, RoundingMode.HALF_UP);
    }

}
