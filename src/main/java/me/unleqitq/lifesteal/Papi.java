package me.unleqitq.lifesteal;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


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
	
	public @NotNull String getAuthor() {
		return this.plugin.getDescription().getAuthors().toString();
	}
	
	public @NotNull String getIdentifier() {
		return "lifesteal";
	}
	
	public @NotNull String getVersion() {
		return this.plugin.getDescription().getVersion();
	}
	
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if (player == null) {
			return null;
		}
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