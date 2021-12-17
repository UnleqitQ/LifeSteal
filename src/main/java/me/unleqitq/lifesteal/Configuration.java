package me.unleqitq.lifesteal;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public class Configuration {
	
	FileConfiguration config;
	ConfigurationSection mysqlSection;
	public String mysqlHost = "";
	public int mysqlPort = 0;
	public String mysqlDatabase = "";
	public String mysqlUsername = "";
	public String mysqlPassword = "";
	public boolean banOnDeath = false;
	
	public Configuration() {
		config = LifeSteal.plugin.getConfig();
	}
	
	public void loadConfig() {
		LifeSteal.plugin.saveDefaultConfig();
		loadConfigVars();
	}
	
	public void loadConfigVars() {
		banOnDeath = config.getBoolean("banOnNoHearts");
		mysqlSection = config.getConfigurationSection("mysql");
		if (mysqlSection != null) {
			mysqlHost = mysqlSection.getString("host");
			mysqlPort = mysqlSection.getInt("port");
			mysqlDatabase = mysqlSection.getString("database");
			mysqlUsername = mysqlSection.getString("username");
			mysqlPassword = mysqlSection.getString("password");
		}
		else {
			throw new IllegalStateException();
		}
	}
	
}
