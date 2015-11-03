package com.czechmate777.ropebridge.blocks;

public class BridgeSlab extends BasicBlock {
	protected static float slabHeight = 4.0F/16.0F;
	public BridgeSlab(String unlocalizedName, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(unlocalizedName);
		this.setStepSound(soundTypeWood);
		this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

}
