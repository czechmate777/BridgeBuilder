package com.czechmate777.ropebridge.client.render.blocks;

import com.czechmate777.ropebridge.Main;
import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;



public final class BlockRenderRegister {
	public static String modid = Main.MODID;

	public static void registerBlockRenderer() {
	    reg(ModBlocks.bridgeBlockUpper);
	    reg(ModBlocks.bridgeBlockLower);
	}

	public static void reg(Block block) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
	    .register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(modid + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
}
