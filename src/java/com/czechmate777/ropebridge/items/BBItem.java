package com.czechmate777.ropebridge.items;

import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BBItem extends Item {
	World world;
	EntityPlayer player;
	ChatStyle chatStyle = new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA);
	boolean posSet = false;
	BlockPos firstPos;
	
	public BBItem(String unlocalizedName) {
		super();
		this.setUnlocalizedName(unlocalizedName);
		this.setCreativeTab(CreativeTabs.tabTools);
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
				tellPlayer("Building Bridge!");
				buildBridge(firstPos, pos);
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

	private void buildBridge(BlockPos pos1, BlockPos pos2) {
		int Xdiff = Math.abs(pos1.getX()-pos2.getX());
		int Zdiff = Math.abs(pos1.getZ()-pos2.getZ());
		if (Xdiff > Zdiff) {
			bridgeHelper(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos1.getZ(), "X");
		}
		else {
			bridgeHelper(pos1.getX(), pos1.getY(), pos1.getZ(), pos1.getX(), pos2.getY(), pos2.getZ(), "Z");
		}
	}

	private void bridgeHelper(int x1, int y1, int z1, int x2, int y2, int z2, String onAxis) {
		double m;
		double b;
		double distance;
		double height;
		int distInt;
		
		if (onAxis.equals("X")){
			m = (double)(y2-y1)/(double)(x2-x1);
			if (Math.abs(m)>0.2) {
				tellPlayer("Sorry, your slope is too great. Please try again with new coordinates.");
				return;
			}
			b = (double)y1-(m*(double)x1);
			distance = Math.abs(x2-x1);
			distInt = Math.abs(x2-x1);
			height = Math.abs(y2-y1)+distance/10;
			int z = z1;
			
			for (int y = Math.max(y1, y2); y>= Math.min(y1, y2)-distInt/10; y--) {
				for (int x = Math.min(x1, x2)+1; x<= Math.max(x1, x2)-1; x++) {
					double funcVal = m*(double)x+b-(distance/10)*(Math.sin((x-Math.min(x1, x2))*(Math.PI/distance)));
					if ((double)y+0.5>funcVal && (double)y-0.5<=funcVal) {
						if (funcVal>=y) {
							setBlock(x,y,z,true);
						}
						else {
							setBlock(x,y,z,false);
						}
					}
					else {
						
					}
				}
			}
		}
		else {
			m = (double)(y2-y1)/(double)(z2-z1);
			if (Math.abs(m)>0.2) {
				tellPlayer("Sorry, your slope is too great. Please try again with new coordinates.");
				return;
			}
			b = (double)y1-(m*(double)z1);
			distance = Math.abs(z2-z1);
			distInt = Math.abs(z2-z1);
			height = Math.abs(y2-y1)+distance/10;
			int x = x1;
			
			for (int y = Math.max(y1, y2); y>= Math.min(y1, y2)-distInt/10; y--) {
				for (int z = Math.min(z1, z2)+1; z<= Math.max(z1, z2)-1; z++) {
					double funcVal = m*(double)z+b-(distance/10)*(Math.sin((z-Math.min(z1, z2))*(Math.PI/distance)));
					if ((double)y+0.5>funcVal && (double)y-0.5<=funcVal) {
						if (funcVal>=y) {
							setBlock(x,y,z,true);
						}
						else {
							setBlock(x,y,z,false);
						}
					}
					else {
						
					}
				}
			}
		}	
	}

	private void tellPlayer(String message) {
		
		player.addChatMessage(new ChatComponentText("[Bridge Builder]: "+message).setChatStyle(chatStyle));
	}

	private void setBlock(int x, int y, int z, boolean upper) {
		Block blk;
		if (upper) {
			blk = ModBlocks.bridgeBlockUpper;
		}
		else {
			blk = ModBlocks.bridgeBlockLower;
		}
		BlockPos pos = new BlockPos(x, y, z);
		String oldName = world.getBlockState(pos).getBlock().getLocalizedName();
		world.setBlockToAir(pos);
		String midName = world.getBlockState(pos).getBlock().getLocalizedName();
		IBlockState state = blk.getDefaultState();
		world.setBlockState(pos, state);
		String newName = world.getBlockState(pos).getBlock().getLocalizedName();
		System.out.println("Old name: "+oldName+"\nMid Name: "+midName+"\nNew Name: "+newName);
	}
}
