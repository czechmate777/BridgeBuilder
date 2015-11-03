package com.czechmate777.ropebridge;

import com.czechmate777.ropebridge.items.ModItems;
import com.czechmate777.ropebridge.blocks.ModBlocks;
import com.czechmate777.ropebridge.crafting.ModCrafting;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
    	ModItems.createItems();
    	ModBlocks.createBlocks();
    }

    public void init(FMLInitializationEvent e) {
    	ModCrafting.initCrafting();
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}