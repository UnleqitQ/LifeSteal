package me.unleqitq.lifesteal.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_17_R1.CraftProfileBanList;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotCommand;

import me.unleqitq.lifesteal.LifeSteal;
import net.minecraft.util.datafix.fixes.DataConverterPlayerUUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;


public class DataTable {
	
	private MySQL sql;
	public HashMap<UUID, Double> heartsMap;
	public HashMap<UUID, Boolean> eliminatedMap;
	public HashMap<UUID, Double> healthMap;
	
	public DataTable(MySQL sql) {
		this.sql = sql;
		heartsMap = new HashMap<>();
		eliminatedMap = new HashMap<>();
		healthMap = new HashMap<>();
		createTable();
	}
	
	public void loadData() {
		Bukkit.getLogger().info("[LifeSteal] " + "=".repeat(100));
		try {
			PreparedStatement statement = sql.getConnection().prepareStatement("SELECT * FROM `playerdata`");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				double hearts = result.getDouble("Hearts");
				UUID id = UUID.fromString(result.getString("UUID"));
				boolean eliminated = result.getBoolean("Eliminated");
				double health = result.getDouble("Health");
				heartsMap.put(id, hearts);
				eliminatedMap.put(id, eliminated);
				Bukkit.getLogger().info("[LifeSteal] " + id + ": " + hearts + " Hearts, " + health + " Health, "
					+ (eliminated ? "eliminated" : "alive"));
				healthMap.put(id, health);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		Bukkit.getLogger().info("[LifeSteal] " + "=".repeat(100));
	}
	
	public void addPlayer(Player player) {
		player.setHealthScaled(false);
		if (heartsMap.containsKey(player.getUniqueId())) {
			// Bukkit.getLogger().info("[LifeSteal] " + "Player exists");
			return;
		}
		try {
			PreparedStatement statement = sql.getConnection().prepareStatement(
				"INSERT INTO `playerdata` (`UUID`, `Hearts`, `Eliminated`) VALUES ('"
					+ player.getUniqueId() + "', '10', '0') ");
			statement.executeUpdate();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		try {
			PreparedStatement statement = sql.getConnection()
				.prepareStatement("SELECT * FROM `playerdata` WHERE `UUID`='"
					+ player.getUniqueId() + "'");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				boolean eliminated = result.getBoolean("Eliminated");
				double hearts = result.getDouble("Hearts");
				UUID id = UUID.fromString(result.getString("UUID"));
				double health = result.getDouble("Health");
				// System.out.println(id + " " + hearts + " " + health);
				heartsMap.put(id, hearts);
				eliminatedMap.put(id, eliminated);
				healthMap.put(id, health);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		player.setHealthScaled(false);
		// Bukkit.getLogger().info("[LifeSteal] " + "Player added");
	}
	
	public void addPlayer(UUID player) {
		if (heartsMap.containsKey(player)) {
			// Bukkit.getLogger().info("[LifeSteal] " + "Player exists");
			return;
		}
		try {
			PreparedStatement statement = sql.getConnection().prepareStatement(
				"INSERT INTO `playerdata` (`UUID`, `Hearts`, `Eliminated`) VALUES ('"
					+ player + "', '10', '0') ");
			statement.executeUpdate();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		try {
			PreparedStatement statement = sql.getConnection()
				.prepareStatement("SELECT * FROM `playerdata` WHERE `UUID`='"
					+ player + "'");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				boolean eliminated = result.getBoolean("Eliminated");
				double hearts = result.getDouble("Hearts");
				UUID id = UUID.fromString(result.getString("UUID"));
				// System.out.println(id + " " + hearts + " " + health);
				heartsMap.put(id, hearts);
				eliminatedMap.put(id, eliminated);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		// Bukkit.getLogger().info("[LifeSteal] " + "Player added");
	}
	
	
	public void updatePlayer(Player player, double hearts, boolean eliminated) {
		// Bukkit.getLogger().info("[LifeSteal] " + "Saving " + player.getName()
		// + " with " + hearts + " hearts as "
		// + (eliminated ? "eliminated" : "alive") + " using parameters");
		if (!heartsMap.containsKey(player.getUniqueId())) { return; }
		// Bukkit.getLogger().info("[LifeSteal] " + player.getName() + "(" +
		// player.getUniqueId() + ") " + hearts);
		try {
			PreparedStatement statement = sql.getConnection().prepareStatement(
				"UPDATE `playerdata` SET `Hearts`='" + hearts + "', `Health`='" + player.getHealth()
					+ "', `Eliminated`='" + (eliminated ? "1" : "0")
					+ "' WHERE `UUID`='"
					+ player.getUniqueId() + "'");
			statement.executeUpdate();
		}
		catch (SQLException ex) {
			// ex.printStackTrace();
		}
		heartsMap.put(player.getUniqueId(), hearts);
		healthMap.put(player.getUniqueId(), player.getHealth());
	}
	
	public void updatePlayer(Player player) {
		// Bukkit.getLogger()
		// .info("[LifeSteal] " + "Saving " + player.getName() + " with " +
		// heartsMap.get(player.getUniqueId())
		// + " hearts and " + player.getHealth() + " Health as " +
		// (eliminatedMap.get(player.getUniqueId()) ?
		// "eliminated" : "alive")
		// + " using saved data");
		healthMap.put(player.getUniqueId(), player.getHealth());
		if (!heartsMap.containsKey(player.getUniqueId())) { return; }
		try {
			PreparedStatement statement = sql.getConnection().prepareStatement(
				"UPDATE `playerdata` SET `Hearts`='" + heartsMap.get(player.getUniqueId()) + "', `Health`='"
					+ player.getHealth() + "', `Eliminated`='"
					+ (eliminatedMap.get(player.getUniqueId()) ? 1 : 0) + "' WHERE `UUID`='"
					+ player.getUniqueId() + "'");
			statement.executeUpdate();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		// healthMap.put(Main.plugin.idTable.playerIdMap.get(player.getUniqueId()),
		// (float) player.getHealth());
	}
	
	// @SuppressWarnings ("deprecation")
	// public void updatePlayer(Player player) {
	// if
	// (!heartsMap.containsKey(Main.plugin.idTable.playerIdMap.get(player.getUniqueId())))
	// { return; }
	// try {
	// PreparedStatement statement = sql.getConnection().prepareStatement(
	// "UPDATE `playerdata` SET `Health`='" + player.getHealth()
	// + "' WHERE `ID`='"
	// + Main.plugin.idTable.playerIdMap.get(player.getUniqueId()) + "'");
	// statement.executeUpdate();
	// }
	// catch (SQLException ex) {
	// // ex.printStackTrace();
	// }
	// healthMap.put(Main.plugin.idTable.playerIdMap.get(player.getUniqueId()),
	// (float) player.getHealth());
	// player.setHealthScaled(false);
	// }
	
	public void loadPlayer(Player player) {
		loadPlayer(player, false);
	}
	
	@SuppressWarnings ("deprecation")
	public void loadPlayer(Player player, boolean updateHealth) {
		// System.out.println(healthMap.get(Main.plugin.idTable.playerIdMap.get(player.getUniqueId())));
		// player.setHealth(healthMap.get(Main.plugin.idTable.playerIdMap.get(player.getUniqueId())));
		double hearts = heartsMap.get(player.getUniqueId());
		if (hearts <= 0) {
			if (LifeSteal.plugin.configuration.banOnDeath) {
				LifeSteal.plugin.getServer().getBanList(BanList.Type.NAME)
					.addBan(player.getUniqueId().toString(), "No Hearts", Date.from(Instant.now().plusSeconds(30)), "");
			}
		}
		// Bukkit.getLogger().info("[LifeSteal] " + player.getName() + "(" +
		// player.getUniqueId() + ") " + hearts);
	}
	
	
	public void createTable() {
		PreparedStatement statement;
		try {
			statement = sql.getConnection().prepareStatement(
				"CREATE TABLE `playerdata` ( `UUID` VARCHAR(100) NOT NULL , `Hearts` DOUBLE NOT NULL , `Health` DOUBLE , `Eliminated` BOOL NOT NULL , PRIMARY KEY (`UUID`)) ENGINE = InnoDB; ");
			statement.executeUpdate();
		}
		catch (SQLException ex) {
			// ex.printStackTrace();
		}
	}
	
}
