package com.appdynamics.aggregators;

import com.appdynamics.Monitor;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;

public class SimpleQueryDB implements IMetricAggregator {

	private static final Logger logger = Logger.getLogger(Monitor.class);

	private Connection conn;

	public int computeMetric(List<Object> argsOpt) {
		String dbURL = (String) argsOpt.get(0);
		String username = (String) argsOpt.get(1);
		String password = (String) argsOpt.get(2);
		String query = (String) argsOpt.get(3);

		int result = 0;
		if(connectDB(dbURL, username, password)) {
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				try (ResultSet rs = stmt.executeQuery(query)) {
					while (rs.next()) {
						result = rs.getInt(1);
					}
				}
			} catch (SQLException e) {
				logger.error(e.getLocalizedMessage());
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error(e.getLocalizedMessage());
					}
				}
			}
			disconnectDB(conn);
		}
		return result;
	}

	private boolean connectDB(String dbUrl, String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbUrl, username, password);
		} catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException e) {
			logger.error(e.getLocalizedMessage());
			return false;
		}
		return true;
	}

	private void disconnectDB(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage());
		}
	}
}
