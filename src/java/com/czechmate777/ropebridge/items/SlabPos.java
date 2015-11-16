package com.czechmate777.ropebridge.items;

import net.minecraft.util.BlockPos;

public class SlabPos {
	public int x;
	public int y;
	public int z;
	public int level;
	public boolean rotate;
	
	public SlabPos(int xCoordinate, int yCoordinate, int zCoordinate, int slabLevel, boolean isRotated) {
		x = xCoordinate;
		y = yCoordinate;
		z = zCoordinate;
		level = slabLevel;
		rotate = isRotated;
	}
	public SlabPos(BlockPos position, int slabLevel, boolean isRotated) {
		x = position.getX();
		y = position.getY();
		z = position.getZ();
		level = slabLevel;
		rotate = isRotated;
	}
}
