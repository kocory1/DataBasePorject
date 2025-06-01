package User.dbConnect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db1 {
	public static void main (String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "root","1234"); // JDBC 연결
			System.out.println("DB 연결 완료");
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("DB 연결 오류");
		}
	}
	
	public static Connection getRootConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "root", "1234");
	}

	// ✅ user1 계정용 연결 (일반회원 로그인용)
	public static Connection getUserConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "user1", "user1");
	}
}


