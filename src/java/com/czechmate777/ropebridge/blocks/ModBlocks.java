package com.czechmate777.ropebridge.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {
	
	public static Block bridgeBlock1;
	public static Block bridgeBlock2;
	public static Block bridgeBlock3;
	public static Block bridgeBlock4;
	public static void createBlocks() {
		GameRegistry.registerBlock(bridgeBlock1 = new BridgeSlab1("bridge_block_1"), "bridge_block_1");
		GameRegistry.registerBlock(bridgeBlock2 = new BridgeSlab2("bridge_block_2"), "bridge_block_2");
		GameRegistry.registerBlock(bridgeBlock3 = new BridgeSlab3("bridge_block_3"), "bridge_block_3");
		GameRegistry.registerBlock(bridgeBlock4 = new BridgeSlab4("bridge_block_4"), "bridge_block_4");
	}
}
