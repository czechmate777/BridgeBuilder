package com.czechmate777.ropebridge.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item bridgeBuilder;
	public static Item bridgeBuilderHook;
	public static Item bridgeBuilderBarrel;
	public static Item bridgeBuilderHandle;
	
	public static void createItems() {
		GameRegistry.registerItem(bridgeBuilder = new BBItem("bridge_builder"), "bridge_builder");
		GameRegistry.registerItem(bridgeBuilderHook = new BasicItem("bridge_builder_hook"), "bridge_builder_hook");
		GameRegistry.registerItem(bridgeBuilderBarrel = new BasicItem("bridge_builder_barrel"), "bridge_builder_barrel");
		GameRegistry.registerItem(bridgeBuilderHandle = new BasicItem("bridge_builder_handle"), "bridge_builder_handle");
	}
	
	
}
