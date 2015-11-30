package com.czechmate777.ropebridge;

import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class bridgeMessageHandler implements IMessageHandler<bridgeMessage, IMessage> {

	@Override
	public IMessage onMessage(bridgeMessage bridgeMessage, MessageContext context) {
		final bridgeMessage message = bridgeMessage;
		final MessageContext ctx = context;
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		// or Minecraft.getMinecraft() on the client
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        		WorldServer world = (WorldServer) player.worldObj;
                switch (message.command) {
                	case 0: { // Sound
                		String name = "";
                		switch  (message.invIndex) {
                		case 0: { name = "random.bow";	break; }
                		case 1: { name = "dig.wood";	break; }
                		case 2: { name = "ropebridge:cock";	break; }
                		}
                		if (message.posX==0) {	// Sound at player
                			world.playSoundAtEntity(player, name, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                		}
                		else {					// Sound at coordinates
                			world.playSoundEffect(message.posX, message.posY, message.posZ, name, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                		}
                		break;
                	}
                	case 1: { // set a block
                		BlockPos blockPos = new BlockPos(message.posX, message.posY, message.posZ);
                		world.destroyBlock(blockPos, true);
                		Block blk;
                		switch (message.invIndex) {
                		case 0: { blk = Blocks.air;				break; }
                		case 1: { blk = ModBlocks.bridgeBlock1; break; }
                		case 2: { blk = ModBlocks.bridgeBlock2; break; }
                		case 3: { blk = ModBlocks.bridgeBlock3; break; }
                		case 4: { blk = ModBlocks.bridgeBlock4; break; }
                		default: { blk = Blocks.air; break; }
                		}
                		world.setBlockState(blockPos, blk.getStateFromMeta(message.stackSize));
                		break;
                	}
                	case 2: { // set inventory
                		if (message.stackSize == 0) {
                			player.inventory.mainInventory[message.invIndex] = null;
                		}
                		else {
                    		player.inventory.mainInventory[message.invIndex].stackSize = message.stackSize;
                		}
                		break;
                	}
                	case 3: { // damage item
                		if (player.getCurrentEquippedItem().getItemDamage()==player.getCurrentEquippedItem().getMaxDamage()) {
                			player.destroyCurrentEquippedItem();
                		}
                		else {
                			player.getCurrentEquippedItem().damageItem(1, player);
                		}
                		break;
                	}
                	case 4: { // trigger the achievement for building a bridge
                		player.triggerAchievement(Main.buildAchievement);
                		break;
                	}
                }
            }
        });
        return null; // no response in this case
	}
	
}
