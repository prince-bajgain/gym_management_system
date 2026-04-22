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
    <title>PowerHouse | Athlete Login</title>
    <link href="https://fonts.googleapis.com/css2?family=Oswald:wght@500;700&family=Roboto:wght@300;400&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-neon: #ccff00; /* Electric Lime */
            --bg-dark: #121212;
            --card-bg: #1e1e1e;
            --error-red: #ff4444;
            --text-light: #ffffff;
        }

        .msg {
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            text-align: center;
            font-weight: bold;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: var(--bg-dark);
            /* Different gym background for login to keep it fresh */
            background-image: linear-gradient(rgba(0,0,0,0.75), rgba(0,0,0,0.75)),
            url('https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&q=80&w=1470');
            background-size: cover;
            background-position: center;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            color: var(--text-light);
        }

        .login-container {
            background: var(--card-bg);
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.6);
            width: 100%;
            max-width: 380px;
            border-left: 5px solid var(--primary-neon); /* Side accent instead of top */
        }

        h2 {
            font-family: 'Oswald', sans-serif;
            text-transform: uppercase;
            font-size: 2.2rem;
            margin-bottom: 10px;
            letter-spacing: 1px;
        }

        p.subtitle {
            color: #888;
            margin-bottom: 30px;
            font-size: 0.9rem;
            text-transform: uppercase;
        }

        .input-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
            color: var(--primary-neon);
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 14px;
            background: #252525;
            border: 1px solid #333;
            color: white;
            border-radius: 4px;
            box-sizing: border-box;
            transition: all 0.3s ease;
        }

        input:focus {
            outline: none;
            border-color: var(--primary-neon);
            background: #2a2a2a;
            box-shadow: 0 0 8px rgba(204, 255, 0, 0.2);
        }

        button {
            width: 100%;
            padding: 16px;
            background: var(--primary-neon);
            border: none;
            color: #000;
            font-family: 'Oswald', sans-serif;
            font-size: 1.2rem;
            text-transform: uppercase;
            font-weight: 700;
            cursor: pointer;
            border-radius: 4px;
            transition: all 0.2s;
            margin-top: 5px;
        }

        button:hover {
            background: #fff; /* White hover for high contrast */
            transform: translateY(-2px);
        }

        .extra-links {
            margin-top: 25px;
            text-align: center;
            font-size: 0.85rem;
        }

        .extra-links a {
            color: #888;
            text-decoration: none;
            transition: color 0.3s;
        }

        .extra-links a:hover {
            color: var(--primary-neon);
        }
    </style>
</head>
<body>

<div class="login-container">
    <h2>Welcome Back</h2>
    <p class="subtitle">Ready for your next set?</p>

    <% if(request.getParameter("error") != null) { %>
    <div class="msg" style="color: var(--error-red); border: 1px solid var(--error-red);">
        <%= request.getParameter("error") %>
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/user" method="post">
        <input type="hidden" name="action" value="login">

        <div class="input-group">
            <label>Email Address</label>
            <input type="text" name="email" placeholder="athlete@powerhouse.com" required>
        </div>

        <div class="input-group">
            <label>Password</label>
            <input type="password" name="password" placeholder="••••••••" required>
        </div>

        <button type="submit">Unlock Session</button>
    </form>

    <div class="extra-links">
        <a href="register.jsp">New here? Join the Tribe</a>
    </div>
</div>

</body>
</html>