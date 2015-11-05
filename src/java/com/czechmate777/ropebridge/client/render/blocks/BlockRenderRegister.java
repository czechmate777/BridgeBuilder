package com.czechmate777.ropebridge.client.render.blocks;

import com.czechmate777.ropebridge.Main;
import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;



public final class BlockRenderRegister {
	public static String modid = Main.MODID;

	public static void registerBlockRenderer() {
	    reg(ModBlocks.bridgeBlockLower, 0, "bridge_slab_lower_ns");
	    reg(ModBlocks.bridgeBlockLower, 1, "bridge_slab_lower_ew");
	    reg(ModBlocks.bridgeBlockUpper, 0, "bridge_slab_upper_ns");
	    reg(ModBlocks.bridgeBlockUpper, 1, "bridge_slab_upper_ew");
	}

	public static void reg(Block block) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
	    .register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(modid + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
	
	public static void reg(Block block, int meta, String file) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
	    .register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(modid + ":" + file, "inventory"));
	}
	
	public static void preInit() {
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlockLower), "ropebridge:bridge_slab_lower_ns", "ropebridge:bridge_slab_lower_ew");
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlockUpper), "ropebridge:bridge_slab_upper_ns", "ropebridge:bridge_slab_upper_ew");
	}
}
