package ua.nure.ageev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ConnectionDB {
	public static void main(String[] args) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ResourceBundle resource = ResourceBundle.getBundle("app");
		String CONNECTION_URL = resource.getString("connection.url");
		try {
			DriverManager.getConnection(CONNECTION_URL);
			con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/library?user=ageev&password=12345678");
			System.out.println("Connected!");
			st = con.createStatement();

			rs = st.executeQuery("SELECT * FROM authors");
			rs.next();
			System.out.println(rs.getString(2));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
