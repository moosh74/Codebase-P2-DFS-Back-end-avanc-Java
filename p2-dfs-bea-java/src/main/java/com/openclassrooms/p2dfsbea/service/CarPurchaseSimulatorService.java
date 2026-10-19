package com.openclassrooms.p2dfsbea.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CarPurchaseSimulatorService {
    public int calculateMonthsToSave(BigDecimal carPrice, BigDecimal monthlySavings) {
        if (carPrice.signum() <= 0) {
            throw new IllegalArgumentException("Le prix de la voiture doit être supérieur à zéro.");
        }
        if (monthlySavings.signum() <= 0) {
            throw new IllegalArgumentException("L'épargne mensuelle doit être supérieure à zéro.");
        }

        return carPrice.divide(monthlySavings, 0, RoundingMode.UP).intValueExact();
    }
}
