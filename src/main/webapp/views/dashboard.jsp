<%@ page import="com.gym.user.model.User" %><%--
  Created by IntelliJ IDEA.
  User: Prince
  Date: 4/22/2026
  Time: 9:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String userEmail = user.getEmail();
    String role = user.getRole();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | PowerHouse Gym</title>
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

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: var(--bg-dark);
            color: var(--text-main);
            display: flex;
            min-height: 100vh;
            background-image: linear-gradient(rgba(18, 18, 18, 0.9), rgba(18, 18, 18, 0.9)),
            url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
        }

        /* Sidebar Navigation */
        nav.sidebar {
            width: 260px;
            background: rgba(30, 30, 30, 0.95);
            border-right: 1px solid rgba(204, 255, 0, 0.1);
            display: flex;
            flex-direction: column;
            position: fixed;
            height: 100vh;
            z-index: 100;
        }

        .brand {
            padding: 40px 30px;
            font-family: 'Oswald', sans-serif;
            font-size: 1.8rem;
            color: var(--neon-lime);
            text-transform: uppercase;
            letter-spacing: 2px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }

        .nav-links {
            flex-grow: 1;
            padding: 20px 0;
        }

        .nav-links a {
            display: block;
            padding: 15px 30px;
            color: var(--text-dim);
            text-decoration: none;
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            letter-spacing: 1px;
            transition: var(--transition);
            border-left: 4px solid transparent;
        }

        .nav-links a:hover, .nav-links a.active {
            color: var(--neon-lime);
            background: rgba(204, 255, 0, 0.05);
            border-left: 4px solid var(--neon-lime);
        }

        /* Main Content */
        main {
            margin-left: 260px;
            flex-grow: 1;
            padding: 40px;
        }

        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 50px;
        }

        .header-title h1 {
            font-family: 'Oswald', sans-serif;
            font-size: 2.5rem;
            text-transform: uppercase;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .user-info span {
            color: var(--text-dim);
            font-size: 0.9rem;
            display: block;
        }

        .logout-btn {
            background: transparent;
            border: 1px solid var(--neon-lime);
            color: var(--neon-lime);
            padding: 8px 20px;
            text-decoration: none;
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            font-size: 0.8rem;
            transition: var(--transition);
        }

        .logout-btn:hover {
            background: var(--neon-lime);
            color: #000;
            box-shadow: 0 0 15px rgba(204, 255, 0, 0.4);
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 25px;
        }

        .card {
            background: var(--card-bg);
            padding: 30px;
            border-left: 4px solid var(--neon-lime);
            position: relative;
            overflow: hidden;
            transition: var(--transition);
            cursor: pointer;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5), 0 0 15px rgba(204, 255, 0, 0.1);
        }

        .card::after {
            content: '';
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            background: radial-gradient(circle at top right, rgba(204, 255, 0, 0.05), transparent);
            opacity: 0;
            transition: var(--transition);
        }

        .card:hover::after {
            opacity: 1;
        }

        .card-label {
            font-family: 'Oswald', sans-serif;
            color: var(--text-dim);
            text-transform: uppercase;
            font-size: 0.9rem;
            letter-spacing: 1px;
            margin-bottom: 10px;
            display: block;
        }

        .card-value {
            font-family: 'Oswald', sans-serif;
            font-size: 2.2rem;
            font-weight: 700;
            color: var(--neon-lime);
        }

        .card-sub {
            font-size: 0.8rem;
            color: #555;
            margin-top: 10px;
        }

        /* Responsive */
        @media (max-width: 992px) {
            nav.sidebar { width: 80px; }
            .brand, .nav-links span, .user-info { display: none; }
            main { margin-left: 80px; }
            .nav-links a { text-align: center; padding: 20px; }
        }

        @media (max-width: 600px) {
            main { padding: 20px; }
            header { flex-direction: column; align-items: flex-start; gap: 20px; }
        }
    </style>
</head>
<body>

<nav class="sidebar">
    <div class="brand">PowerHouse</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/views/dashboard.jsp" class="active">Dashboard</a>
        <a href="add-member.jsp">Add Member</a>
        <a href="${pageContext.request.contextPath}/admin?action=view">View Members</a>
        <a href="payments.jsp">Payments</a>
        <a href="settings.jsp">Settings</a>
    </div>
</nav>

<main>
    <header>
        <div class="header-title">
            <h1>PowerHouse Dashboard</h1>
            <p style="color: var(--text-dim);">Welcome back to the arena.</p>
        </div>
        <div class="user-profile">
            <div class="user-info">
                <span>Logged in as</span>
                <strong><%= userEmail %></strong>
            </div>
            <form action="${pageContext.request.contextPath}/user" method="post" style="display:inline;">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </header>

    <section class="stats-grid">
        <div class="card">
            <span class="card-label">Total Members</span>
            <div class="card-value">1,248</div>
            <div class="card-sub">+12% from last month</div>
        </div>

        <div class="card">
            <span class="card-label">Active Plans</span>
            <div class="card-value">856</div>
            <div class="card-sub">Premium: 430 | Basic: 426</div>
        </div>

        <div class="card">
            <span class="card-label">Today's Attendance</span>
            <div class="card-value">142</div>
            <div class="card-sub">Peak time: 18:00 - 20:00</div>
        </div>

        <div class="card">
            <span class="card-label">Revenue Summary</span>
            <div class="card-value">$12,450</div>
            <div class="card-sub">Monthly Goal: 85% reached</div>
        </div>
    </section>

    <div style="margin-top: 40px; background: var(--card-bg); padding: 30px; border-left: 4px solid var(--neon-lime); height: 300px; display: flex; align-items: center; justify-content: center; color: #444; border-radius: 2px;">
        <p style="font-family: 'Oswald', sans-serif; letter-spacing: 2px;">Recent Activity Feed - Analytics Engine Active</p>
    </div>
</main>

</body>
</html>
