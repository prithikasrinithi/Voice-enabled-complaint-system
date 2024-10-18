import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class LoginServlet extends HttpServlet {
    private static final String url = "jdbc:mysql://localhost:3306/users";
    private static final String user = "root";
    private static final String pass = "klnp@2005";
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Set response content type
        res.setContentType("text/html;charset=UTF-8");
        // Retrieve the form data
        String name = req.getParameter("name");
        String password = req.getParameter("psw");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the database
            conn = DriverManager.getConnection(url, user, pass);
            // Prepare SQL query to check if user exists with matching name and password
            String sql = "SELECT * FROM register WHERE name = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, password)
            // Execute the query
            rs = pstmt.executeQuery();
            // Check if user exists
            if (rs.next()) {
                res.sendRedirect("complaint.html");
            } else {
                res.getWriter().println("Invalid username or password.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            res.getWriter().println("Database error: " + e.getMessage());
        } finally {
            // Close the database resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
