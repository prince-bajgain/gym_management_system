package com.gym.user.controller;

import com.gym.dao.AttendanceDAO;
import com.gym.dao.MembershipDAO;
import com.gym.dao.NotificationDAO;
import com.gym.dao.PaymentDAO;
import com.gym.dao.WorkoutDAO;
import com.gym.model.MemberMembership;
import com.gym.user.model.User;
import com.gym.user.model.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet("/member")
public class MemberServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final WorkoutDAO workoutDAO = new WorkoutDAO();
    private String e(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("user");
        int userId = sessionUser.getUserId();
        String action = request.getParameter("action");
        if (action == null) {
            action = "dashboard";
        }

        if ("dashboard".equals(action)) {
            User latestUser = userDAO.findById(userId);
            MemberMembership membership = membershipDAO.getLatestMembershipByUser(userId);

            long remainingDays = 0;
            if (membership != null) {
                remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate().toLocalDate());
                if (remainingDays >= 0 && remainingDays <= 7) {
                    notificationDAO.addNotification(userId, "Your membership expires in " + remainingDays + " day(s).", "EXPIRY");
                }
            }

            request.setAttribute("profile", latestUser);
            request.setAttribute("membership", membership);
            request.setAttribute("remainingDays", remainingDays);
            request.setAttribute("payments", paymentDAO.getPaymentsByUser(userId));
            request.setAttribute("attendanceList", attendanceDAO.getAttendanceByUser(userId));
            request.setAttribute("workouts", workoutDAO.getWorkoutsByUser(userId));
            request.setAttribute("notifications", notificationDAO.getNotificationsByUser(userId));
            request.getRequestDispatcher("/views/member-dashboard.jsp").forward(request, response);
        } else {
            response.sendRedirect("member?action=dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("user");
        int userId = sessionUser.getUserId();
        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            String fullName = request.getParameter("fullName") == null ? "" : request.getParameter("fullName").trim();
            if (fullName.isBlank()) {
                response.sendRedirect("member?action=dashboard&error=" + e("Full name is required."));
                return;
            }
            boolean ok = userDAO.updateProfile(userId, fullName, request.getParameter("phone"));
            response.sendRedirect("member?action=dashboard&" + (ok ? "success=" + e("Profile updated.") : "error=" + e("Could not update profile.")));
            return;
        } else if ("updatePassword".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            if (newPassword == null || newPassword.length() < 6) {
                response.sendRedirect("member?action=dashboard&error=" + e("New password must be at least 6 characters."));
                return;
            }
            boolean ok = userDAO.updatePassword(userId, currentPassword, newPassword);
            response.sendRedirect("member?action=dashboard&" + (ok ? "success=" + e("Password changed.") : "error=" + e("Current password is incorrect.")));
            return;
        } else if ("addWorkout".equals(action)) {
            String title = request.getParameter("title") == null ? "" : request.getParameter("title").trim();
            if (title.isBlank()) {
                response.sendRedirect("member?action=dashboard&error=" + e("Workout title is required."));
                return;
            }
            boolean ok = workoutDAO.addWorkoutPlan(userId, title, request.getParameter("description"), request.getParameter("goal"));
            response.sendRedirect("member?action=dashboard&" + (ok ? "success=" + e("Workout added.") : "error=" + e("Could not add workout.")));
            return;
        } else if ("updateWorkout".equals(action)) {
            try {
                boolean ok = workoutDAO.updateWorkoutPlan(
                        Integer.parseInt(request.getParameter("workoutId")),
                        userId,
                        request.getParameter("title"),
                        request.getParameter("description"),
                        request.getParameter("goal")
                );
                response.sendRedirect("member?action=dashboard&" + (ok ? "success=" + e("Workout updated.") : "error=" + e("Could not update workout.")));
            } catch (Exception ex) {
                response.sendRedirect("member?action=dashboard&error=" + e("Invalid workout id."));
            }
            return;
        } else if ("deleteWorkout".equals(action)) {
            try {
                boolean ok = workoutDAO.deleteWorkoutPlan(Integer.parseInt(request.getParameter("workoutId")), userId);
                response.sendRedirect("member?action=dashboard&" + (ok ? "success=" + e("Workout deleted.") : "error=" + e("Could not delete workout.")));
            } catch (Exception ex) {
                response.sendRedirect("member?action=dashboard&error=" + e("Invalid workout id."));
            }
            return;
        }
        response.sendRedirect("member?action=dashboard");
    }
}
