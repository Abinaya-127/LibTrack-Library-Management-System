package com.libtrack.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/returns")
@CrossOrigin(origins = "*")
public class ReturnController {

    private final JdbcTemplate jdbcTemplate;

    public ReturnController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getReturns() {
        return jdbcTemplate.queryForList(
            "SELECT * FROM Returns ORDER BY return_id DESC"
        );
    }

    @PostMapping
    @Transactional
    public String returnBook(@RequestBody Map<String, Object> data) {

        int issueId = Integer.parseInt(data.get("issue_id").toString());

        List<Map<String, Object>> records = jdbcTemplate.queryForList(
            "SELECT book_id, status, due_date FROM IssueRecords " +
            "WHERE issue_id = ? FOR UPDATE",
            issueId
        );

        if (records.isEmpty()) {
            throw new IllegalArgumentException("Invalid Issue ID");
        }

        Map<String, Object> record = records.get(0);

        if (!"Issued".equalsIgnoreCase(record.get("status").toString())) {
            throw new IllegalArgumentException("Book already returned");
        }

        int bookId = (Integer) record.get("book_id");

        Double fine = jdbcTemplate.queryForObject(
            "SELECT GREATEST(DATEDIFF(CURDATE(), ?), 0) * 5.0",
            Double.class, record.get("due_date")
        );

        jdbcTemplate.update(
            "UPDATE IssueRecords SET status = 'Returned', " +
            "return_date = CURDATE() WHERE issue_id = ?",
            issueId
        );

        jdbcTemplate.update(
            "UPDATE Books SET quantity = quantity + 1 WHERE book_id = ?",
            bookId
        );

        jdbcTemplate.update(
            "INSERT INTO Returns (issue_id, actual_return_date, fine_amount) " +
            "VALUES (?, CURDATE(), ?)",
            issueId, fine
        );

        return "Book returned successfully. Fine: ₹" + fine;
    }
}