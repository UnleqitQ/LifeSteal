package me.unleqitq.lifesteal.storage;

import java.sql.*;
import java.util.UUID;


public class MySQL implements IStorage {
	
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
	
	@Override
	public int getHearts(UUID player) {
		try {
			connect();
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM lifesteal WHERE player='" + player + "'");
			ResultSet rs = ps.executeQuery();
			int value = 10;
			if (rs.next()) {
				value = rs.getShort("hearts");
			}
			disconnect();
			return value;
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setHearts(UUID player, int amount) {
		try {
			connect();
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM lifesteal WHERE player='" + player + "'");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateShort("hearts", (short) amount);
			}
			else {
				connection.prepareStatement(
						"INSERT INTO lifesteal (player, hearts) VALUES ('" + player + "', " + amount + ")").execute();
			}
			disconnect();
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void connect() throws SQLException {
		if (!isConnected()) {
			connection =
					DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
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
	
	@Override
	public void setup() {
		try {
			connect();
			PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS lifesteal " +
					"(ID MEDIUMINT NOT NULL AUTO_INCREMENT, player TINYTEXT NOT NULL, hearts SMALLINT NOT NULL, " +
					"PRIMARY KEY (ID)), UNIQUE (ID,player)");
			ps.execute();
			disconnect();
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	
	public String getDatabase() {
		return database;
	}
	
}
