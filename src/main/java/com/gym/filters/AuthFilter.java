package com.gym.filters;

import com.gym.user.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/member", "/views/*"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        String uri = req.getRequestURI();
        if (uri.endsWith("login.jsp") || uri.endsWith("register.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        if (uri.contains("/admin") && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/member?action=dashboard");
            return;
        }

        if (uri.contains("/member") && !"MEMBER".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/admin?action=dashboard");
            return;
        }

        chain.doFilter(request, response);
    }
}
