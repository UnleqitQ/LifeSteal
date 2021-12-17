package me.unleqitq.lifesteal.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public abstract class Command implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		
		return onCommand(sender, args);
	}
	
	public abstract boolean onCommand(CommandSender sender, String[] args);
	
}
