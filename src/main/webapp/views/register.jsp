<%@ page import="com.gym.user.model.User" %><%--
  Created by IntelliJ IDEA.
  User: Prince
  Date: 4/19/2026
  Time: 8:35 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute("user");

    if (user != null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }

%>
<html>
<head>
    <title>PowerHouse | Join the Tribe</title>
    <link href="https://fonts.googleapis.com/css2?family=Oswald:wght@500;700&family=Roboto:wght@300;400&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-neon: #ccff00; /* Electric Lime */
            --bg-dark: #121212;
            --card-bg: #1e1e1e;
            --text-light: #ffffff;
            --error-red: #ff4444;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: var(--bg-dark);
            background-image: linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.7)),
            url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80&w=1470');
            background-size: cover;
            background-position: center;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            color: var(--text-light);
        }

        .register-container {
            background: var(--card-bg);
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.5);
            width: 100%;
            max-width: 400px;
            border-top: 5px solid var(--primary-neon);
        }

        h2 {
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            font-size: 2.5rem;
            margin-bottom: 20px;
            letter-spacing: 2px;
            text-align: center;
        }

        .msg {
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            text-align: center;
            font-weight: bold;
        }

        .input-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-size: 0.9rem;
            text-transform: uppercase;
            color: #888;
        }

        input[type="text"],
        input[type="password"],
        select {
            width: 100%;
            padding: 12px;
            background: #2a2a2a;
            border: 1px solid #333;
            color: white;
            border-radius: 4px;
            box-sizing: border-box;
            transition: border 0.3s;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary-neon);
        }

        button {
            width: 100%;
            padding: 15px;
            background: var(--primary-neon);
            border: none;
            color: black;
            font-family: 'Oswald', sans-serif;
            font-size: 1.2rem;
            text-transform: uppercase;
            cursor: pointer;
            border-radius: 4px;
            transition: transform 0.2s, background 0.3s;
            margin-top: 10px;
        }

        button:hover {
            background: #b3e600;
            transform: scale(1.02);
        }

        .footer-text {
            margin-top: 20px;
            text-align: center;
            font-size: 0.8rem;
            color: #666;
        }
    </style>
</head>
<body>

<div class="register-container">
    <h2>Join the Tribe</h2>

    <% if(request.getAttribute("success") != null) { %>
    <div class="msg" style="color: var(--primary-neon); border: 1px solid var(--primary-neon);">
        <%= request.getAttribute("success") %>
    </div>
    <% } %>

    <% if(request.getParameter("error") != null) { %>
    <div class="msg" style="color: var(--error-red); border: 1px solid var(--error-red);">
        <%= request.getParameter("error") %>
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/user" method="post">
        <input type="hidden" name="action" value="register">

        <div class="input-group">
            <label>Full Name</label>
            <input type="text" name="name" placeholder="e.g. Arnold S." required>
        </div>

        <div class="input-group">
            <label>Email Address</label>
            <input type="text" name="email" placeholder="muscle@gym.com" required>
        </div>

        <div class="input-group">
            <label>Password</label>
            <input type="password" name="password" placeholder="••••••••" required>
        </div>

        <button type="submit">Start Training</button>
    </form>

    <p class="footer-text">NO EXCUSES. JUST RESULTS.</p>
</div>

</body>
</html>