package com.openclassrooms.p2dfsbea.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Expense {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATTER_MONTH = DateTimeFormatter.ofPattern("MMMM yyyy");

    private LocalDate date;
    private String category;
    private String description;
    private BigDecimal amount;

    public Expense() {
    }

    public Expense(LocalDate date, String category, String description, BigDecimal amount) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getMonthFullName() {
        return date.format(FORMATTER_MONTH);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date.format(FORMATTER) + "," + category + "," + description + "," + amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static Expense fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;
        try {
            LocalDate date = LocalDate.parse(parts[0].trim(), FORMATTER);
            String category = parts[1].trim();
            String description = parts[2].trim();
            BigDecimal amount = new BigDecimal(parts[3].trim().replace(',', '.'));
            return new Expense(date, category, description, amount);
        } catch (Exception e) {
            return null;
        }
    }

    public String toCsvLine() {
        return toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(date, expense.date) && Objects.equals(category, expense.category) && Objects.equals(description, expense.description) && Objects.equals(amount, expense.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, category, description, amount);
    }
}
