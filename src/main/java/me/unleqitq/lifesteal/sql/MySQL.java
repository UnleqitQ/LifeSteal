package me.unleqitq.lifesteal.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;


public class MySQL {
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String database;
	
	private Connection connection;
	
	
	public MySQL(String host, int port, String username, String password, String database) {
		this.database = database;
		this.host = host;
		this.port = port;
		this.password = password;
		this.username = username;
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		if (!isConnected()) {
			connection =
				DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
					username, password);
		}
	}
	
	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		}
		catch (SQLException ex) {
			return false;
		}
	}
	
	public void disconnect() {
		if (isConnected()) {
			try {
				connection.close();
				connection = null;
			}
			catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	public Connection getConnection() {
		return connection;
	}
	
	
	public String getDatabase() {
		return database;
	}
	
}
