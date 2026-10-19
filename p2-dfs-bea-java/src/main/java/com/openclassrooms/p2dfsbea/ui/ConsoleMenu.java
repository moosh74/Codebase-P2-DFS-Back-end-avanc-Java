package com.openclassrooms.p2dfsbea.ui;

import com.openclassrooms.p2dfsbea.model.Expense;
import com.openclassrooms.p2dfsbea.repository.ExpenseRepository;
import com.openclassrooms.p2dfsbea.service.CarPurchaseSimulatorService;
import com.openclassrooms.p2dfsbea.service.ExpenseService;
import com.openclassrooms.p2dfsbea.service.MortgageLoanSimulatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final ExpenseRepository repository = new ExpenseRepository();
    private final ExpenseService expenseService = new ExpenseService(repository);
    private final CarPurchaseSimulatorService cpsService = new CarPurchaseSimulatorService();
    private final MortgageLoanSimulatorService mortgageSimulatorService = new MortgageLoanSimulatorService();

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showHistory();
                case "2" -> showStatistics();
                case "3" -> carPurchaseSimulator();
                case "4" -> mortgageLoanSimulator();
                case "5" -> addExpense();
                case "6" -> {
                    System.out.println("Goodbye");
                    running = false;
                }
                default -> System.out.println("Unknown option, please try again.");
            }
        }
    }

    private void printMenu() {
        info("\n--- Personal Finance Tracker ---");
        System.out.println("1) Show history");
        System.out.println("2) Show statistics");
        System.out.println("3) Simulation « achat d'une voiture »");
        System.out.println("4) Simulation « prêt immobilier »");
        System.out.println("5) Add expense");
        System.out.println("6) Quit");
        System.out.print("Choose an option: ");
    }

    private void showHistory() {
        List<Expense> list = expenseService.loadAllExpenses();
   
        info("\nHistory (most recent first):");
        info("--------------------------");
       
        list.stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).forEach(e -> System.out.println(e.toCsvLine()));
    }

    private void showStatistics() {
        expenseService.loadAllExpenses();

        BigDecimal total = expenseService.calculateTotalExpenses();
        TreeMap<String, BigDecimal> listPerCategory = expenseService.groupByCategory();
        TreeMap<String, BigDecimal> listPerMonth = expenseService.groupByMonth();

        info("\n--------------------------");
        info("Total dépenses : " + total.setScale(2, RoundingMode.HALF_UP));
        info("Nombre de mois : " + listPerMonth.size());
        info("--------------------------");

        info("\nPar catégorie");
        info("--------------------------");
      
        listPerCategory.forEach((label, amount) -> System.out.println(label + ": " + amount.setScale(2, RoundingMode.HALF_UP)));
    
        info("\nPar mois");
        info("--------------------------");
  
        listPerMonth.forEach((label, amount) -> System.out.println(label + ": " + amount.setScale(2, RoundingMode.HALF_UP)));
   }

    private void carPurchaseSimulator() {
        System.out.print("Prix d'achat de la voiture : ");

        try {
            BigDecimal carPrice = new BigDecimal(scanner.nextLine().trim().replace(',', '.'));

            expenseService.loadAllExpenses();
          
            BigDecimal averageMonthlyExpenses = expenseService.calculateAverageMonthlyExpenses();
            BigDecimal monthlySavings = expenseService.calculateAvailableMonthlySavings();
          
            if (monthlySavings.signum() <= 0) {
                error("Simulation impossible : les dépenses mensuelles moyennes sont supérieures ou égales au revenu mensuel.");
                return;
            }

            int months = cpsService.calculateMonthsToSave(carPrice, monthlySavings);

            info("\n--------------------------");
            info("Revenu mensuel : " + ExpenseService.MONTHLY_INCOME.setScale(2, RoundingMode.HALF_UP));
            info("Dépenses mensuelles moyennes : " + averageMonthlyExpenses.setScale(2, RoundingMode.HALF_UP));
            info("Épargne mensuelle disponible : " + monthlySavings.setScale(2, RoundingMode.HALF_UP));
            info("Prix de la voiture : " + carPrice.setScale(2, RoundingMode.HALF_UP));
            info("Temps nécessaire : " + months + " mois");
            info("--------------------------");
        } catch (NumberFormatException ex) {
            error("Prix invalide. Saisissez un montant positif (ex. 12500.00).");
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (ArithmeticException ex) {
            error("Le montant saisi dépasse la capacité de calcul de la simulation.");
        }
    }

    private void mortgageLoanSimulator() {
        expenseService.loadAllExpenses();

        BigDecimal availableMonthlySavings = expenseService.calculateAvailableMonthlySavings();
        BigDecimal averageHousingExpenses = expenseService.calculateAverageMonthlyExpensesForCategory("housing");
        BigDecimal maximumMonthlyPayment = mortgageSimulatorService.calculateMaximumMonthlyPayment(ExpenseService.MONTHLY_INCOME, averageHousingExpenses, availableMonthlySavings);

        if (maximumMonthlyPayment.signum() <= 0) {
            error("Simulation impossible : aucune capacité de remboursement mensuelle n'est disponible sous le plafond de 35 %.");
            return;
        }

        info("\n--------------------------");
        info("Revenu mensuel : " + ExpenseService.MONTHLY_INCOME.setScale(2, RoundingMode.HALF_UP));
        info("Dépenses logement mensuelles moyennes : " + averageHousingExpenses.setScale(2, RoundingMode.HALF_UP));
        info("Épargne mensuelle disponible : " + availableMonthlySavings.setScale(2, RoundingMode.HALF_UP));
        info("Mensualité maximale pour le prêt : " + maximumMonthlyPayment.setScale(2, RoundingMode.HALF_UP));
        info("--------------------------");
    }

    private void addExpense() {
        try {
            System.out.print("Date (dd/MM/yyyy): ");
            String dateS = scanner.nextLine().trim();
            LocalDate date = LocalDate.parse(dateS, Expense.FORMATTER);

            System.out.print("Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Amount (e.g. 12.50): ");
            String amountS = scanner.nextLine().trim().replace(',', '.');
            BigDecimal amount = new BigDecimal(amountS);

            Expense e = new Expense(date, category, description, amount);
            expenseService.addExpense(e);

            success("Expense added.");
        } catch (DateTimeParseException dtpe) {
            error("Invalid date format. Use dd/MM/yyyy.");
        } catch (IOException io) {
            error("Unable to save expense: " + io.getMessage());
        } catch(NumberFormatException ex) {
        	error("Invalid amount format. Decimal is expected.");
        } catch (Exception ex) {
            error("Error: " + ex.getMessage());
        }
    }

    private void error(String message) {
        System.out.println("\033[31m" + message + "\u001B[0m");
    }

    private void success(String message) {
        System.out.println("\u001B[32m" + message + "\u001B[0m");
    }

    private void info(String message) {
        System.out.println("\u001B[34m" + message + "\u001B[0m");
    }
}
