<%@ page import="com.gym.user.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute("user");
    if (user != null) {
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/member?action=dashboard");
        }
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Gym System Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css" />
</head>
<body>
<div class="auth-wrap">
    <div class="card auth-card">
        <h2 class="title">Gym Management Login</h2>
        <p class="subtitle">Secure access for admin and members.</p>
        <% if (request.getParameter("error") != null) { %>
        <div class="notice error"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
        <div class="notice">Registration successful. Please login.</div>
        <% } %>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="login" />
            <label class="label">Email</label>
            <input type="email" name="email" required/>
            <label class="label">Password</label>
            <input type="password" name="password" required/>
            <button type="submit">Login</button>
        </form>
        <p class="subtitle">New user? <a href="register.jsp">Create account</a></p>
    </div>
</div>
</body>
</html>