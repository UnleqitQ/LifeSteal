package me.unleqitq.lifesteal.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.unleqitq.lifesteal.LifeSteal;


public class GetHeartItemCommand extends Command {
	
	private LifeSteal plugin;
	
	
	public GetHeartItemCommand(LifeSteal plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		// System.out.println(args);
		if (args.length >= 1) {
			try {
				Player player = (Player) sender;
				ItemStack itemStack = new ItemStack(LifeSteal.plugin.heartItem);
				itemStack.setAmount(Integer.valueOf(args[0]));
				player.getInventory().addItem(itemStack);
				return true;
			}
			catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		else {
			try {
				Player player = (Player) sender;
				ItemStack itemStack = new ItemStack(LifeSteal.plugin.heartItem);
				player.getInventory().addItem(itemStack);
				return true;
			}
			catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		return false;
	}
	
}
