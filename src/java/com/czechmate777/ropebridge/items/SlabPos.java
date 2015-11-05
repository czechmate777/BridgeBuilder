package com.czechmate777.ropebridge.items;

import net.minecraft.util.BlockPos;

public class SlabPos {
	public int x;
	public int y;
	public int z;
	public boolean upper;
	public boolean rotate;
	
	public SlabPos(int xCoordinate, int yCoordinate, int zCoordinate, boolean isUpperSlab, boolean isRotated) {
		x = xCoordinate;
		y = yCoordinate;
		z = zCoordinate;
		upper = isUpperSlab;
		rotate = isRotated;
	}
	public SlabPos(BlockPos position, boolean isUpperSlab, boolean isRotated) {
		x = position.getX();
		y = position.getY();
		z = position.getZ();
		upper = isUpperSlab;
		rotate = isRotated;
	}
}
