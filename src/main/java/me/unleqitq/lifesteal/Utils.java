package me.unleqitq.lifesteal;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;


public class Utils {
	
	public static double parseHearts(double hp) {
		return hp + Double.parseDouble(String.valueOf(hp).replaceFirst("\\.0+$", ""));
	}
	
	public static AttributeInstance getMaxHealth(Player player) {
		return player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
	}
	
	public static void addHealth(Player player, double scale) {
		// Bukkit.getLogger().info("[LifeSteal] " + "adding to health " +
		// scale);
		AttributeInstance maxHp = getMaxHealth(player);
		
		if (maxHp.getBaseValue() + scale <= 0) {
			maxHp.setBaseValue(20);
			eliminate(player);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), true);
			LifeSteal.plugin.dataTable.heartsMap.put(player.getUniqueId(), 0.0);
			LifeSteal.plugin.deadPlayers.add(player.getUniqueId());
		}
		else {
			maxHp.setBaseValue(LifeSteal.plugin.dataTable.heartsMap.get(player.getUniqueId()) * 2 + scale);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), false);
			LifeSteal.plugin.dataTable.heartsMap.put(player.getUniqueId(), maxHp.getBaseValue() / 2);
			LifeSteal.plugin.deadPlayers.remove(player.getUniqueId());
		}
		// Bukkit.getLogger().info("[LifeSteal] " + "health is " +
		// maxHp.getBaseValue());
		player.setHealthScaled(false);
		LifeSteal.plugin.dataTable.updatePlayer(player);
	}
	
	public static void setHealth(Player player, double scale) {
		AttributeInstance maxHp = getMaxHealth(player);
		
		if (scale <= 0) {
			maxHp.setBaseValue(20);
			eliminate(player);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), true);
			LifeSteal.plugin.deadPlayers.add(player.getUniqueId());
		}
		else {
			maxHp.setBaseValue(scale);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), false);
			LifeSteal.plugin.deadPlayers.remove(player.getUniqueId());
		}
		// Bukkit.getLogger().info("[LifeSteal] " + "health is " +
		// maxHp.getBaseValue());
		player.setHealthScaled(false);
		LifeSteal.plugin.dataTable.heartsMap.put(player.getUniqueId(), scale / 2);
		
		LifeSteal.plugin.dataTable.updatePlayer(player);
	}
	
	public static void setHealthWithoutUpdate(Player player, double scale) {
		AttributeInstance maxHp = getMaxHealth(player);
		
		if (scale <= 0) {
			maxHp.setBaseValue(20);
			eliminate(player);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), true);
			LifeSteal.plugin.deadPlayers.add(player.getUniqueId());
		}
		else {
			maxHp.setBaseValue(scale);
			LifeSteal.plugin.dataTable.eliminatedMap.put(player.getUniqueId(), false);
			LifeSteal.plugin.deadPlayers.remove(player.getUniqueId());
		}
		// Bukkit.getLogger().info("[LifeSteal] " + "health is " +
		// maxHp.getBaseValue());
		player.setHealthScaled(false);
		LifeSteal.plugin.dataTable.heartsMap.put(player.getUniqueId(), scale / 2);
		
	}
	
	public static void eliminate(Player player) {
		
		// if (Configuration.shouldBan()) {
		// // Configuration.addElimination(player, id);
		// // if (Configuration.shouldBroadcastBan())
		// // Bukkit.broadcastMessage(
		// // getFromText(""));
		// // Configuration.banID(player.getUniqueId(), "");
		//
		// // Use this rather than the player#kick or player#kickPlayer to
		// // support multiple server software
		// // Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
		// // String.format("kick %s %s", player.getName(),
		// // Configuration.getBanMessage().replace("%player%", killerName)));
		// }
		player.setGameMode(GameMode.SPECTATOR);
		// player.setSpectatorTarget(killer);
		// Configuration.addElimination(player, id);
	}
	
	public static void update(Player player) {
		if (LifeSteal.plugin.dataTable.heartsMap.get(player.getUniqueId()) <= 0) {
			eliminate(player);
		}
	}
	
	public static String getFromText(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static boolean isEliminated(UUID uuid) {
		return LifeSteal.plugin.dataTable.eliminatedMap.get(uuid);
	}
	
}
