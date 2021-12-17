package me.unleqitq.lifesteal.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.unleqitq.lifesteal.LifeSteal;


public class GetHealthCommand extends Command {
	
	private LifeSteal plugin;
	
	
	public GetHealthCommand(LifeSteal plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		// System.out.println(args);
		if (args.length >= 1) {
			try {
				Player player = plugin.getServer().getPlayer(args[0]);
				sender.sendMessage(
					"Hearts: " + plugin.dataTable.heartsMap.get(player.getUniqueId()));
				return true;
			}
			catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		else {
			Player player = (Player) sender;
			sender.sendMessage(
				"Hearts: " + plugin.dataTable.heartsMap.get(player.getUniqueId()));
			return true;
		}
		return false;
	}
	
}
