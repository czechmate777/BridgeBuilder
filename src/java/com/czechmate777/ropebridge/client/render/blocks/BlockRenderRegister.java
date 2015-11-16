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
	    reg(ModBlocks.bridgeBlock1, 0, "bridge_slab_1_ns");
	    reg(ModBlocks.bridgeBlock1, 1, "bridge_slab_1_ew");
	    reg(ModBlocks.bridgeBlock2, 0, "bridge_slab_2_ns");
	    reg(ModBlocks.bridgeBlock2, 1, "bridge_slab_2_ew");
	    reg(ModBlocks.bridgeBlock3, 0, "bridge_slab_3_ns");
	    reg(ModBlocks.bridgeBlock3, 1, "bridge_slab_3_ew");
	    reg(ModBlocks.bridgeBlock4, 0, "bridge_slab_4_ns");
	    reg(ModBlocks.bridgeBlock4, 1, "bridge_slab_4_ew");
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
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlock1), "ropebridge:bridge_slab_1_ns", "ropebridge:bridge_slab_1_ew");
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlock2), "ropebridge:bridge_slab_2_ns", "ropebridge:bridge_slab_2_ew");
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlock3), "ropebridge:bridge_slab_3_ns", "ropebridge:bridge_slab_3_ew");
	    ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.bridgeBlock4), "ropebridge:bridge_slab_4_ns", "ropebridge:bridge_slab_4_ew");
	}
}
