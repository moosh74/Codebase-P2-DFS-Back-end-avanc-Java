package com.openclassrooms.p2dfsbea.service;

import com.openclassrooms.p2dfsbea.model.Expense;
import com.openclassrooms.p2dfsbea.repository.ExpenseRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
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

    // Calcule la somme de toutes les dépenses chargées.
    public BigDecimal calculateTotalExpenses() {
        return expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calcule le total des dépenses pour chaque catégorie.
    public TreeMap<String, BigDecimal> groupByCategory() {
        return expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    // Calcule le total des dépenses pour chaque mois.
    public TreeMap<String, BigDecimal> groupByMonth() {
        Comparator<String> sortOrder = Comparator.comparing(
            (String label) -> YearMonth.parse(label, Expense.FORMATTER_MONTH));

        return expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getMonthFullName,
                () -> new TreeMap<String, BigDecimal>(sortOrder),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    // Calcule la dépense moyenne par mois distinct présent dans l'historique.
    public BigDecimal calculateAverageMonthlyExpenses() {
        int monthCount = groupByMonth().size();

        if (monthCount == 0) return BigDecimal.ZERO;

        BigDecimal totalExpenses = calculateTotalExpenses();

        return totalExpenses.divide(BigDecimal.valueOf(monthCount), 2, RoundingMode.HALF_UP);
    }

    // Calcule l'épargne mensuelle disponible sans exclure de catégorie.
    public BigDecimal calculateAvailableMonthlySavings() {
        return calculateAvailableMonthlySavings("");
    }

    // Calcule l'épargne mensuelle disponible en ignorant la catégorie indiquée.
    public BigDecimal calculateAvailableMonthlySavings(String excludedCategory) {
        int monthCount = groupByMonth().size();

        if (monthCount == 0) return MONTHLY_INCOME;

        BigDecimal totalExpenses = calculateTotalExpenses();

        if (excludedCategory != null && !excludedCategory.isBlank()) {
            totalExpenses = totalExpenses.subtract(groupByCategory().getOrDefault(excludedCategory, BigDecimal.ZERO));
        }

        BigDecimal averageExpenses = totalExpenses.divide(BigDecimal.valueOf(monthCount), 2, RoundingMode.HALF_UP);

        return MONTHLY_INCOME.subtract(averageExpenses);
    }

}
