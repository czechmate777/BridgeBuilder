package com.czechmate777.ropebridge.items;

import java.awt.List;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BBItem extends Item {
	WorldServer server;
	World world;
	EntityPlayer player;
	Timer smokeTimer;
	Timer buildTimer;
	ChatStyle chatStyle = new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA);
	boolean posSet = false;
	BlockPos firstPos;
	
	public BBItem(String unlocalizedName) {
		super();
		this.setUnlocalizedName(unlocalizedName);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
		this.setMaxDamage(64);
		smokeTimer = new Timer();
		buildTimer = new Timer();
	}
	
	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
    	playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
		server = MinecraftServer.getServer().worldServers[0];
    	return itemStackIn;
    }
    
    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }
    
    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
    
    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }
    
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
		if (worldIn.isRemote) {
			if (world==null) {
				world = worldIn;
			}
			if (player==null) {
				player = playerIn;
			}
			MovingObjectPosition hit = player.rayTrace(400, 1.0F);
			server.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F);
			if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				server.playSoundEffect(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), "random.bowhit", 1.0F, 1.0F);
				BlockPos floored = new BlockPos(Math.floor(player.posX), Math.floor(player.posY)-1, Math.floor(player.posZ));
				newBridge(floored, hit.getBlockPos());
			}
		}
    }

	private void newBridge(BlockPos pos1, BlockPos pos2) {
		LinkedList<SlabPos> bridge = new LinkedList<SlabPos>();
		boolean allClear = true;

		int x1,y1,x2,y2,z;
		int Xdiff = Math.abs(pos1.getX()-pos2.getX());
		int Zdiff = Math.abs(pos1.getZ()-pos2.getZ());
		boolean rotate;
		if (Xdiff > Zdiff) {
			rotate = false;
			x1 = pos1.getX();
			y1 = pos1.getY();
			x2 = pos2.getX();
			y2 = pos2.getY();
			z = pos1.getZ();
		}
		else {
			rotate = true;
			x1 = pos1.getZ();
			y1 = pos1.getY();
			x2 = pos2.getZ();
			y2 = pos2.getY();
			z = pos1.getX();
		}
		
		double m;
		double b;
		double distance;
		double height;
		int distInt;
		
		m = (double)(y2-y1)/(double)(x2-x1);
		if (Math.abs(m)>0.2) {
			tellPlayer("Sorry, your slope is too great. Please try again with new coordinates.");
			return;
		}
		b = (double)y1-(m*(double)x1);
		distance = Math.abs(x2-x1);
		distInt = Math.abs(x2-x1);
		
		for (int x = Math.min(x1, x2)+1; x<= Math.max(x1, x2)-1; x++) {
			for (int y = Math.max(y1, y2); y>= Math.min(y1, y2)-distInt/8; y--) {
				double funcVal = m*(double)x+b-(distance/10)*(Math.sin((x-Math.min(x1, x2))*(Math.PI/distance)));
				if ((double)y+0.5>funcVal && (double)y-0.5<=funcVal) {
					if (funcVal>=y) {
						allClear = !addSlab(bridge,x,y+1,z,true,rotate) ? false : allClear;
					}
					else {
						allClear = !addSlab(bridge,x,y+1,z,false,rotate) ? false : allClear;
					}
				}
			}
		}
		
		if (allClear) {
			tellPlayer("Building Bridge!");
			buildBridge(bridge);
		}
		else {
			tellPlayer("Oops! Looks like there's something in the way. Look for the Smoke to see where that is and try again.");
		}
	}

	private void tellPlayer(String message) {
		player.addChatMessage(new ChatComponentText("[Rope Bridge]: "+message).setChatStyle(chatStyle));
	}

	private boolean addSlab(LinkedList<SlabPos> list, int x, int y, int z, boolean upper, boolean rotate) {
		boolean isClear;
		BlockPos pos;
		if (rotate) {
			pos = new BlockPos(z, y, x);
		}
		else {
			pos = new BlockPos(x, y, z);
		}
		isClear = world.isAirBlock(pos);
		list.add(new SlabPos(pos, upper, rotate));
		if (!isClear) {
			spawnSmoke(pos, 15);
		}
		return isClear;
	}
	
	private void spawnSmoke(BlockPos pos, int times) {
		if (times > 0) {
			server.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D, new int[0]);
			world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D, new int[0]);
			
			final BlockPos finPos = pos; final int finTimes = times-1;
		    smokeTimer.schedule(new TimerTask() { public void run() { spawnSmoke(finPos, finTimes); } }, 1000);
		}
	}

	private void buildBridge(LinkedList<SlabPos> bridge) {
		Block blk;
		BlockPos pos;
		IBlockState state;
		SlabPos slab;
		if(!bridge.isEmpty()) {
			slab = bridge.pop();
			if (slab.upper) {
				blk = ModBlocks.bridgeBlockUpper;
			}
			else {
				blk = ModBlocks.bridgeBlockLower;
			}
			pos = new BlockPos(slab.x, slab.y, slab.z);
			if(slab.rotate)
				state = blk.getStateFromMeta(1);
			else
				state = blk.getStateFromMeta(0);
			server.destroyBlock(pos, true);
			server.setBlockState(pos, state);
			
			spawnSmoke(pos, 1);
			server.playSoundEffect(slab.x, slab.y, slab.z, "dig.wood", 1.0F, server.rand.nextFloat() * 0.1F + 0.9F);
			
			final LinkedList<SlabPos> finBridge = bridge;
		    buildTimer.schedule(new TimerTask() { public void run() { buildBridge(finBridge); } }, 250);
		}
	}
}
