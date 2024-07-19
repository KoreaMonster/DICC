import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/submit-appointment")
public class AppointmentServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String patientName = request.getParameter("patientName");
        String caregiverName = request.getParameter("caregiverName");
        String appointmentDateStr = request.getParameter("appointmentDate");
        String notes = request.getParameter("notes");

        try {
            LocalDateTime appointmentDate = LocalDateTime.parse(appointmentDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            boolean success = saveAppointmentToDatabase(patientName, caregiverName, appointmentDate, notes);

            if (success) {
                response.sendRedirect("/appointment-confirmation.html?status=success");
            } else {
                response.sendRedirect("/appointment-confirmation.html?status=error");
            }
        } catch (Exception e) {
            getServletContext().log("Error in AppointmentServlet", e);
            response.sendRedirect("/appointment-confirmation.html?status=error");
        }
    }

    private boolean saveAppointmentToDatabase(String patientName, String caregiverName, LocalDateTime appointmentDate, String notes) {
        String sql = "INSERT INTO appointments (patient_name, caregiver_name, appointment_date, notes) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            getServletContext().log("Oracle JDBC Driver not found", e);
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientName);
            pstmt.setString(2, caregiverName);
            pstmt.setTimestamp(3, Timestamp.valueOf(appointmentDate));
            pstmt.setString(4, notes);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            getServletContext().log("Database error", e);
            return false;
        }
    }
}