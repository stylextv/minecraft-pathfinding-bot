package de.stylextv.bits.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public final class WConnection
{
	public static void sendPacket(Packet packet)
	{
		Minecraft.getMinecraft().player.connection.sendPacket(packet);
	}
	
//	public static void sendPacketBypass(Packet packet)
//	{
//		Minecraft.getMinecraft().player.connection.sendPacketBypass(packet);
//	}
	
}