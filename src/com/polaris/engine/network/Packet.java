package com.polaris.engine.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Packet
{
	
	private static LinkedList<Class<? extends Packet>> packetList = new LinkedList<Class<? extends Packet>>();
	
	static
	{
		addPacket(PacketRSA.class);
		addPacket(PacketAES.class);
		addPacket(PacketSecure.class);
	}
	
	private short packetHeader = 0;
	
	public static void addPacket(Class<? extends Packet> packet)
	{
		packetList.add(packet);
	}
	
	public static void sortPackets()
	{
		List<Class<? extends Packet>> sortedList = new ArrayList<Class<? extends Packet>>();
		for(Class<? extends Packet> packetCL : packetList)
		{
			boolean placed = false;
			for(int i = 0; i < sortedList.size(); i++)
			{
				if(sortedList.get(i).toString().compareTo(packetCL.toString()) < 0)
				{
					placed = true;
					sortedList.add(i, packetCL);
					i = sortedList.size();
				}
			}
			if(!placed)
			{
				sortedList.add(packetCL);
			}
		}
	}
	
	public static short getPacketHeader(Packet packet)
	{
		return (short) packetList.indexOf(packet.getClass());
	}
	
	public static Packet wrap(int packet, byte[] data) throws ReflectiveOperationException, IOException 
	{
		Class<? extends Packet> packetClass = packetList.get(packet);
		Constructor<? extends Packet> packetInstance = packetClass.getConstructor();
		DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));
		Packet p = packetInstance.newInstance();
		p.copy(dataStream);
		return p;
	}
	
	public Packet() 
	{
		packetHeader = getPacketHeader(this);
	}
	
	public abstract void writeData(DataOutputStream output) throws IOException;
	
	public abstract void copy(DataInputStream data) throws IOException;
	
	public abstract void handle(Network network);

	public final int getHeader()
	{
		return packetHeader;
	}

	/**
	 * @return
	 */
	public long getCreationTime()
	{
		return 0;
	}

}
