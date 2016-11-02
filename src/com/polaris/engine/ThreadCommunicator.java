/**
 * 
 */
package com.polaris.engine;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.codec.binary.StringUtils;

import com.polaris.engine.network.Packet;

/**
 * @author Killian Le Clainche
 *
 */
public class ThreadCommunicator
{
	
	private Map<String, Set<Packet>> threadPacketMap;
	
	public ThreadCommunicator()
	{
		threadPacketMap = new ConcurrentHashMap<String, Set<Packet>>();
	}

	/**
	 * @param currentThread
	 * @param string 
	 */
	public void addSide(String sideName)
	{
		PacketComparator packetComparator = new PacketComparator();
		ConcurrentSkipListSet<Packet> set = new ConcurrentSkipListSet<Packet>(packetComparator);
		threadPacketMap.put(sideName, set);
	}
	
	public void sendPacket(String sideName, Packet packetToSend)
	{
		threadPacketMap.get(sideName).add(packetToSend);
	}
	
	public void sendPacketToAll(String sourceSide, Packet packetToSend)
	{
		Iterator<String> keyIterator = threadPacketMap.keySet().iterator();
		String key;
		while(keyIterator.hasNext())
		{
			key = keyIterator.next();
			
			if(!StringUtils.equals(sourceSide, key))
			{
				threadPacketMap.get(key).add(packetToSend);
			}
		}
	}
	
	public Iterator<Packet> recievePackets(String sideName)
	{
		return threadPacketMap.get(sideName).iterator();
	}
	
}
