package com.czechmate777.ropebridge.items;

import java.util.List;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.czechmate777.ropebridge.Main;
import com.czechmate777.ropebridge.bridgeMessage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BBItem extends Item {
	World world;
	EntityPlayer player;
	boolean viewSnap;
	float playerFov;
	boolean fovNormal;
	Timer smokeTimer;
	Timer buildTimer;
	Timer clickTimer;
	ChatStyle chatStyle = new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA);
	boolean posSet = false;
	BlockPos firstPos;
	private boolean warningSent = false;
	
	public BBItem(String unlocalizedName) {
		super();
		this.setUnlocalizedName(unlocalizedName);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
		this.setMaxDamage(64);
		smokeTimer = new Timer();
		buildTimer = new Timer();
		clickTimer = new Timer();
		viewSnap = false;
		fovNormal = true;
	}
	
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		playerIn.triggerAchievement(Main.craftAchievement);
	}
	
	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
    	if (worldIn.isRemote) {
			world = worldIn;
		}
		if (playerIn.worldObj.isRemote) {
			player = playerIn;
			if (playerFov == 0) {
				playerFov = Minecraft.getMinecraft().gameSettings.fovSetting;
			}
		}
    	playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
    	
    	if (worldIn.isRemote) {
    		viewSnap = true;
    		fovNormal = false;
    		clickTimer = new Timer();
    		clickTimer.schedule(new TimerTask() { public void run() { Main.snw.sendToServer(new bridgeMessage(0,0,0,0,2,0)); } }, 500);
    	}
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
    
    public float getDigSpeed(ItemStack itemstack, net.minecraft.block.state.IBlockState state) {
    	if (player==null) {
    		return 1.0F;
    	}
    	else {
    		if (player.isSneaking() && isBridgeBlock(state.getBlock())) {
    			if (!warningSent ) {
    				tellPlayer("WARNING! Breaking whole bridge!");
    				warningSent = true;
    			}
    			return 0.3F;
    		}
    		else {
    			return 1.0F;
    		}
    	}
    }
    
    /**
     * allows items to add custom lines of information to the mouseover description
     *  
     * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    	tooltip.add("- Hold right-click to build");
    	tooltip.add("- Sneak to break whole bridge");
    }
    
    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }
    
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
		if (worldIn.isRemote) {
			viewSnap = false;
			if (72000-timeLeft > 10) {
				if (!player.onGround) {
					tellPlayer("You must be standing on something to build a bridge!");
				}
				else {
					MovingObjectPosition hit = player.rayTrace(Main.maxBridgeDistance, 1.0F);
					//world.playSoundEffect(player.posX,player.posY,player.posZ, "random.bow", 1.0F, 1.0F);
					//			play sound at 					player		random.bow
					Main.snw.sendToServer(new bridgeMessage(0, 	0, 0, 0, 	0, 0));
					if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
						BlockPos floored = new BlockPos(Math.floor(player.posX), Math.floor(player.posY)-1, Math.floor(player.posZ));
						// Vector offsets
						double xOffset = 0.0D;
						double yOffset = 0.0D;
						double zOffset = 0.0D;
						if (hit.hitVec.xCoord%1==0 && hit.hitVec.xCoord<floored.getX()) {
							xOffset = -0.8D;
						}
						if (hit.hitVec.zCoord%1==0 && hit.hitVec.zCoord<floored.getZ()) {
							zOffset = -0.8D;
						}
						if (hit.hitVec.yCoord%1==0) {
							if (player.rotationPitch>0) {	// Looking from top
								yOffset = -0.8D;
							}
						}
						
						newBridge(stack,floored, new BlockPos(hit.hitVec.xCoord+xOffset, hit.hitVec.yCoord+yOffset, hit.hitVec.zCoord+zOffset));
					}
				}
			}
			else {
				// Cancel click noise
				clickTimer.cancel();
			}
		}
    }

	private void newBridge(ItemStack stack, BlockPos pos1, BlockPos pos2) {
		LinkedList<SlabPos> bridge = new LinkedList<SlabPos>();
		boolean allClear = true;

		int x1,y1,x2,y2,z,z2;
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
			z2 = pos2.getZ();
		}
		else {
			rotate = true;
			x1 = pos1.getZ();
			y1 = pos1.getY();
			x2 = pos2.getZ();
			y2 = pos2.getY();
			z = pos1.getX();
			z2 = pos2.getX();
		}
		if (Math.abs(z2-z)>3) {
			tellPlayer("Sorry, bridge must be built in a cardinal dirrection. Please try again.");
			return;
		}
		double m;
		double b;
		double distance;
		int distInt;
		
		m = (double)(y2-y1)/(double)(x2-x1);
		if (Math.abs(m)>0.2) {
			tellPlayer("Sorry, your slope is too great. Please try again.");
			return;
		}
		b = (double)y1-(m*(double)x1);
		distance = Math.abs(x2-x1);
		distInt = Math.abs(x2-x1);
		
		// Check if bridge longer than 0
		if (distInt < 2) {
			// bridge too short
			return;
		}
		
		// Check for materials in inventory
		if (!hasMaterials(distInt-1) && !player.capabilities.isCreativeMode) {
			return;
		}
		
		for (int x = Math.min(x1, x2)+1; x<= Math.max(x1, x2)-1; x++) {
			for (int y = Math.max(y1, y2); y>= Math.min(y1, y2)-distInt/8-1; y--) {
				double funcVal = m*(double)x+b-(distance/1000)*(Math.sin((x-Math.min(x1, x2))*(Math.PI/distance)))*Main.bridgeDroopFactor + Main.bridgeYOffset;
				if ((double)y+0.5>funcVal && (double)y-0.5<=funcVal) {
					int level;
					if (funcVal>=y) {
						if (funcVal>=(double)y+0.25) {
							level = 4;
						}
						else {
							level = 3;
						}
					}
					else {
						if (funcVal>=(double)y-0.25) {
							level = 2;
						}
						else {
							level = 1;
						}
					}
					allClear = !addSlab(bridge,x,y+1,z,level,rotate) ? false : allClear;
				}
			}
		}
		
		
		if (allClear) {
			int type = getWoodType();
			if (!player.capabilities.isCreativeMode) {
				takeMaterials(distInt-1);
				if (stack.getItemDamage()==stack.getMaxDamage()) {
					zoomTo(playerFov);
				}
				Main.snw.sendToServer(new bridgeMessage(3, 0, 0, 0, 0, 0)); // damage item
			}
			Main.snw.sendToServer(new bridgeMessage(4, 0, 0, 0, 0, 0)); // trigger building achievement
			tellPlayer("Building Bridge!");
			buildBridge(bridge, type);
		}
		else {
			tellPlayer("Oops! Looks like there's something in the way. Look for the Smoke to see where that is and try again.");
		}
	}

	private int getWoodType() {
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack == null) {
				continue;
			}
			String name = stack.getItem().getUnlocalizedName();
			if (name.equals("tile.woodSlab")) {
				return stack.getItemDamage();
			}
		}
		return 0;
	}

	private void tell(EntityPlayer playerIn, String message) {
		playerIn.addChatMessage(new ChatComponentText("[Rope Bridge]: "+message).setChatStyle(chatStyle));
	}
	
	private void tellPlayer(String message) {
		tell(player, message);
	}

	private boolean addSlab(LinkedList<SlabPos> list, int x, int y, int z, int level, boolean rotate) {
		boolean isClear;
		BlockPos pos;
		if (rotate) {
			pos = new BlockPos(z, y, x);
		}
		else {
			pos = new BlockPos(x, y, z);
		}
		isClear = (world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos));
		list.add(new SlabPos(pos, level, rotate));
		if (!isClear) {
			spawnSmoke(pos, 15);
		}
		return isClear;
	}
	
	private void spawnSmoke(BlockPos pos, int times) {
		if (times > 0) {
			world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 0.0D, 0.0D, 0.0D, new int[0]);
			final BlockPos finPos = pos; final int finTimes = times-1;
		    smokeTimer.schedule(new TimerTask() { public void run() { spawnSmoke(finPos, finTimes); } }, 1000);
		}
	}

	private void buildBridge(LinkedList<SlabPos> bridge, int type) {
		SlabPos slab;
		if(!bridge.isEmpty()) {
			slab = bridge.pop();
			// Server call							build  x       y       z
			Main.snw.sendToServer(new bridgeMessage(1, slab.x, slab.y, slab.z, slab.level, (slab.rotate?1:0)+2*type));
			
			spawnSmoke(new BlockPos(slab.x, slab.y, slab.z), 1);
			//		play sound at 						x		y		z		wood
			Main.snw.sendToServer(new bridgeMessage(0, 	slab.x, slab.y, slab.z, 1, 0));
			
			final LinkedList<SlabPos> finBridge = bridge; final int finType = type;
		    buildTimer.schedule(new TimerTask() { public void run() { buildBridge(finBridge, finType); } }, 100);
		}
	}
	
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) {
			if (player == null) {
				player = (EntityPlayer) entityIn;
			}
			if (((EntityPlayer) entityIn).getCurrentEquippedItem()!=null) {
				if (((EntityPlayer) entityIn).getCurrentEquippedItem().getUnlocalizedName().equals(stack.getUnlocalizedName())) {
					if (viewSnap) {
						if (isSelected) {
							rotatePlayerTowards(getNearestYaw());
							zoomTowards(30);
						}
					}
					else {
						zoomTowards(playerFov);
					}
				}
				else {
					zoomTowards(playerFov);
					viewSnap = false;
					clickTimer.cancel();
				}
			}
			else {
				zoomTowards(playerFov);
				viewSnap = false;
				clickTimer.cancel();
			}
		}
	}
	
	/**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos Block's position in world
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer playerIn) {
        if (playerIn.worldObj.isRemote) {
        	Block blk = playerIn.worldObj.getBlockState(pos).getBlock();
	    	if (playerIn.isSneaking() && isBridgeBlock(blk)) {
	    		breakBridge(playerIn.worldObj, pos, blk.getMetaFromState(playerIn.worldObj.getBlockState(pos)));
	        }
        }
    	return false;
    }
	
	private boolean isBridgeBlock(Block blockIn) {
		if (blockIn.getUnlocalizedName().contains("bridge_block")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Breaks block at position posIn and recursively spreads to in-line neighbors
	 * @param posIn the position of the block to start breaking bridge from
	 */
	private void breakBridge(World worldIn, BlockPos posIn, int meta) {
		// Break block and turn into air
		Main.snw.sendToServer(new bridgeMessage(1, posIn.getX(), posIn.getY(), posIn.getZ(), 0, 0));
		int xRange = 0;
		int zRange = 0;
		if (meta%2 == 0)
			xRange = 1;
		else
			zRange = 1;
		for (int x = posIn.getX()-xRange; x <= posIn.getX()+xRange; x++) {
			for (int y = posIn.getY()-1; y <= posIn.getY()+1; y++) {
				for (int z = posIn.getZ()-zRange; z <= posIn.getZ()+zRange; z++) {
					if (x-posIn.getX()==0 && z-posIn.getZ()==0) {
						// No bridge 
					}
					else {
						BlockPos currentPos = new BlockPos(x, y, z);
						IBlockState currentBlockState = worldIn.getBlockState(currentPos);
						if (isBridgeBlock(currentBlockState.getBlock())) {
							if (currentBlockState.getBlock().getMetaFromState(currentBlockState)==meta) {
								final World worldInFinal = worldIn; final BlockPos currentPosFinal = currentPos; final int metaFinal = meta;
								buildTimer.schedule(new TimerTask() { public void run() { breakBridge(worldInFinal, currentPosFinal, metaFinal); } }, 100);
							}
						}
					}
				}
			}
		}
	}
	
	private boolean hasMaterials(int dist) {
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		int slabsNeeded = dist;
		int stringNeeded = 1+Math.round(dist/2);
		int slabsHad = 0;
		int stringHad = 0;
		
		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack == null) {
				continue;
			}
			String name = stack.getItem().getUnlocalizedName();
			if (name.equals("item.string")) {
				stringHad += stack.stackSize;
			}
			if (name.equals("tile.woodSlab")) {
				slabsHad += stack.stackSize;
			}
		}
		if (slabsHad>=slabsNeeded && stringHad>=stringNeeded) {
			return true;
		}
		else {
			tellPlayer("You need at least "+slabsNeeded+" slabs and "+stringNeeded+" strings to build this bridge.");
			return false;
		}
	}
	
	private void takeMaterials(int dist) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		int slabsNeeded = dist;
		int stringNeeded = 1+Math.round(dist/2);
		
		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack == null) {
				continue;
			}
			String name = stack.getItem().getUnlocalizedName();
			if (name.equals("item.string")) {
				if (stack.stackSize > stringNeeded) {
					//stack.stackSize = stack.stackSize - stringNeeded;
					// Update on server
					Main.snw.sendToServer(new bridgeMessage(2, 0, 0, 0, i, stack.stackSize - stringNeeded));
					stringNeeded = 0;
				}
				else {
					stringNeeded -= stack.stackSize;
					//player.inventory.mainInventory[i] = null;
					// Update on server
					Main.snw.sendToServer(new bridgeMessage(2, 0, 0, 0, i, 0));
					continue;
				}
			}
			if (name.equals("tile.woodSlab")) {
				if (stack.stackSize > slabsNeeded) {
					//stack.stackSize = stack.stackSize - slabsNeeded;
					// Update on server
					Main.snw.sendToServer(new bridgeMessage(2, 0, 0, 0, i, stack.stackSize - slabsNeeded));
					slabsNeeded = 0;
				}
				else {
					slabsNeeded -= stack.stackSize;
					//player.inventory.mainInventory[i] = null;
					// update on server
					Main.snw.sendToServer(new bridgeMessage(2, 0, 0, 0, i, 0));
					continue;
				}
			}
		}
	}
	
	private float getNearestYaw() {
		float yaw = player.rotationYaw%360;
		if (yaw < 0) yaw+= 360;
		if (yaw < 45) return 0F;
		if (yaw > 45 && yaw <= 135) return 90F;
		else if (yaw > 135 && yaw <= 225) return 180F;
		else if (yaw > 225 && yaw <= 315) return 270F;
		else return 360F;
	}
	
	private void rotatePlayerTowards(float target) {
		float yaw = player.rotationYaw%360;
		if (yaw < 0) yaw+= 360;
		rotatePlayerTo(yaw+(target-yaw)/4);
	}
	
 	private void rotatePlayerTo(float yaw) {
        float original = player.rotationYaw;
        player.rotationYaw = yaw;
        player.prevRotationYaw += player.rotationYaw - original;
	}

 	private void zoomTowards(float toFov) {
 		if (toFov != 0 && !fovNormal) {
 			float currentFov = Minecraft.getMinecraft().gameSettings.fovSetting;
 			if (Math.round(currentFov)!=toFov) {
 				zoomTo(currentFov+(toFov-currentFov)/4);
 			}
 			else {
 				if (Math.round(currentFov)==playerFov) {
 					fovNormal = true;
 				}
 			}
 		}
 	}
 	
 	private void zoomTo(float toFov) {
 		if (toFov != 0) {
 			Minecraft.getMinecraft().gameSettings.fovSetting = toFov;
 		}
 	}
}
