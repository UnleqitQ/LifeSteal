package me.unleqitq.lifesteal.storage;

import java.sql.SQLException;
import java.util.UUID;

public interface IStorage {
	
	int getHearts(UUID player);
	
	void setHearts(UUID player, int amount);
	
	default void load() {}
	default void save() {}
	
	default void connect() throws SQLException {}
	default void setup() {}
	default void disconnect() {}
	
}
