package com.czechmate777.ropebridge.crafting;

import com.czechmate777.ropebridge.items.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {

	public static void initCrafting() {
		GameRegistry.addRecipe(new ItemStack(ModItems.bridgeBuilderHook), new Object[] {"i  ", "iii", "i  ", 'i', Items.iron_ingot});
		GameRegistry.addRecipe(new ItemStack(ModItems.bridgeBuilderBarrel), new Object[] {"iii", "sss", "iii", 'i', Items.iron_ingot, 's', Items.string});
		GameRegistry.addRecipe(new ItemStack(ModItems.bridgeBuilderHandle), new Object[] {"i f", "sg ", "iww", 'i', Items.iron_ingot, 'f', Items.flint_and_steel, 's', Items.string, 'g', Items.gunpowder, 'w', Blocks.planks});
		GameRegistry.addRecipe(new ItemStack(ModItems.bridgeBuilder), new Object[] {"tbh", 't', ModItems.bridgeBuilderHook, 'b', ModItems.bridgeBuilderBarrel, 'h', ModItems.bridgeBuilderHandle});
		GameRegistry.addRecipe(new ItemStack(Items.string, 4), new Object[] {"w", 'w', Blocks.wool});
	}

}
