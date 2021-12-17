package me.unleqitq.lifesteal.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.unleqitq.lifesteal.LifeSteal;
import me.unleqitq.lifesteal.Utils;


public class SetHealthCommand extends Command {
	
	private LifeSteal plugin;
	
	
	public SetHealthCommand(LifeSteal plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			try {
				int hearts = Integer.valueOf(args[0]);
				// plugin.dataTable.updatePlayer((Player) sender, hearts, true);
				Utils.setHealth((Player) sender, hearts * 2);
				return true;
			}
			catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		else if (args.length == 2) {
			try {
				int hearts = Integer.valueOf(args[0]);
				Player player = plugin.getServer().getPlayer(args[1]);
				plugin.dataTable.updatePlayer(player, hearts, true);
				Utils.setHealth(player, hearts * 2);
				return true;
			}
			catch (Exception ex0) {
				try {
					int hearts = Integer.valueOf(args[1]);
					Player player = plugin.getServer().getPlayer(args[0]);
					plugin.dataTable.updatePlayer(player, hearts, true);
					Utils.setHealth(player, hearts * 2);
					return true;
				}
				catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
		return false;
	}
	
}
