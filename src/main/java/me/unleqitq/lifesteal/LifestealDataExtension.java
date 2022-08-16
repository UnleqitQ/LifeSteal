package me.unleqitq.lifesteal;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.extension.*;
import com.djrapitops.plan.extension.annotation.NumberProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;

import java.util.UUID;

@PluginInfo (name = "Lifesteal", iconName = "heart", color = Color.RED)
public class LifestealDataExtension implements DataExtension {
	
	private static LifestealDataExtension extension;
	private static Caller caller;
	
	public static void register() {
		extension = new LifestealDataExtension();
		caller = ExtensionService.getInstance().register(extension).orElseThrow();
		listenForPlanReloads();
	}
	
	public static void unregister() {
		if (extension != null)
			ExtensionService.getInstance().unregister(extension);
		extension = null;
	}
	
	public static boolean isEnabled() {
		return extension != null;
	}
	
	public static void update(UUID player) {
		caller.updatePlayerData(player, null);
	}
	
	private static void listenForPlanReloads() {
		CapabilityService.getInstance().registerEnableListener(isPlanEnabled -> {
			if (isPlanEnabled)
				register();
		});
	}
	
	public CallEvents[] callExtensionMethodsOn() {
		return new CallEvents[]{
				CallEvents.SERVER_PERIODICAL, CallEvents.MANUAL
		};
	}
	
	@NumberProvider (
			text = "Hearts", description = "How many hearts the player has left", priority = 4, iconName = "heart",
			iconFamily = Family.SOLID, iconColor = Color.RED, format = FormatType.NONE, showInPlayerTable = true
	)
	public long hearts(UUID player) {
		return LifeSteal.getHearts(player);
	}
	
	public String getPluginName() {
		return LifeSteal.getInstance().getName();
	}
	
}
