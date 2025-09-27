import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/ComplaintServlet")
@MultipartConfig   // Required for handling file uploads
public class ComplaintServlet extends HttpServlet {

    private static final String url = "jdbc:mysql://localhost:3306/users";
    private static final String user = "root";
    private static final String pass = "klnp@2005";

    // Directory to save uploaded files (change path as needed)
    private static final String UPLOAD_DIR = "C:/uploads";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        // Get normal form fields
        String firstname = req.getParameter("fir");
        String lastname = req.getParameter("las");
        String email = req.getParameter("email");
        String district = req.getParameter("dis");
        String pincode = req.getParameter("zip");
        String date = req.getParameter("dor");
        String incidentLocation = req.getParameter("incloc");
        String complaintTitle = req.getParameter("comtit");
        String complaintDescription = req.getParameter("comdet");
        String severity = req.getParameter("sev");
        String desiredOutcome = req.getParameter("desout");

        // Get uploaded files
        Part imagePart = req.getPart("img");
        Part audioPart = req.getPart("file");
        Part signaturePart = req.getPart("fil");

        // Save files locally and get file paths
        String imagePath = saveFile(imagePart, "image");
        String audioPath = saveFile(audioPart, "audio");
        String signaturePath = saveFile(signaturePart, "signature");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);

            String sql = "INSERT INTO complaints (firstname, lastname, email, district, pincode, date_of_incident, incident_location, complaint_title, complaint_description, severity, image_path, audio_path, desired_outcome, signature_path) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstname);
            pstmt.setString(2, lastname);
            pstmt.setString(3, email);
            pstmt.setString(4, district);
            pstmt.setString(5, pincode);
            pstmt.setString(6, date);
            pstmt.setString(7, incidentLocation);
            pstmt.setString(8, complaintTitle);
            pstmt.setString(9, complaintDescription);
            pstmt.setString(10, severity);
            pstmt.setString(11, imagePath);
            pstmt.setString(12, audioPath);
            pstmt.setString(13, desiredOutcome);
            pstmt.setString(14, signaturePath);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                res.getWriter().println("Complaint submitted successfully!");
            } else {
                res.getWriter().println("Failed to submit complaint.");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            res.getWriter().println("Database error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to save file and return file path
    private String saveFile(Part part, String type) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        String fileName = type + "_" + System.currentTimeMillis() + "_" + part.getSubmittedFileName();
        String filePath = UPLOAD_DIR + File.separator + fileName;

        try (InputStream input = part.getInputStream();
             FileOutputStream output = new FileOutputStream(filePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return filePath;
    }
}
