package dbConnect;

import java.sql.*;

public class ViewCustomerTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/DBTEST";
        String user = "user1";       // 또는 "user1"
        String password = "user1";   // 또는 "user1"

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ DB 연결 성공");

            String sql = "SELECT * FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("📋 customer 테이블 내용:");

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String username = rs.getString("username");
                String passwordCol = rs.getString("password");
                String name = rs.getString("customer_name");
                String email = rs.getString("email");

                System.out.printf("%d | %s | %s | %s | %s\n",
                        id, username, passwordCol, name, email);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ 오류 발생");
            e.printStackTrace();
        }
    }
}
