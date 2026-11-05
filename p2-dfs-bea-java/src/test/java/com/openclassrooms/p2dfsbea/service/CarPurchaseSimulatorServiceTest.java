package com.openclassrooms.p2dfsbea.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarPurchaseSimulatorServiceTest {
    private final CarPurchaseSimulatorService service = new CarPurchaseSimulatorService();

    @Test
    // Vérifie que le nombre de mois est arrondi au mois supérieur si nécessaire.
    void roundsUpToTheNextMonthWhenSavingsDoNotDividePriceExactly() {
        assertEquals(3, service.calculateMonthsToSave(new BigDecimal("1000"), new BigDecimal("400")));
    }

    @Test
    // Vérifie le résultat quand le prix est divisible exactement par l'épargne mensuelle.
    void returnsExactNumberOfMonthsWhenPriceDividesEvenly() {
        assertEquals(4, service.calculateMonthsToSave(new BigDecimal("1000"), new BigDecimal("250")));
    }

    @Test
    // Vérifie que le prix et l'épargne doivent être supérieurs à zéro.
    void rejectsNonPositiveCarPriceAndMonthlySavings() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateMonthsToSave(BigDecimal.ZERO, new BigDecimal("250")));
        assertThrows(IllegalArgumentException.class, () -> service.calculateMonthsToSave(new BigDecimal("1000"), BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> service.calculateMonthsToSave(new BigDecimal("-1"), new BigDecimal("250")));
        assertThrows(IllegalArgumentException.class, () -> service.calculateMonthsToSave(new BigDecimal("1000"), new BigDecimal("-1")));
    }
}
