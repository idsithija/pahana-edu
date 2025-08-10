package com.pahanaedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;
import com.pahanaedu.util.PasswordHash;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    static class LoginRequest {
        public String username;
        public String password;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Parse JSON request
        BufferedReader reader = req.getReader();
        LoginRequest loginRequest = mapper.readValue(reader, LoginRequest.class);

        User user = userDAO.findByUsername(loginRequest.username);

        Map<String, Object> response = new HashMap<>();

        if (user != null && PasswordHash.checkPassword(loginRequest.password, user.getPassword())) {
            response.put("success", true);
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }

        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), response);
    }
}
