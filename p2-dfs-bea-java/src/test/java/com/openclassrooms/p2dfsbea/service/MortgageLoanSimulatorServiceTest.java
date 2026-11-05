package com.openclassrooms.p2dfsbea.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MortgageLoanSimulatorServiceTest {
    private final MortgageLoanSimulatorService service = new MortgageLoanSimulatorService();

    @Test
    // Vérifie le plafond de 35 % et sa limite par l'épargne disponible.
    void maximumMonthlyPaymentIsLimitedByThirtyFivePercentAndAvailableSavings() {
        assertEquals(new BigDecimal("500.00"), service.calculateMaximumMonthlyPayment(new BigDecimal("2000.00"), new BigDecimal("500.00")));
        assertEquals(new BigDecimal("700.0000"), service.calculateMaximumMonthlyPayment(new BigDecimal("2000.00"), new BigDecimal("900.00")));
    }

    @Test
    // Vérifie que la mensualité maximale ne peut pas être négative.
    void maximumMonthlyPaymentCannotBeNegative() {
        assertEquals(BigDecimal.ZERO, service.calculateMaximumMonthlyPayment(new BigDecimal("2000.00"), new BigDecimal("-100.00")));
    }

    @Test
    // Vérifie le calcul du capital quand le taux d'intérêt est nul.
    void borrowingCapacityAtZeroRateIsPaymentTimesNumberOfMonths() {
        assertEquals(new BigDecimal("240000.00"), service.calculateBorrowingCapacity(new BigDecimal("1000"), BigDecimal.ZERO, 20));
    }

    @Test
    // Vérifie le calcul du capital avec un taux annuel et une durée en années.
    void borrowingCapacityUsesAnnualPercentageRateAndDurationInYears() {
        BigDecimal capacity = service.calculateBorrowingCapacity(new BigDecimal("1000"), new BigDecimal("4"), 20);

        assertTrue(capacity.subtract(new BigDecimal("165021.86")).abs().compareTo(new BigDecimal("0.02")) <= 0, "Actual capacity: " + capacity);
    }

    @Test
    // Vérifie que les valeurs invalides sont refusées par le calcul du prêt.
    void rejectsInvalidPaymentRateAndDuration() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateBorrowingCapacity(BigDecimal.ZERO, new BigDecimal("4"), 20));
        assertThrows(IllegalArgumentException.class, () -> service.calculateBorrowingCapacity(new BigDecimal("1000"), new BigDecimal("-1"), 20));
        assertThrows(IllegalArgumentException.class, () -> service.calculateBorrowingCapacity(new BigDecimal("1000"), new BigDecimal("4"), 0));
    }
}
