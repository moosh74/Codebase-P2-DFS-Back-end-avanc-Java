package com.openclassrooms.p2dfsbea.service;

import com.openclassrooms.p2dfsbea.model.Expense;
import com.openclassrooms.p2dfsbea.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {
    private ExpenseRepository repository;
    private ExpenseService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseRepository.class);
        service = new ExpenseService(repository);
    }

    @Test
    void calculatesTotalAndGroupsExpensesByCategoryAndMonth() {
        List<Expense> expenses = List.of(
            expense("2025-03-05", "Food", "20.00"),
            expense("2025-03-08", "food", "10.00"),
            expense("2025-04-02", "Transport", "15.00"));
            
        load(expenses);

        assertEquals(new BigDecimal("45.00"), service.calculateTotalExpenses());

        TreeMap<String, BigDecimal> byCategory = service.groupByCategory();
        assertEquals(2, byCategory.size());
        assertEquals(new BigDecimal("30.00"), byCategory.get("FOOD"));
        assertEquals(new BigDecimal("15.00"), byCategory.get("transport"));

        TreeMap<String, BigDecimal> byMonth = service.groupByMonth();
        assertEquals(2, byMonth.size());
        assertEquals(new BigDecimal("30.00"), byMonth.get(expenses.get(0).getMonthFullName()));
        assertEquals(new BigDecimal("15.00"), byMonth.get(expenses.get(2).getMonthFullName()));
    }

    @Test
    void calculatesAverageMonthlyExpensesRoundedToTwoDecimals() {
        load(List.of(
            expense("2025-03-05", "Food", "3.00"),
            expense("2025-04-02", "Food", "3.00"),
            expense("2025-05-03", "Transport", "4.00")));

        assertEquals(new BigDecimal("3.33"), service.calculateAverageMonthlyExpenses());
    }

    @Test
    void returnsZeroAverageAndFullIncomeSavingsWhenThereAreNoExpenses() {
        load(List.of());

        assertEquals(BigDecimal.ZERO, service.calculateAverageMonthlyExpenses());
        assertEquals(ExpenseService.MONTHLY_INCOME, service.calculateAvailableMonthlySavings());
    }

    @Test
    void calculatesAvailableSavingsAndCanExcludeHousingExpenses() {
        load(List.of(
            expense("2025-03-05", "Food", "100.00"),
            expense("2025-03-08", "Housing", "200.00")));

        assertEquals(new BigDecimal("1700.00"), service.calculateAvailableMonthlySavings());
        assertEquals(new BigDecimal("1900.00"), service.calculateAvailableMonthlySavings("housing"));
    }

    @Test
    void loadsAndReturnsRepositoryExpenses() {
        List<Expense> expenses = List.of(expense("2025-03-05", "Food", "12.50"));
        when(repository.readAll()).thenReturn(expenses);

        assertSame(expenses, service.loadAllExpenses());
    }

    @Test
    void delegatesExpenseAppendToRepository() throws IOException {
        Expense expense = expense("2025-03-05", "Food", "12.50");

        service.addExpense(expense);

        verify(repository).append(expense);
    }

    private void load(List<Expense> expenses) {
        when(repository.readAll()).thenReturn(expenses);
        service.loadAllExpenses();
    }

    private Expense expense(String date, String category, String amount) {
        return new Expense(LocalDate.parse(date), category, "Test expense", new BigDecimal(amount));
    }
}
