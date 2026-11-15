package com.libtrack.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private final JdbcTemplate jdbcTemplate;

    public IssueController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getIssues() {
        return jdbcTemplate.queryForList(
            "SELECT * FROM IssueRecords ORDER BY issue_id DESC"
        );
    }

    @PostMapping
    @Transactional
    public String issueBook(@RequestBody Map<String, Object> data) {

        int bookId = Integer.parseInt(data.get("book_id").toString());
        int memberId = Integer.parseInt(data.get("member_id").toString());

        Integer memberCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Members WHERE member_id = ?",
            Integer.class, memberId
        );

        if (memberCount == null || memberCount == 0) {
            throw new IllegalArgumentException("Member not found");
        }

        Integer quantity = jdbcTemplate.queryForObject(
            "SELECT quantity FROM Books WHERE book_id = ? FOR UPDATE",
            Integer.class, bookId
        );

        if (quantity == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Book is not available");
        }

        jdbcTemplate.update(
            "UPDATE Books SET quantity = quantity - 1 WHERE book_id = ?",
            bookId
        );

        jdbcTemplate.update(
            "INSERT INTO IssueRecords " +
            "(book_id, member_id, issue_date, due_date, status) " +
            "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Issued')",
            bookId, memberId
        );

        return "Book issued successfully";
    }
}