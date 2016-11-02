/**
 * 
 */
package com.polaris.engine.thread;


/**
 * @author lec50
 *
 */
public class ThreadPacket
{
	
	private final long creationTime;
	
	public ThreadPacket()
	{
		creationTime = System.nanoTime();
	}

	/**
	 * @return
	 */
	public long getCreationTime()
	{
		return creationTime;
	}
	
}
