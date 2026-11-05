package com.openclassrooms.p2dfsbea.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExpenseTest {

    @Test
    // Vérifie qu'une ligne CSV valide produit la dépense attendue.
    void parsesValidCsvLine() {
        Expense expense = Expense.fromCsvLine("05/03/2025,Food,Lunch,12.50");

        assertEquals(LocalDate.of(2025, 3, 5), expense.getDate());
        assertEquals("Food", expense.getCategory());
        assertEquals("Lunch", expense.getDescription());
        assertEquals(new BigDecimal("12.50"), expense.getAmount());
    }

    @Test
    // Vérifie que les lignes vides ou incorrectes sont ignorées.
    void ignoresBlankMalformedAndInvalidCsvLines() {
        assertNull(Expense.fromCsvLine(null));
        assertNull(Expense.fromCsvLine("  "));
        assertNull(Expense.fromCsvLine("date,category"));
        assertNull(Expense.fromCsvLine("31/02/2025,Food,Lunch,12.50"));
        assertNull(Expense.fromCsvLine("05/03/2025,Food,Lunch,not-a-number"));
    }

    @Test
    // Vérifie le format CSV, la date et l'arrondi du montant à deux décimales.
    void writesCsvLineWithExpectedDateAndTwoDecimalAmount() {
        Expense expense = new Expense(LocalDate.of(2025, 3, 5), "Food", "Lunch", new BigDecimal("12.345"));

        assertEquals("05/03/2025,Food,Lunch,12.35", expense.toCsvLine());
    }
}
