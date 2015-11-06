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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

// TODO:
// make it woode
// Bridge builder damaged
// break whole bridge with the tool

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
		smokeTimer = new Timer();
		buildTimer = new Timer();
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (world==null) {
			world = worldIn;
		}
		if (player==null) {
			player = playerIn;
		}
		if (posSet) {
			if (pos.getX()==firstPos.getX()&&pos.getY()==firstPos.getY()&&pos.getZ()==firstPos.getZ()) {
				tellPlayer("Selection canceled. Select a bridge start position.");
				posSet = false;
				firstPos = null;
			}
			else {
				newBridge(firstPos, pos);
				posSet = false;
			}
		}
		else {
			firstPos = pos;
			posSet = true;
			tellPlayer("Start of bridge set.");
		}
		return true;
    }

	private void newBridge(BlockPos pos1, BlockPos pos2) {
		server = MinecraftServer.getServer().worldServers[0];
		
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
		
		for (int x = Math.min(x1, x2); x<= Math.max(x1, x2); x++) {
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
		player.addChatMessage(new ChatComponentText("[Bridge Builder]: "+message).setChatStyle(chatStyle));
	}

	private boolean addSlab(LinkedList<SlabPos> list, int x, int y, int z, boolean upper, boolean rotate) {
		boolean isClear;
		BlockPos pos;
		if (rotate) {
			pos = new BlockPos(z, y, x );
		}
		else {
			pos = new BlockPos(x, y, z );
		}
		isClear = world.isAirBlock(pos);
		list.add(new SlabPos(pos, upper, rotate));
		if (!isClear) {
			spawnSmoke(pos, 20);
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
			
			final LinkedList<SlabPos> finBridge = bridge;
		    buildTimer.schedule(new TimerTask() { public void run() { buildBridge(finBridge); } }, 250);
		}
	}
}
