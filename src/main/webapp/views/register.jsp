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
    <title>Gym System Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css" />
</head>
<body>
<div class="auth-wrap">
    <div class="card auth-card">
        <h2 class="title">Create Account</h2>
        <p class="subtitle">Register as member or admin.</p>
        <% if (request.getParameter("error") != null) { %>
        <div class="notice error"><%= request.getParameter("error") %></div>
        <% } %>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="register" />
            <label class="label">Full Name</label>
            <input type="text" name="fullName" required/>
            <label class="label">Email</label>
            <input type="email" name="email" required/>
            <label class="label">Phone</label>
            <input type="text" name="phone"/>
            <label class="label">Password</label>
            <input type="password" name="password" required/>
            <label class="label">Role</label>
            <select name="role">
                <option value="MEMBER">MEMBER</option>
                <option value="ADMIN">ADMIN</option>
            </select>
            <button type="submit">Register</button>
        </form>
        <p class="subtitle">Already have account? <a href="login.jsp">Login</a></p>
    </div>
</div>
</body>
</html>