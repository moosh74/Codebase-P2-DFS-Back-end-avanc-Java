package com.openclassrooms.p2dfsbea.service;

import com.openclassrooms.p2dfsbea.model.Expense;
import com.openclassrooms.p2dfsbea.repository.ExpenseRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseService {
    public static final BigDecimal MONTHLY_INCOME = new BigDecimal("2000.00");

    private final ExpenseRepository repository;
    private List<Expense> expenses = new ArrayList<>();

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> loadAllExpenses() {
        expenses = repository.readAll();
        return expenses;
    }

    public void addExpense(Expense expense) throws IOException {
        repository.append(expense);
    }

    public BigDecimal calculateTotalExpenses() {
        return expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TreeMap<String, BigDecimal> groupByCategory() {
        return expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    public TreeMap<String, BigDecimal> groupByMonth() {
        Comparator<String> sortOrder = Comparator.comparing(
            (String label) -> YearMonth.parse(label, Expense.FORMATTER_MONTH));

        return expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getMonthFullName,
                () -> new TreeMap<String, BigDecimal>(sortOrder),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    public BigDecimal calculateAverageMonthlyExpenses() {
        TreeMap<String, BigDecimal> expensesByMonth = groupByMonth();

        if (expensesByMonth.isEmpty()) return BigDecimal.ZERO;

        BigDecimal totalExpenses = calculateTotalExpenses();

        return totalExpenses.divide(BigDecimal.valueOf(expensesByMonth.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAvailableMonthlySavings() {
        return MONTHLY_INCOME.subtract(calculateAverageMonthlyExpenses());
    }

    public BigDecimal calculateAverageMonthlyExpensesForCategory(String category) {
        int monthCount = groupByMonth().size();

        if (monthCount == 0) return BigDecimal.ZERO;

        BigDecimal categoryTotal = groupByCategory().getOrDefault(category, BigDecimal.ZERO);
       
        return categoryTotal.divide(BigDecimal.valueOf(monthCount), 2, RoundingMode.HALF_UP);
    }
}
