<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gym.user.model.User" %>
<%
    // Session check to ensure styling consistency
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<User> users = (List<User>) request.getAttribute("users");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Members | PowerHouse Gym</title>
    <link href="https://fonts.googleapis.com/css2?family=Oswald:wght@400;700&family=Roboto:wght@300;400;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --neon-lime: #ccff00;
            --bg-dark: #121212;
            --card-bg: #1e1e1e;
            --text-main: #ffffff;
            --text-dim: #b3b3b3;
            --transition: all 0.3s ease;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: var(--bg-dark);
            color: var(--text-main);
            display: flex;
            min-height: 100vh;
            background-image: linear-gradient(rgba(18, 18, 18, 0.9), rgba(18, 18, 18, 0.9)),
            url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80');
            background-size: cover; background-position: center; background-attachment: fixed;
        }

        /* Sidebar Navigation */
        nav.sidebar {
            width: 260px; background: rgba(30, 30, 30, 0.95);
            border-right: 1px solid rgba(204, 255, 0, 0.1);
            display: flex; flex-direction: column; position: fixed; height: 100vh; z-index: 100;
        }

        .brand {
            padding: 40px 30px; font-family: 'Oswald', sans-serif; font-size: 1.8rem;
            color: var(--neon-lime); text-transform: uppercase; letter-spacing: 2px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }

        .nav-links a {
            display: block; padding: 15px 30px; color: var(--text-dim);
            text-decoration: none; font-family: 'Oswald', sans-serif;
            text-transform: uppercase; transition: var(--transition); border-left: 4px solid transparent;
        }

        .nav-links a:hover, .nav-links a.active {
            color: var(--neon-lime); background: rgba(204, 255, 0, 0.05); border-left: 4px solid var(--neon-lime);
        }

        /* Main Content */
        main { margin-left: 260px; flex-grow: 1; padding: 40px; }

        header { margin-bottom: 40px; }
        header h1 { font-family: 'Oswald', sans-serif; font-size: 2.5rem; text-transform: uppercase; }

        /* Table Styling */
        .table-container {
            background: var(--card-bg);
            padding: 20px;
            border-left: 4px solid var(--neon-lime);
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th {
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            color: var(--neon-lime);
            text-align: left;
            padding: 15px;
            border-bottom: 2px solid rgba(204, 255, 0, 0.2);
            letter-spacing: 1px;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
            color: var(--text-main);
        }

        tr:hover {
            background: rgba(255, 255, 255, 0.02);
        }

        /* Action Buttons */
        .btn-delete {
            background: transparent;
            border: 1px solid #ff4444;
            color: #ff4444;
            padding: 5px 12px;
            text-decoration: none;
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            font-size: 0.75rem;
            transition: var(--transition);
        }

        .btn-delete:hover {
            background: #ff4444;
            color: white;
            box-shadow: 0 0 10px rgba(255, 68, 68, 0.3);
        }

        @media (max-width: 992px) {
            nav.sidebar { width: 80px; }
            .brand, .nav-links span { display: none; }
            main { margin-left: 80px; }
        }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="brand">PowerHouse</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/views/dashboard.jsp">Dashboard</a>
        <a href="add-member.jsp">Add Member</a>
        <a href="${pageContext.request.contextPath}/admin?action=view" class="active">View Members</a>
        <a href="payments.jsp">Payments</a>
        <a href="settings.jsp">Settings</a>
    </div>
</nav>

<main>
    <header>
        <h1>Member Directory</h1>
        <p style="color: var(--text-dim);">Review and manage your active athletes.</p>
    </header>

    <div class="table-container">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Email Address</th>
                <th style="text-align: right;">Management</th>
            </tr>
            </thead>
            <tbody>
            <% if(users != null) {
                for(User u : users) { %>
            <tr>
                <td style="color: var(--neon-lime); font-weight: bold;">#<%= u.getId() %></td>
                <td><%= u.getName() %></td>
                <td style="color: var(--text-dim);"><%= u.getEmail() %></td>
                <td style="text-align: right;">
                    <a href="admin?action=delete&id=<%= u.getId() %>"
                       class="btn-delete"
                       onclick="return confirm('Are you sure you want to remove this member?')">Delete</a>
                </td>
            </tr>
            <%  }
            } else { %>
            <tr>
                <td colspan="4" style="text-align: center; padding: 40px; color: var(--text-dim);">No members found in the arena.</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</main>

</body>
</html>