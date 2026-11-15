package com.libtrack.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/overdue")
@CrossOrigin(origins = "*")
public class OverdueController {

    private final JdbcTemplate jdbcTemplate;

    public OverdueController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getOverdueBooks() {

        String sql =
            "SELECT i.issue_id, i.book_id, b.title, " +
            "i.member_id, m.name AS member_name, " +
            "i.issue_date, i.due_date, " +
            "DATEDIFF(CURDATE(), i.due_date) AS overdue_days, " +
            "DATEDIFF(CURDATE(), i.due_date) * 5 AS fine_amount " +
            "FROM IssueRecords i " +
            "JOIN Books b ON i.book_id = b.book_id " +
            "JOIN Members m ON i.member_id = m.member_id " +
            "WHERE i.status = 'Issued' " +
            "AND i.due_date < CURDATE() " +
            "ORDER BY i.due_date ASC";

        List<Map<String, Object>> overdueBooks =
            jdbcTemplate.queryForList(sql);

        return overdueBooks;
    }
}