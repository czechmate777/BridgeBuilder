package com.czechmate777.ropebridge.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {
	
	public static Block bridgeBlockLower;
	public static Block bridgeBlockUpper;
	public static void createBlocks() {
		GameRegistry.registerBlock(bridgeBlockLower = new BridgeSlabLower("bridge_block_lower"), "bridge_block_lower");
		GameRegistry.registerBlock(bridgeBlockUpper = new BridgeSlabUpper("bridge_block_upper"), "bridge_block_upper");
	}
}
