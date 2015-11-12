package com.czechmate777.ropebridge.crafting;

import com.czechmate777.ropebridge.items.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {

	public static void initCrafting() {
		GameRegistry.addRecipe(new ItemStack(ModItems.bridgeBuilder), new Object[] {"asi", "  t", 'a', Items.arrow, 's', Items.string, 'i', Items.iron_ingot, 't', Items.stick});
	}

}
