import jakarta.servlet.ServletException; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class register extends HttpServlet{
    private static final String url = "jdbc:mysql://localhost:3306/users";
    private static final String user = "root";
    private static final String pass ="klnp@2005";
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        String name = req.getParameter("name");
        String password = req.getParameter("psw");
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,pass);
            String sql = "INSERT INTO register(name,password) values(?,?)";
            System.out.println(sql);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            int rows = pstmt.executeUpdate();
            if(rows>0) {
                res.sendRedirect("login.html");
            }
            else {
                res.getWriter().println("Failed to add user");
            }
        }catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            res.getWriter().println("Database error:" +e.getMessage());
        }finally {
            try {
                if(pstmt != null)pstmt.close();
                if(conn != null)conn.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
