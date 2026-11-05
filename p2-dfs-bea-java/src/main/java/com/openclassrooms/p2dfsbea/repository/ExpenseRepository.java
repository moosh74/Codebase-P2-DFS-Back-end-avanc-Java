package com.openclassrooms.p2dfsbea.repository;

import com.openclassrooms.p2dfsbea.model.Expense;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ExpenseRepository {
    private final Path emmaPath = Path.of("src/main/resources/emma_expenses.csv");

    public List<Expense> readAll() {
        List<Expense> result = new ArrayList<>();
        readFileIntoList(emmaPath, result);
        return result;
    }

    private void readFileIntoList(Path p, List<Expense> list) {
        if (!Files.exists(p)) return;
        try (Stream<String> lines = Files.lines(p, StandardCharsets.UTF_8)) {
            lines.map(Expense::fromCsvLine).filter(e -> e != null).forEach(list::add);
        } catch (IOException e) {
            System.err.println("Unable to read " + p + ": " + e.getMessage());
        }
    }

    public void append(Expense expense) throws IOException {
        if (!Files.exists(emmaPath) || Files.size(emmaPath) == 0) {
            Files.createDirectories(emmaPath.getParent());
            Files.writeString(emmaPath, "date,category,description,amount" + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(emmaPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(expense.toCsvLine());
            writer.newLine();
        }
    }
}
