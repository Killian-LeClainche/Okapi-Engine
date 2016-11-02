/**
 * 
 */
package com.polaris.engine;

import java.util.Comparator;

import com.polaris.engine.network.Packet;

/**
 * @author Killian Le Clainche
 *
 */
public class PacketComparator implements Comparator<Packet>
{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Packet p1, Packet p2)
	{
		return (int) (p2.getCreationTime() - p1.getCreationTime());
	}
	
}
