<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gym.user.model.User" %>
<%
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null || !"ADMIN".equalsIgnoreCase(sessionUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) {
        users = java.util.Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Members Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css" />
</head>
<body>
<div class="page">
    <% if (request.getParameter("error") != null) { %>
    <div class="notice error"><%= request.getParameter("error") %></div>
    <% } %>
    <% if (request.getParameter("success") != null) { %>
    <div class="notice"><%= request.getParameter("success") %></div>
    <% } %>
    <div class="topbar">
        <div>
            <h2 class="title">Members Management</h2>
            <div class="subtitle">Search, filter, update and remove users.</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin?action=dashboard">Back to Dashboard</a>
    </div>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/admin" class="grid-3">
            <input type="hidden" name="action" value="members"/>
            <input type="text" name="search" placeholder="Search by name or email" value="<%= request.getAttribute("search") == null ? "" : request.getAttribute("search") %>"/>
            <select name="status">
                <%
                    String status = request.getAttribute("status") == null ? "ALL" : String.valueOf(request.getAttribute("status"));
                %>
                <option value="ALL" <%= "ALL".equals(status) ? "selected" : "" %>>ALL</option>
                <option value="ACTIVE" <%= "ACTIVE".equals(status) ? "selected" : "" %>>ACTIVE</option>
                <option value="EXPIRED" <%= "EXPIRED".equals(status) ? "selected" : "" %>>EXPIRED</option>
            </select>
            <button type="submit">Apply Filter</button>
        </form>
    </div>

    <div class="card" style="margin-top: 14px;">
        <div class="table-wrap">
            <table>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
                <% for (User u : users) { %>
                <tr>
                    <td><%= u.getUserId() %></td>
                    <td><%= u.getFullName() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><%= u.getPhone() == null ? "" : u.getPhone() %></td>
                    <td><%= u.getRole() %></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin?action=delete&id=<%= u.getUserId() %>"
                           onclick="return confirm('Delete this member?')">Delete</a>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>

    <div class="card" style="margin-top: 14px;">
        <h3>Update Member</h3>
        <form action="${pageContext.request.contextPath}/admin" method="post">
            <input type="hidden" name="action" value="updateMember"/>
            <div class="grid-3">
                <input type="number" name="userId" placeholder="User ID" required/>
                <input type="text" name="fullName" placeholder="Full Name" required/>
                <input type="email" name="email" placeholder="Email" required/>
                <input type="text" name="phone" placeholder="Phone"/>
                <select name="role">
                    <option value="MEMBER">MEMBER</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
                <button type="submit">Update Member</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>