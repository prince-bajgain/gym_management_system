<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gym.user.model.User" %>
<%@ page import="com.gym.model.MemberMembership" %>
<%@ page import="com.gym.model.Payment" %>
<%@ page import="com.gym.model.Attendance" %>
<%@ page import="com.gym.model.WorkoutPlan" %>
<%@ page import="com.gym.model.GymNotification" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"MEMBER".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
    User profile = (User) request.getAttribute("profile");
    MemberMembership membership = (MemberMembership) request.getAttribute("membership");
    List<Payment> payments = (List<Payment>) request.getAttribute("payments");
    List<Attendance> attendanceList = (List<Attendance>) request.getAttribute("attendanceList");
    List<WorkoutPlan> workouts = (List<WorkoutPlan>) request.getAttribute("workouts");
    List<GymNotification> notifications = (List<GymNotification>) request.getAttribute("notifications");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Member Dashboard</title>
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
            <h1 class="title">Member Dashboard</h1>
            <div class="subtitle">Welcome, <%= profile == null ? user.getFullName() : profile.getFullName() %></div>
        </div>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <input type="hidden" name="action" value="logout"/>
            <button class="btn-secondary" type="submit">Logout</button>
        </form>
    </div>

    <div class="row">
        <div class="col-4 card">
            <h3>Membership</h3>
            <% if (membership != null) { %>
            <div class="label">Plan</div><div><%= membership.getPlanName() %></div>
            <div class="label">Status</div><div><%= membership.getStatus() %></div>
            <div class="label">Start</div><div><%= membership.getStartDate() %></div>
            <div class="label">End</div><div><%= membership.getEndDate() %></div>
            <div class="label">Days Remaining</div><div class="kpi"><%= request.getAttribute("remainingDays") %></div>
            <% } else { %>
            <div class="notice error">No membership assigned yet.</div>
            <% } %>
        </div>

        <div class="col-8 card">
            <h3>Profile Management</h3>
            <div class="grid-2">
                <form action="${pageContext.request.contextPath}/member" method="post">
                    <input type="hidden" name="action" value="updateProfile"/>
                    <label class="label">Full Name</label>
                    <input type="text" name="fullName" value="<%= profile == null ? "" : profile.getFullName() %>" required/>
                    <label class="label">Phone</label>
                    <input type="text" name="phone" value="<%= profile == null || profile.getPhone() == null ? "" : profile.getPhone() %>"/>
                    <button type="submit">Update Profile</button>
                </form>
                <form action="${pageContext.request.contextPath}/member" method="post">
                    <input type="hidden" name="action" value="updatePassword"/>
                    <label class="label">Current Password</label>
                    <input type="password" name="currentPassword" required/>
                    <label class="label">New Password</label>
                    <input type="password" name="newPassword" required/>
                    <button type="submit">Change Password</button>
                </form>
            </div>
        </div>

        <div class="col-6 card">
            <h3>Attendance History</h3>
            <div class="label">Total Visits</div>
            <div class="kpi"><%= attendanceList == null ? 0 : attendanceList.size() %></div>
            <div class="table-wrap">
                <table>
                    <tr><th>Check In</th><th>Check Out</th></tr>
                    <% if (attendanceList != null) for (Attendance a : attendanceList) { %>
                    <tr>
                        <td><%= a.getCheckInTime() %></td>
                        <td><%= a.getCheckOutTime() == null ? "-" : a.getCheckOutTime() %></td>
                    </tr>
                    <% } %>
                </table>
            </div>
        </div>

        <div class="col-6 card">
            <h3>Payment History</h3>
            <div class="table-wrap">
                <table>
                    <tr><th>Date</th><th>Amount</th><th>Method</th><th>Status</th></tr>
                    <% if (payments != null) for (Payment p : payments) { %>
                    <tr>
                        <td><%= p.getPaymentDate() %></td>
                        <td><%= p.getAmount() %></td>
                        <td><%= p.getPaymentMethod() %></td>
                        <td><%= p.getStatus() %></td>
                    </tr>
                    <% } %>
                </table>
            </div>
        </div>

        <div class="col-8 card">
            <h3>Workout Plans</h3>
            <form action="${pageContext.request.contextPath}/member" method="post">
                <input type="hidden" name="action" value="addWorkout"/>
                <div class="grid-2">
                    <input type="text" name="title" placeholder="Workout Title" required/>
                    <input type="text" name="goal" placeholder="Goal"/>
                </div>
                <textarea name="description" placeholder="Workout Description"></textarea>
                <button type="submit">Add Workout</button>
            </form>
            <div class="table-wrap">
                <table>
                    <tr><th>ID</th><th>Title</th><th>Goal</th><th>Description</th><th>Actions</th></tr>
                    <% if (workouts != null) for (WorkoutPlan w : workouts) { %>
                    <tr>
                        <td><%= w.getWorkoutId() %></td>
                        <td><%= w.getTitle() %></td>
                        <td><%= w.getGoal() %></td>
                        <td><%= w.getDescription() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/member" method="post">
                                <input type="hidden" name="action" value="deleteWorkout"/>
                                <input type="hidden" name="workoutId" value="<%= w.getWorkoutId() %>"/>
                                <button class="btn-danger" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </table>
            </div>
        </div>

        <div class="col-4 card">
            <h3>Notifications</h3>
            <% if (notifications == null || notifications.isEmpty()) { %>
            <div class="label">No notifications yet.</div>
            <% } else { %>
            <div class="table-wrap">
                <table>
                    <tr><th>Type</th><th>Message</th></tr>
                    <% for (GymNotification n : notifications) { %>
                    <tr>
                        <td><%= n.getType() %></td>
                        <td><%= n.getMessage() %></td>
                    </tr>
                    <% } %>
                </table>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
