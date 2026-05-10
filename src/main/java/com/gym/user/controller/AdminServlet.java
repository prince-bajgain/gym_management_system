package com.gym.user.controller;

import com.gym.dao.AttendanceDAO;
import com.gym.dao.MembershipDAO;
import com.gym.dao.NotificationDAO;
import com.gym.dao.PaymentDAO;
import com.gym.model.MembershipPlan;
import com.gym.user.model.User;
import com.gym.user.model.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private String e(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "dashboard";
        }

        if ("dashboard".equals(action)) {
            request.setAttribute("totalMembers", userDAO.getTotalMembersCount());
            request.setAttribute("activeMemberships", membershipDAO.getActiveMembershipCount());
            request.setAttribute("todayAttendance", attendanceDAO.getTodayAttendanceCount());
            request.setAttribute("totalRevenue", paymentDAO.getTotalRevenue());
            request.setAttribute("members", userDAO.searchMembers("", "ALL"));
            request.setAttribute("plans", membershipDAO.getAllPlans());
            request.getRequestDispatcher("/views/admin-dashboard.jsp").forward(request, response);
        } else if ("members".equals(action)) {
            String search = request.getParameter("search") == null ? "" : request.getParameter("search");
            String status = request.getParameter("status") == null ? "ALL" : request.getParameter("status");
            List<User> users = userDAO.searchMembers(search, status);
            request.setAttribute("users",users);
            request.setAttribute("search", search);
            request.setAttribute("status", status);
            request.getRequestDispatcher("/views/view-members.jsp").forward(request,response);
        } else if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = userDAO.deleteUser(id);
                response.sendRedirect("admin?action=members&" + (ok ? "success=" + e("Member deleted.") : "error=" + e("Could not delete member.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=members&error=" + e("Invalid member id."));
            }
        } else {
            response.sendRedirect("admin?action=dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("addMember".equals(action)) {
            String fullName = request.getParameter("fullName") == null ? "" : request.getParameter("fullName").trim();
            String email = request.getParameter("email") == null ? "" : request.getParameter("email").trim().toLowerCase();
            String phone = request.getParameter("phone") == null ? "" : request.getParameter("phone").trim();
            String password = request.getParameter("password") == null ? "" : request.getParameter("password");
            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Name, email and password are required."));
                return;
            }
            if (password.length() < 6) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Password must be at least 6 characters."));
                return;
            }
            if (userDAO.emailExists(email)) {
                response.sendRedirect("admin?action=dashboard&error=" + e("A user with this email already exists."));
                return;
            }
            User user = new User(
                    fullName,
                    email,
                    password,
                    phone,
                    "MEMBER"
            );
            boolean ok = userDAO.registerUser(user);
            response.sendRedirect("admin?action=members&" + (ok ? "success=" + e("Member added successfully.") : "error=" + e("Could not add member.")));
        } else if ("updateMember".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String email = request.getParameter("email") == null ? "" : request.getParameter("email").trim().toLowerCase();
                if (!userDAO.userExists(userId)) {
                    response.sendRedirect("admin?action=members&error=" + e("Member does not exist."));
                    return;
                }
                if (userDAO.emailExistsForOtherUser(email, userId)) {
                    response.sendRedirect("admin?action=members&error=" + e("Another user already uses that email."));
                    return;
                }
                User user = new User();
                user.setUserId(userId);
                user.setFullName(request.getParameter("fullName"));
                user.setEmail(email);
                user.setPhone(request.getParameter("phone"));
                user.setRole(request.getParameter("role"));
                boolean ok = userDAO.updateMemberByAdmin(user);
                response.sendRedirect("admin?action=members&" + (ok ? "success=" + e("Member updated.") : "error=" + e("Could not update member.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=members&error=" + e("Invalid member data."));
            }
        } else if ("assignMembership".equals(action)) {
            int userId;
            int planId;
            LocalDate start;
            try {
                userId = Integer.parseInt(request.getParameter("userId"));
                planId = Integer.parseInt(request.getParameter("planId"));
                start = LocalDate.parse(request.getParameter("startDate"));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid membership input."));
                return;
            }
            String endDateStr = request.getParameter("endDate");
            LocalDate end;
            if (endDateStr == null || endDateStr.isBlank()) {
                MembershipPlan plan = membershipDAO.getPlanById(planId);
                if (plan == null) {
                    response.sendRedirect("admin?action=dashboard&error=" + e("Selected plan not found."));
                    return;
                }
                int duration = plan.getDurationDays();
                end = start.plusDays(duration);
            } else {
                end = LocalDate.parse(endDateStr);
            }
            if (end.isBefore(start)) {
                response.sendRedirect("admin?action=dashboard&error=" + e("End date cannot be before start date."));
                return;
            }
            boolean ok = membershipDAO.assignMembership(userId, planId, Date.valueOf(start), Date.valueOf(end), request.getParameter("status"));
            if (ok) {
                notificationDAO.addNotification(userId, "Membership assigned/updated by admin.", "SYSTEM");
            }
            response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Membership assigned.") : "error=" + e("Could not assign membership.")));
        } else if ("createPlan".equals(action)) {
            try {
                String planName = request.getParameter("planName") == null ? "" : request.getParameter("planName").trim();
                int duration = Integer.parseInt(request.getParameter("durationDays"));
                double price = Double.parseDouble(request.getParameter("price"));
                if (planName.isBlank() || duration <= 0 || price <= 0) {
                    response.sendRedirect("admin?action=dashboard&error=" + e("Plan name, duration and price must be valid."));
                    return;
                }
                boolean ok = membershipDAO.createPlan(planName, duration, price, request.getParameter("description"));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Plan created.") : "error=" + e("Could not create plan.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid plan input."));
            }
        } else if ("updatePlan".equals(action)) {
            try {
                int planId = Integer.parseInt(request.getParameter("planId"));
                String planName = request.getParameter("planName") == null ? "" : request.getParameter("planName").trim();
                int duration = Integer.parseInt(request.getParameter("durationDays"));
                double price = Double.parseDouble(request.getParameter("price"));
                if (planName.isBlank() || duration <= 0 || price <= 0) {
                    response.sendRedirect("admin?action=dashboard&error=" + e("Plan name, duration and price must be valid."));
                    return;
                }
                boolean ok = membershipDAO.updatePlan(planId, planName, duration, price, request.getParameter("description"));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Plan updated.") : "error=" + e("Could not update plan.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid plan update data."));
            }
        } else if ("deletePlan".equals(action)) {
            try {
                boolean ok = membershipDAO.deletePlan(Integer.parseInt(request.getParameter("planId")));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Plan deleted.") : "error=" + e("Could not delete plan. It may be in use.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid plan id."));
            }
        } else if ("updateMembershipStatus".equals(action)) {
            try {
                int membershipId = Integer.parseInt(request.getParameter("membershipId"));
                boolean ok = membershipDAO.updateMembershipStatus(membershipId, request.getParameter("status"));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Membership status updated.") : "error=" + e("Could not update membership status.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid membership id."));
            }
        } else if ("recordPayment".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                double amount = Double.parseDouble(request.getParameter("amount"));
                if (amount <= 0) {
                    response.sendRedirect("admin?action=dashboard&error=" + e("Payment amount must be greater than zero."));
                    return;
                }
                boolean ok = paymentDAO.addPayment(
                        userId,
                        amount,
                        Date.valueOf(request.getParameter("paymentDate")),
                        request.getParameter("paymentMethod"),
                        request.getParameter("status")
                );
                if (ok) {
                    notificationDAO.addNotification(userId, "A payment record was added to your account.", "PAYMENT");
                }
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Payment recorded.") : "error=" + e("Could not record payment.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid payment data."));
            }
        } else if ("checkIn".equals(action)) {
            try {
                boolean ok = attendanceDAO.checkIn(Integer.parseInt(request.getParameter("userId")));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Check-in saved.") : "error=" + e("Could not check in user.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid user for check-in."));
            }
        } else if ("checkOut".equals(action)) {
            try {
                boolean ok = attendanceDAO.checkOutLatest(Integer.parseInt(request.getParameter("userId")));
                response.sendRedirect("admin?action=dashboard&" + (ok ? "success=" + e("Check-out saved.") : "error=" + e("No open check-in found for this user.")));
            } catch (Exception ex) {
                response.sendRedirect("admin?action=dashboard&error=" + e("Invalid user for check-out."));
            }
        } else {
            response.sendRedirect("admin?action=dashboard");
        }
    }
}
