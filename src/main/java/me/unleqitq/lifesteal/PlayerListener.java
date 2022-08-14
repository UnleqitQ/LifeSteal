package me.unleqitq.lifesteal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player killer = event.getEntity().getKiller();
		int hearts = LifeSteal.getHearts(event.getEntity().getUniqueId());
		if (hearts > 0) {
			LifeSteal.setHearts(event.getEntity().getUniqueId(), hearts - 1);
			if (killer != null) {
				LifeSteal.setHearts(killer.getUniqueId(), LifeSteal.getHearts(killer.getUniqueId()) + 1);
				LifeSteal.updatePlayer(killer);
			}
			LifeSteal.updatePlayer(event.getEntity());
		}
		if (hearts <= 1) {
			event.getEntity().kickPlayer("§6You have no hearts left!");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (LifeSteal.getHearts(event.getPlayer().getUniqueId()) <= 0) {
			event.getPlayer().kickPlayer("§6You have no hearts left!");
		}
		else {
			LifeSteal.updatePlayer(event.getPlayer());
		}
	}
	
}
