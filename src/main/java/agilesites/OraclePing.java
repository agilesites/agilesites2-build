package agilesites;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OraclePing {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out
					.println("usage: user/pass@host:port/sid (oracle driver should be in the classpath)");
			System.exit(0);
		}

		// Database credentials
		String DB_URL = "jdbc:oracle:thin:" + args[0];
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL);

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT 2+2 from DUAL";
			ResultSet rs = stmt.executeQuery(sql);

			// STEP 5: Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				int r = rs.getInt(1);

				// Display values
				System.out.print("OK! According Oracle, 2+2 = " + r);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.print("KO! Sorry it did not work...");
		}
	}
}
