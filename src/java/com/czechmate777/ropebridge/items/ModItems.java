package com.czechmate777.ropebridge.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item tutorialItem;
	public static Item bridgeBuilder;
	
	public static void createItems() {
		GameRegistry.registerItem(tutorialItem = new BasicItem("tutorial_item"), "tutorial_item");
		GameRegistry.registerItem(bridgeBuilder = new BBItem("bridge_builder"), "bridge_builder");
	}
	
	
}
