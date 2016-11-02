/**
 * 
 */
package com.polaris.engine.thread;

import java.util.Comparator;

import com.polaris.engine.network.Packet;

/**
 * @author Killian Le Clainche
 *
 */
public class PacketComparator implements Comparator<ThreadPacket>
{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ThreadPacket p1, ThreadPacket p2)
	{
		return (int) (p2.getCreationTime() - p1.getCreationTime());
	}
	
}
