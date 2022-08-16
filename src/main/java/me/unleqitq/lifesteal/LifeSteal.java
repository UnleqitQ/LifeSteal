package me.unleqitq.lifesteal;

import me.unleqitq.commandframework.CommandManager;
import me.unleqitq.commandframework.building.argument.IntegerArgument;
import me.unleqitq.commandframework.building.argument.OfflinePlayerArgument;
import me.unleqitq.commandframework.building.command.FrameworkCommand;
import me.unleqitq.lifesteal.storage.FileStorage;
import me.unleqitq.lifesteal.storage.IStorage;
import me.unleqitq.lifesteal.storage.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;


public class LifeSteal extends JavaPlugin {
	
	private static LifeSteal instance;
	
	public Object papiHook;
	public static IStorage storage;
	private CommandManager commandManager;
	
	@Override
	public void onEnable() {
		instance = this;
		commandManager = new CommandManager(this);
		registerCommands();
		saveDefaultConfig();
		reloadConfig();
		
		if (getConfig().getBoolean("mysql.enabled")) {
			storage = new MySQL(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"),
					getConfig().getString("mysql.username"), getConfig().getString("mysql.password"),
					getConfig().getString("mysql.database"));
		}
		else {
			storage = new FileStorage();
		}
		try {
			storage.connect();
			storage.disconnect();
		}
		catch (SQLException ex) {
			Bukkit.getLogger().warning("[LifeSteal] " + "Cannot connect to Database");
		}
		storage.load();
		storage.setup();
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			papiHook = new Papi();
			((Papi) papiHook).register();
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Plan")) {
			LifestealDataExtension.register();
		}
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	private void registerCommands() {
		FrameworkCommand.Builder<CommandSender> topBuilder = FrameworkCommand.commandBuilder("lifesteal");
		commandManager.register(
				topBuilder.subCommand("set").permission("lifesteal.set").argument(OfflinePlayerArgument.of("player"))
						.argument(IntegerArgument.of("hearts")).handler(c -> {
							CommandSender sender = c.getSender();
							OfflinePlayer p = c.get("player");
							int hearts = c.get("hearts");
							storage.setHearts(p.getUniqueId(), hearts);
							storage.save();
							if (p.isOnline())
								updatePlayer(p.getPlayer());
							sender.sendMessage(p.getName() + " now has " + hearts + " hearts");
							return true;
						}));
		commandManager.register(
				topBuilder.subCommand("add").permission("lifesteal.set").argument(OfflinePlayerArgument.of("player"))
						.argument(IntegerArgument.of("amount")).handler(c -> {
							CommandSender sender = c.getSender();
							OfflinePlayer p = c.get("player");
							int amount = c.get("amount");
							int hearts = storage.getHearts(p.getUniqueId()) + amount;
							storage.setHearts(p.getUniqueId(), hearts);
							storage.save();
							if (p.isOnline())
								updatePlayer(p.getPlayer());
							sender.sendMessage(p.getName() + " now has " + hearts + " hearts");
							return true;
						}));
		commandManager.register(
				topBuilder.subCommand("get").permission("lifesteal.get").argument(OfflinePlayerArgument.of("player"))
						.handler(c -> {
							CommandSender sender = c.getSender();
							OfflinePlayer p = c.get("player");
							int hearts = storage.getHearts(p.getUniqueId());
							sender.sendMessage(p.getName() + " has " + hearts + " hearts");
							return true;
						}));
	}
	
	@Override
	public void onDisable() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && papiHook != null) {
			((Papi) papiHook).unregister();
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Plan")) {
			LifestealDataExtension.unregister();
		}
		storage.disconnect();
	}
	
	public static LifeSteal getInstance() {
		return instance;
	}
	
	public static int getHearts(UUID player) {
		return storage.getHearts(player);
	}
	
	public static void setHearts(UUID player, int amount) {
		storage.setHearts(player, amount);
		updatePlan(player);
	}
	
	public static void updatePlayer(Player player) {
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		double prev = attribute.getValue();
		attribute.getModifiers().stream().filter(am -> am.getName().equals("lifesteal"))
				.forEach(attribute::removeModifier);
		attribute.addModifier(new AttributeModifier("lifesteal", getHearts(player.getUniqueId()) * 2 - 20,
				AttributeModifier.Operation.ADD_NUMBER));
		try {
			player.setHealth(player.getHealth() + attribute.getValue() - prev);
		}
		catch (IllegalArgumentException ignored) {
		}
		player.setHealthScaled(false);
		updatePlan(player.getUniqueId());
	}
	
	public static void updatePlan(UUID player) {
		if (Bukkit.getPluginManager().isPluginEnabled("Plan")) {
			LifestealDataExtension.update(player);
		}
	}
	
}
