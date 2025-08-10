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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/createUser")
public class CreateUserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CreateUserServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            // Read JSON body and map to User class
            User inputUser = mapper.readValue(request.getReader(), User.class);

            if (inputUser.getUsername() == null ||
                    inputUser.getPassword() == null ||
                    inputUser.getRole() == null) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing required fields\"}");
                return;
            }

            // Hash the password
            String hashedPassword = PasswordHash.hashPassword(inputUser.getPassword());
            inputUser.setPassword(hashedPassword);

            boolean success = userDAO.createUser(inputUser);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\":\"User created successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to create user\"}");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
}
