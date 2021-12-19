package me.unleqitq.lifesteal;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.unleqitq.lifesteal.commands.GetHealthCommand;
import me.unleqitq.lifesteal.commands.GetHeartItemCommand;
import me.unleqitq.lifesteal.commands.SetHealthCommand;
import me.unleqitq.lifesteal.listeners.PlayerListener;
import me.unleqitq.lifesteal.sql.DataTable;
import me.unleqitq.lifesteal.sql.MySQL;


public class LifeSteal extends JavaPlugin {
	
	public NamespacedKey heartKey;
	public ItemStack heartItem = new ItemStack(Material.RED_DYE, (short) 1);
	public NamespacedKey compressedDiamondKey;
	public NamespacedKey compressedDiamondKeyBack;
	public ItemStack compressedDiamondItem = new ItemStack(Material.DIAMOND, (short) 1);
	public MySQL sql;
	public static LifeSteal plugin;
	public ShapedRecipe heartRecipe;
	public ShapedRecipe compressedDiamondRecipeBack;
	public ShapedRecipe compressedDiamondRecipe;
	public DataTable dataTable;
	public Configuration configuration;
	public Set<UUID> deadPlayers;
	public ConcurrentMap<UUID, UUID> updatePlayers;
	
	public LifeSteal() {
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		updatePlayers = new ConcurrentHashMap<>();
		deadPlayers = new HashSet<>();
		heartKey = new NamespacedKey(this, "ls_heart_item");
		compressedDiamondKey = new NamespacedKey(this, "ls_compressed_diamond");
		compressedDiamondKeyBack = new NamespacedKey(this, "ls_compressed_diamond_back");
		ItemMeta itemMeta = Objects.requireNonNull(heartItem.getItemMeta());
		itemMeta.setCustomModelData(1001);
		itemMeta.setDisplayName(ChatColor.RED + "Extra heart.");
		itemMeta.setLore(List.of("Gives you an extra heart!"));
		itemMeta.setLocalizedName("ls_heart");
		heartItem.setItemMeta(itemMeta);
		itemMeta = Objects.requireNonNull(compressedDiamondItem.getItemMeta());
		itemMeta.setCustomModelData(1001);
		itemMeta.setDisplayName(ChatColor.AQUA + "Compressed Diamond");
		itemMeta.setLore(List.of("An impossible compressed Diamond"));
		itemMeta.setLocalizedName("ls_compresssed_diamond");
		compressedDiamondItem.setItemMeta(itemMeta);
		// System.out.println("XXXXXXX");
		registerCommand("lsgethearts", new GetHealthCommand(this));
		registerCommand("lssethearts", new SetHealthCommand(this));
		registerCommand("lsgetheartitem", new GetHeartItemCommand(this));
		
		heartRecipe = new ShapedRecipe(heartKey, heartItem);
		heartRecipe.shape("dod", "oeo", "dod");
		heartRecipe.setIngredient('d', new ExactChoice(compressedDiamondItem));
		heartRecipe.setIngredient('e', Material.DRAGON_EGG);
		heartRecipe.setIngredient('o', Material.OBSIDIAN);
		
		new PlayerListener();
		compressedDiamondRecipe = new ShapedRecipe(compressedDiamondKey, compressedDiamondItem);
		compressedDiamondRecipe.shape("d d", " D ", "d d");
		compressedDiamondRecipe.setIngredient('d', Material.DIAMOND);
		compressedDiamondRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
		
		compressedDiamondRecipeBack = new ShapedRecipe(compressedDiamondKeyBack, new ItemStack(Material.DIAMOND, 13));
		compressedDiamondRecipeBack.shape("d");
		compressedDiamondRecipeBack.setIngredient('d', new ExactChoice(compressedDiamondItem));
		
		Bukkit.addRecipe(compressedDiamondRecipe);
		Bukkit.addRecipe(compressedDiamondRecipeBack);
		Bukkit.addRecipe(heartRecipe);
		
		sql = new MySQL(configuration.mysqlHost, configuration.mysqlPort, configuration.mysqlUsername,
				configuration.mysqlPassword, configuration.mysqlDatabase);
		try {
			sql.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			// ex.printStackTrace();
			Bukkit.getLogger().warning("[LifeSteal] " + "Database not connected");
		}
		Bukkit.getLogger().info("[LifeSteal] " + "-".repeat(50) + " Database connected " + "-".repeat(50));
		dataTable = new DataTable(sql);
		dataTable.loadData();
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			(new Papi(this)).register();
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (updatePlayers.containsKey(player.getUniqueId())) {
					dataTable.updatePlayer(player);
				}
				else {
					// Bukkit.getLogger().info("[LifeSteal] Remove player with
					// uuid " + uuid);
					updatePlayers.remove(player.getUniqueId());
				}
				Utils.update(player);
			}
		}, 0, 5);
		for (Player player : Bukkit.getOnlinePlayers()) {
			dataTable.addPlayer(player);
			updatePlayers.put(player.getUniqueId(), player.getUniqueId());
		}
	}
	
	@Override
	public void onLoad() {
		configuration = new Configuration();
		configuration.loadConfig();
		super.onLoad();
	}
	
	@Override
	public void onDisable() {
		sql.disconnect();
	}
	
	public void firstJoin(Player player) {}
	
	public void savePlayer() {
		
	}
	
	public <T extends CommandExecutor> void registerCommand(String cmd, T handler) {
		Objects.requireNonNull(getCommand(cmd)).setExecutor(handler);
	}
	
	public static boolean takesPart(CommandSender sender) {
		if (sender instanceof Player) {
			return !sender.hasPermission("lifesteal.bypass");
		}
		return false;
	}
	
}
