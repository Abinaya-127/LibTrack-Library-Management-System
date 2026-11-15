package com.libtrack.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final JdbcTemplate jdbcTemplate;

    public MemberController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getMembers() {
        return jdbcTemplate.queryForList("SELECT * FROM Members");
    }

    @PostMapping
    public String addMember(@RequestBody Map<String, Object> member) {

        String name = (String) member.get("name");
        String email = (String) member.get("email");
        String phone = (String) member.get("phone");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required");
        }

        jdbcTemplate.update(
            "INSERT INTO Members (name, email, phone) VALUES (?, ?, ?)",
            name, email, phone
        );

        return "Member added successfully";
    }
}