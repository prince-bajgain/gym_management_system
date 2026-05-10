<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gym.user.model.User" %>
<%@ page import="com.gym.model.MembershipPlan" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
    List<User> members = (List<User>) request.getAttribute("members");
    List<MembershipPlan> plans = (List<MembershipPlan>) request.getAttribute("plans");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
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
            <h1 class="title">Admin Dashboard</h1>
            <div class="subtitle">Welcome, <%= user.getFullName() %></div>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/admin?action=members">Manage Members</a>
            <form style="display:inline-block; margin-left:8px;" action="${pageContext.request.contextPath}/auth" method="post">
                <input type="hidden" name="action" value="logout"/>
                <button class="btn-secondary" type="submit">Logout</button>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-3 card"><div class="label">Total Members</div><div class="kpi"><%= request.getAttribute("totalMembers") %></div></div>
        <div class="col-3 card"><div class="label">Active Memberships</div><div class="kpi"><%= request.getAttribute("activeMemberships") %></div></div>
        <div class="col-3 card"><div class="label">Today's Attendance</div><div class="kpi"><%= request.getAttribute("todayAttendance") %></div></div>
        <div class="col-3 card"><div class="label">Total Revenue</div><div class="kpi"><%= request.getAttribute("totalRevenue") %></div></div>
    </div>

    <div class="row" style="margin-top: 14px;">
        <div class="col-6 card">
            <h3>Add Member</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="addMember"/>
                <div class="grid-2">
                    <input type="text" name="fullName" placeholder="Full Name" required/>
                    <input type="email" name="email" placeholder="Email" required/>
                    <input type="text" name="phone" placeholder="Phone"/>
                    <input type="password" name="password" placeholder="Password" required/>
                </div>
                <button type="submit">Add Member</button>
            </form>
        </div>

        <div class="col-6 card">
            <h3>Assign Membership</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="assignMembership"/>
                <div class="grid-2">
                    <select name="userId" required>
                        <option value="">Select Member</option>
                        <% if (members != null) for (User m : members) { %>
                        <option value="<%= m.getUserId() %>"><%= m.getUserId() %> - <%= m.getFullName() %></option>
                        <% } %>
                    </select>
                    <select name="planId" required>
                        <option value="">Select Plan</option>
                        <% if (plans != null) for (MembershipPlan p : plans) { %>
                        <option value="<%= p.getPlanId() %>"><%= p.getPlanName() %> (<%= p.getDurationDays() %> days)</option>
                        <% } %>
                    </select>
                    <input type="date" name="startDate" required/>
                    <input type="date" name="endDate" />
                </div>
                <select name="status">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="EXPIRED">EXPIRED</option>
                    <option value="SUSPENDED">SUSPENDED</option>
                </select>
                <button type="submit">Assign Membership</button>
            </form>
        </div>

        <div class="col-6 card">
            <h3>Update Membership Status</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="updateMembershipStatus"/>
                <div class="grid-2">
                    <input type="number" name="membershipId" placeholder="Membership ID" required/>
                    <select name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="EXPIRED">EXPIRED</option>
                        <option value="SUSPENDED">SUSPENDED</option>
                    </select>
                </div>
                <button type="submit">Update Status</button>
            </form>
        </div>

        <div class="col-6 card">
            <h3>Record Payment</h3>
            <form action="${pageContext.request.contextPath}/admin" method="post">
                <input type="hidden" name="action" value="recordPayment"/>
                <div class="grid-3">
                    <select name="userId" required>
                        <option value="">Member</option>
                        <% if (members != null) for (User m : members) { %>
                        <option value="<%= m.getUserId() %>"><%= m.getUserId() %> - <%= m.getFullName() %></option>
                        <% } %>
                    </select>
                    <input type="number" step="0.01" name="amount" placeholder="Amount" required/>
                    <input type="date" name="paymentDate" required/>
                </div>
                <div class="grid-2">
                    <input type="text" name="paymentMethod" placeholder="Payment Method"/>
                    <select name="status">
                        <option value="PAID">PAID</option>
                        <option value="PENDING">PENDING</option>
                        <option value="FAILED">FAILED</option>
                    </select>
                </div>
                <button type="submit">Record Payment</button>
            </form>
        </div>

        <div class="col-12 card">
            <h3>Membership Plans</h3>
            <div class="table-wrap">
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Duration</th>
                        <th>Price</th>
                        <th>Description</th>
                    </tr>
                    <% if (plans != null) for (MembershipPlan p : plans) { %>
                    <tr>
                        <td><%= p.getPlanId() %></td>
                        <td><%= p.getPlanName() %></td>
                        <td><%= p.getDurationDays() %> days</td>
                        <td><%= p.getPrice() %></td>
                        <td><%= p.getDescription() == null ? "" : p.getDescription() %></td>
                    </tr>
                    <% } %>
                </table>
            </div>
            <div class="grid-3" style="margin-top: 10px;">
                <form action="${pageContext.request.contextPath}/admin" method="post">
                    <input type="hidden" name="action" value="createPlan"/>
                    <input type="text" name="planName" placeholder="Plan Name" required/>
                    <input type="number" name="durationDays" placeholder="Duration Days" required/>
                    <input type="number" step="0.01" name="price" placeholder="Price" required/>
                    <textarea name="description" placeholder="Description"></textarea>
                    <button type="submit">Create Plan</button>
                </form>
                <form action="${pageContext.request.contextPath}/admin" method="post">
                    <input type="hidden" name="action" value="updatePlan"/>
                    <input type="number" name="planId" placeholder="Plan ID" required/>
                    <input type="text" name="planName" placeholder="Plan Name" required/>
                    <input type="number" name="durationDays" placeholder="Duration Days" required/>
                    <input type="number" step="0.01" name="price" placeholder="Price" required/>
                    <textarea name="description" placeholder="Description"></textarea>
                    <button class="btn-secondary" type="submit">Update Plan</button>
                </form>
                <form action="${pageContext.request.contextPath}/admin" method="post">
                    <input type="hidden" name="action" value="deletePlan"/>
                    <input type="number" name="planId" placeholder="Plan ID" required/>
                    <button class="btn-danger" type="submit">Delete Plan</button>
                </form>
            </div>
        </div>

        <div class="col-12 card">
            <h3>Attendance Control</h3>
            <div class="grid-2">
                <form action="${pageContext.request.contextPath}/admin" method="post">
                    <input type="hidden" name="action" value="checkIn"/>
                    <select name="userId" required>
                        <option value="">Member for check-in</option>
                        <% if (members != null) for (User m : members) { %>
                        <option value="<%= m.getUserId() %>"><%= m.getUserId() %> - <%= m.getFullName() %></option>
                        <% } %>
                    </select>
                    <button type="submit">Check In</button>
                </form>
                <form action="${pageContext.request.contextPath}/admin" method="post">
                    <input type="hidden" name="action" value="checkOut"/>
                    <select name="userId" required>
                        <option value="">Member for check-out</option>
                        <% if (members != null) for (User m : members) { %>
                        <option value="<%= m.getUserId() %>"><%= m.getUserId() %> - <%= m.getFullName() %></option>
                        <% } %>
                    </select>
                    <button class="btn-secondary" type="submit">Check Out</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
