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

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> loadAllExpenses() {
        return repository.readAll();
    }

    public void addExpense(Expense expense) throws IOException {
        repository.append(expense);
    }

    public BigDecimal calculateTotalExpenses() {
        return loadAllExpenses().stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TreeMap<String, BigDecimal> groupByCategory() {
        return loadAllExpenses().stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    public TreeMap<String, BigDecimal> groupByMonth() {
        Comparator<String> sortOrder = Comparator.comparing(
            (String label) -> YearMonth.parse(label, Expense.FORMATTER_MONTH));

        return loadAllExpenses().stream()
            .collect(Collectors.groupingBy(
                Expense::getMonthFullName,
                () -> new TreeMap<String, BigDecimal>(sortOrder),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    public BigDecimal calculateAverageMonthlyExpenses() {
        Map<YearMonth, BigDecimal> expensesByMonth = loadAllExpenses().stream()
            .collect(Collectors.groupingBy(
                expense -> YearMonth.from(expense.getDate()),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        if (expensesByMonth.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalExpenses = expensesByMonth.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        return totalExpenses.divide(BigDecimal.valueOf(expensesByMonth.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAvailableMonthlySavings() {
        return MONTHLY_INCOME.subtract(calculateAverageMonthlyExpenses());
    }
}
