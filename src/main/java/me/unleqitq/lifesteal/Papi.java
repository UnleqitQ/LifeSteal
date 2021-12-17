package me.unleqitq.lifesteal;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Papi extends PlaceholderExpansion {
	
	private LifeSteal plugin;
	
	public Papi(LifeSteal plugin) {
		this.plugin = plugin;
	}
	
	public boolean persist() {
		return true;
	}
	
	public boolean canRegister() {
		return true;
	}
	
	public String getAuthor() {
		return this.plugin.getDescription().getAuthors().toString();
	}
	
	public String getIdentifier() {
		return "lifesteal";
	}
	
	public String getVersion() {
		return this.plugin.getDescription().getVersion();
	}
	
	public String onPlaceholderRequest(Player player, String identifier) {
		if (player == null) { return null; }
		identifier = identifier.toLowerCase();
		
		if (identifier.contains("hearts")) {
			return Double.toString(plugin.dataTable.heartsMap.get(player.getUniqueId()));
		}
		
		return "0";
	}
	
}


/*
 * Location:
 * C:\Users\quent.DESKTOP-L0VD1RP\Downloads\AmongUs.jar!\com\nktfh100\AmongUs\
 * main\SomeExpansion.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.3
 */