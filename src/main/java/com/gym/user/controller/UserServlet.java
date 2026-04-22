package com.gym.user.controller;

import com.gym.user.model.User;
import com.gym.user.model.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        String action = request.getParameter("action");

        if(action.equals("register")){
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            boolean emailExists = userDAO.emailExists(email);

            if(emailExists){
                response.sendRedirect("views/register.jsp?error=email_already_exists");
                return;
            }

            User user = new User(name,email,password,role);
            boolean registrationStatus = userDAO.registerUser(user);
            if (registrationStatus) {
                response.sendRedirect("views/login.jsp?success=registered");
            } else {
                response.sendRedirect("views/register.jsp?error=failed");
            }
        } else if (action.equals("login")){
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User user = userDAO.loginUser(email,password);
            if(user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user",user);

                Cookie emailCookie = new Cookie("userEmail", user.getEmail());
                emailCookie.setMaxAge(60*60*24);
                emailCookie.setHttpOnly(true);
                response.addCookie(emailCookie);

                response.sendRedirect("views/dashboard.jsp");

            } else {
                response.sendRedirect("views/login.jsp?error=failed");
            }
        } else if (action.equals("logout")){
            HttpSession session = request.getSession();
            session.invalidate();

            Cookie emailCookie = new Cookie("userEmail", "");
            emailCookie.setMaxAge(0);
            response.addCookie(emailCookie);

            response.sendRedirect("views/login.jsp");
        }
    }
}
