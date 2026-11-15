package com.libtrack.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final JdbcTemplate jdbcTemplate;

    public BookController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getBooks() {
        return jdbcTemplate.queryForList("SELECT * FROM Books");
    }

    @PostMapping
    public String addBook(@RequestBody Map<String, Object> book) {

        String title = (String) book.get("title");
        String author = (String) book.get("author");
        String category = (String) book.get("category");
        int quantity = Integer.parseInt(book.get("quantity").toString());

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        jdbcTemplate.update(
            "INSERT INTO Books (title, author, category, quantity) VALUES (?, ?, ?, ?)",
            title, author, category, quantity
        );

        return "Book added successfully";
    }
}