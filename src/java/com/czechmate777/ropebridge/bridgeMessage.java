package com.czechmate777.ropebridge;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class bridgeMessage implements IMessage{
	int command; 	// 0 = smoke, 1 = setState, 2 = inventory change
	int posX, posY, posZ; 	// Where smoke or setState
	int invIndex;	// inventory index to change/upper or lower
	int stackSize;	// value of stack/rotate or not
	
	public bridgeMessage() {
		command = -1;
	}
	
	public bridgeMessage(int command, int posX, int posY, int posZ, int invIndex, int stackSize) {
		this.command = command;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.invIndex = invIndex;
		this.stackSize = stackSize;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		command = ByteBufUtils.readVarInt(buf, 5);
		posX = ByteBufUtils.readVarInt(buf, 5);
		posY = ByteBufUtils.readVarInt(buf, 5);
		posZ = ByteBufUtils.readVarInt(buf, 5);
		invIndex = ByteBufUtils.readVarInt(buf, 5);
		stackSize = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, command, 5);
		ByteBufUtils.writeVarInt(buf, posX, 5);
		ByteBufUtils.writeVarInt(buf, posY, 5);
		ByteBufUtils.writeVarInt(buf, posZ, 5);
		ByteBufUtils.writeVarInt(buf, invIndex, 5);
		ByteBufUtils.writeVarInt(buf, stackSize, 5);
	}
}
