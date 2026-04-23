package com.gym.user.controller;

import com.gym.user.model.User;
import com.gym.user.model.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");

        if("view".equals(action)){
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users",users);
            request.getRequestDispatcher("/views/view-members.jsp").forward(request,response);
        } else if ("delete".equals(action)){
            int id = Integer.parseInt(request.getParameter("id"));
            userDAO.deleteUser(id);
            response.sendRedirect("admin?action=view");

        }
    }
}
