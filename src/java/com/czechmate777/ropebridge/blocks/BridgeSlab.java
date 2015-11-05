package com.czechmate777.ropebridge.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BridgeSlab extends BasicBlock {
	protected static float slabHeight = 4.0F/16.0F;
	public static final PropertyEnum DIR = PropertyEnum.create("dir", BridgeSlab.EnumType.class);
	public BridgeSlab(String unlocalizedName, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(unlocalizedName);
		this.setStepSound(soundTypeWood);
		this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DIR, EnumType.NS));
	}
	
	public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }
	
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }
	
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }
	
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        return ret;
    }
	
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
    {
        return null;
    }
	
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { DIR });
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DIR, meta == 0 ? EnumType.NS : EnumType.EW);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumType type = (EnumType) state.getValue(DIR);
        return type.getID();
    }
    
    public enum EnumType implements IStringSerializable {
        NS(0, "north-south"),
        EW(1, "east-west");

        private int ID;
        private String name;
        
        private EnumType(int ID, String name) {
            this.ID = ID;
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        public int getID() {
            return ID;
        }
        
        @Override
        public String toString() {
            return getName();
        }
    }

}
