package com.gym.user.controller;

import com.gym.user.model.User;
import com.gym.user.model.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/auth")
public class UserServlet extends HttpServlet {
    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            String name = request.getParameter("fullName") == null ? "" : request.getParameter("fullName").trim();
            String email = request.getParameter("email") == null ? "" : request.getParameter("email").trim().toLowerCase();
            String password = request.getParameter("password") == null ? "" : request.getParameter("password");
            String phone = request.getParameter("phone") == null ? "" : request.getParameter("phone").trim();
            String role = request.getParameter("role") == null ? "MEMBER" : request.getParameter("role");

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                response.sendRedirect("views/register.jsp?error=" + enc("Please fill all required fields."));
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                response.sendRedirect("views/register.jsp?error=" + enc("Please provide a valid email address."));
                return;
            }
            if (password.length() < 6) {
                response.sendRedirect("views/register.jsp?error=" + enc("Password must be at least 6 characters."));
                return;
            }
            if (!"ADMIN".equals(role) && !"MEMBER".equals(role)) {
                response.sendRedirect("views/register.jsp?error=" + enc("Invalid role selected."));
                return;
            }

            boolean emailExists = userDAO.emailExists(email);

            if (emailExists) {
                response.sendRedirect("views/register.jsp?error=" + enc("A user with this email already exists."));
                return;
            }

            User user = new User(name, email, password, phone, role);
            boolean registrationStatus = userDAO.registerUser(user);
            if (registrationStatus) {
                response.sendRedirect("views/login.jsp?success=registered");
            } else {
                response.sendRedirect("views/register.jsp?error=" + enc("Registration failed. Please try again."));
            }
        } else if ("login".equals(action)) {
            String email = request.getParameter("email") == null ? "" : request.getParameter("email").trim();
            String password = request.getParameter("password") == null ? "" : request.getParameter("password");
            if (email.isBlank() || password.isBlank()) {
                response.sendRedirect("views/login.jsp?error=" + enc("Email and password are required."));
                return;
            }

            User user = userDAO.loginUser(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                Cookie emailCookie = new Cookie("userEmail", user.getEmail());
                emailCookie.setMaxAge(60 * 60 * 24);
                emailCookie.setHttpOnly(true);
                response.addCookie(emailCookie);

                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("admin?action=dashboard");
                } else {
                    response.sendRedirect("member?action=dashboard");
                }

            } else {
                response.sendRedirect("views/login.jsp?error=" + enc("Invalid email or password."));
            }
        } else if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate();

            Cookie emailCookie = new Cookie("userEmail", "");
            emailCookie.setMaxAge(0);
            response.addCookie(emailCookie);

            response.sendRedirect("views/login.jsp");
        }
    }
}
