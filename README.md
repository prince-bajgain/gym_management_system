# Gym Management System (Servlet + JSP + MySQL)

This project is a complete coursework-ready Gym Management System using:
- Java 17
- Jakarta Servlet + JSP (MVC style)
- MySQL
- Maven WAR packaging

## Implemented Features

### Admin
- Login authentication with BCrypt password hashing
- Dashboard overview (total members, active memberships, attendance, revenue)
- Member management (add, update, delete, view)
- Search by name/email and filter by membership status (active/expired/all)
- Membership management (assign plans, update status)
- Payment management (record payment and status)
- Attendance tracking (check-in/check-out)
- RBAC (admin routes protected)

### Member
- Profile management (update name/contact/password)
- Membership viewing (plan, start, expiry, days remaining)
- Attendance history (total visits + log)
- Payment history
- Notification view (payment/system/expiry)
- Workout plans (add, list, delete)
- RBAC (member routes protected)

## Database Setup

1. Open MySQL Workbench.
2. Run `database/schema.sql`.
3. Ensure DB name is `gym_db`.

## Project Structure

- `src/main/java/com/gym/user/controller` - Servlets (`UserServlet`, `AdminServlet`, `MemberServlet`)
- `src/main/java/com/gym/user/model` - User entity
- `src/main/java/com/gym/user/model/dao` - User DAO and interface
- `src/main/java/com/gym/dao` - Membership, payment, attendance, workout, notification DAOs
- `src/main/java/com/gym/model` - Domain model classes
- `src/main/java/com/gym/filters` - Authentication + RBAC filter
- `src/main/webapp/views` - JSP views for login/register/admin/member
- `database/schema.sql` - Full schema and base plans

## Run

1. Configure DB credentials in `src/main/java/com/gym/utils/DBConnection.java`.
2. Build:
   - `mvn clean package`
3. Deploy generated WAR from `target/` to Tomcat 10+.
4. Access app:
   - `http://localhost:8080/gym_management_system/`

## First Use

- Register an admin account from the registration page (choose role `ADMIN`).
- Login as admin and manage members/plans/payments.
- Register member accounts and test member dashboard features.
