package me.unleqitq.lifesteal.listeners;


import static me.unleqitq.lifesteal.Utils.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.unleqitq.lifesteal.LifeSteal;
import me.unleqitq.lifesteal.Utils;


public class PlayerListener implements Listener {
	
	
	public PlayerListener() {
		Bukkit.getPluginManager().registerEvents(this, LifeSteal.plugin);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onKill(PlayerDeathEvent event) {
		// Bukkit.getLogger().info("-------------- Kill");
		// Bukkit.getLogger().info("[LifeSteal] " + "Player Kill Event");
		Player killed = event.getEntity();
		Player killer = event.getEntity().getKiller();
		
		if (LifeSteal.takesPart(killed)) {
			double scaleAmount = 2;
			if (killer != null) {
				if (LifeSteal.takesPart(killer)) {
					addHealth(killer, scaleAmount);
					addHealth(killed, -scaleAmount);
					return;
				}
			}
			else {
				addHealth(killed, -scaleAmount);
			}
		}
		
		
		// String killerUUID = Configuration.getKiller(killed.getUniqueId());
		// if (killerUUID != null) {
		// }
		// Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
		// if (player != null)
		// killed.setSpectatorTarget(player);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;
		ItemMeta meta = event.getItem().getItemMeta();
		ItemMeta heartMeta = LifeSteal.plugin.heartItem.getItemMeta();
		if (meta == null || heartMeta == null)
			return;
		if (meta.getDisplayName().equalsIgnoreCase(heartMeta.getDisplayName()) &&
			meta.getLore() != null
			&& meta.getLore().equals(heartMeta.getLore())
			&& event.getItem().getType() == LifeSteal.plugin.heartItem.getType()) {
			event.setCancelled(true);
			Utils.addHealth(event.getPlayer(), 2);
			if (event.getHand() != null) {
				ItemStack stack =
					event.getItem().getAmount() - 1 == 0 ? new ItemStack(Material.AIR, 2) :
						event.getItem();
				stack.setAmount(stack.getAmount() - 1);
				event.getPlayer().getInventory().setItem(event.getHand(), stack);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		// Bukkit.getLogger().info("-------------- Respawn");
		// Bukkit.getLogger().info("[LifeSteal] " + "Player Respawn Event");
		Player respawned = event.getPlayer();
		setHealth(respawned, LifeSteal.plugin.dataTable.heartsMap.get(respawned.getUniqueId()) * 2);
		// String killerUUID = Configuration.getKiller(respawned.getUniqueId());
		// if (killerUUID != null) {
		// Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
		// if (player != null)
		// respawned.setSpectatorTarget(player);
		// }
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		// Bukkit.getLogger().info("-------------- Join");
		// Bukkit.getLogger().info("[LifeSteal] " + "Player Join Event");
		// Bukkit.getLogger().info("-------------- Add Mathod");
		LifeSteal.plugin.dataTable.addPlayer(event.getPlayer());
		// Bukkit.getLogger().info("-------------- SetHealth Method");
		setHealthWithoutUpdate(event.getPlayer(),
			LifeSteal.plugin.dataTable.heartsMap.get(event.getPlayer().getUniqueId()) * 2);
		// Bukkit.getLogger().info("-------------- stop");
		if (LifeSteal.plugin.dataTable.healthMap.containsKey(event.getPlayer().getUniqueId())) {
			// Bukkit.getLogger().info("[Lifesteal] Player loaded with "
			// +
			// Main.plugin.dataTable.healthMap.get(event.getPlayer().getUniqueId())
			// + " health");
			event.getPlayer().setHealth(LifeSteal.plugin.dataTable.healthMap.get(event.getPlayer().getUniqueId()));
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(LifeSteal.plugin, () -> {
			LifeSteal.plugin.updatePlayers.put(event.getPlayer().getUniqueId(), event.getPlayer().getUniqueId());
		}, 400);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPreJoin(AsyncPlayerPreLoginEvent event) {
		Bukkit.getLogger().info("-------------- PreJoin");
		Bukkit.getLogger().info("[LifeSteal] " + "Player PreJoin Event");
		// if (Configuration.isBanned(event.getUniqueId()))
		// event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
		// Utils.getFromText(
		// Configuration.getBanMessage(event.getUniqueId())));
		LifeSteal.plugin.dataTable.addPlayer(event.getUniqueId());
		if (isEliminated(event.getUniqueId())) {
			// String killerUUID = Configuration.getKiller(event.getUniqueId());
			// if (killerUUID == null) {
			// event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
			// Utils.getFromText(
			// Configuration.getKillerNotOnline()));
			// return;
			// }
			// Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
			// if (player == null) {
			// UUID id = UUID.fromString(killerUUID);
			// if
			// (!Configuration.getKilled(id).equals(event.getUniqueId().toString()))
			// {
			// Configuration.reviveOnlyDead(id);
			// OfflinePlayer off = Bukkit.getOfflinePlayer(id);
			// // Configuration.banID(id,
			// // Configuration.getBanMessage().replace("%player%",
			// // off.getName() == null ? "unknown" : off.getName()));
			// }
			// event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
			// Utils.getFromText(
			// Configuration.getKillerNotOnline()));
			// }
		}
	}
	
	// @EventHandler (priority = EventPriority.HIGHEST)
	// public void onDisconnect(PlayerQuitEvent event) {
	// String killedUUID =
	// Configuration.getKilled(event.getPlayer().getUniqueId());
	// if (killedUUID != null) {
	// Player player = Bukkit.getPlayer(UUID.fromString(killedUUID));
	// if (player != null) {
	// player.kickPlayer(
	// Utils.getFromText(
	// Configuration.getKillerDisconnected()));
	// }
	// }
	// }
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onGamemodeSwitch(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		if (isEliminated(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent event) {
		// Bukkit.getLogger().info("-------------- Kick");
		// Bukkit.getLogger().info("[LifeSteal] Remove player with uuid " +
		// event.getPlayer().getUniqueId());
		LifeSteal.plugin.updatePlayers.remove(event.getPlayer().getUniqueId());
		LifeSteal.plugin.dataTable.updatePlayer(event.getPlayer());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		// Bukkit.getLogger().info("-------------- Leave");
		// Bukkit.getLogger().info("[LifeSteal] Remove player with uuid " +
		// event.getPlayer().getUniqueId());
		LifeSteal.plugin.updatePlayers.remove(event.getPlayer().getUniqueId());
		LifeSteal.plugin.dataTable.updatePlayer(event.getPlayer());
	}
	
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			LifeSteal.plugin.dataTable.updatePlayer(player);
		}
	}
	
}
