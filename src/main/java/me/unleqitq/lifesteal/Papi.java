package me.unleqitq.lifesteal;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class Papi extends PlaceholderExpansion {
	
	public boolean persist() {
		return true;
	}
	
	public boolean canRegister() {
		return true;
	}
	
	public @NotNull String getAuthor() {
		return LifeSteal.getInstance().getDescription().getAuthors().toString();
	}
	
	public @NotNull String getIdentifier() {
		return "lifesteal";
	}
	
	public @NotNull String getVersion() {
		return LifeSteal.getInstance().getDescription().getVersion();
	}
	
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if (identifier.equalsIgnoreCase("hearts")) {
			return String.valueOf(LifeSteal.getHearts(player.getUniqueId()));
		}
		return "";
	}
	
}

