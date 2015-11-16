package com.czechmate777.ropebridge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION)
public class Main {

    public static final String MODID = "ropebridge";
    public static final String MODNAME = "Rope Bridge Mod";
    public static final String VERSION = "1.0.0";
    public static SimpleNetworkWrapper snw;

    @SidedProxy(clientSide="com.czechmate777.ropebridge.ClientProxy", serverSide="com.czechmate777.ropebridge.ServerProxy")
    public static CommonProxy proxy;
    
    @Instance
    public static Main instance = new Main();

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	// read your config file, create Blocks, Items, register with GameRegistry
    	this.proxy.preInit(e);
    	// Register Simple Channel
    	snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    	snw.registerMessage(bridgeMessageHandler.class, bridgeMessage.class, 0, Side.SERVER);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
    	// build up data structures, add Crafting Recipes and register new handler
    	this.proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    	// communicate with other mods, adjust based on this
    	this.proxy.postInit(e);
    }
}