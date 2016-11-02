/**
 * 
 */
package com.polaris.engine;


/**
 * @author Killian Le Clainche
 *
 */
public class LogicApp extends Thread
{
	private final ThreadCommunicator renderCommunicator;
	
	public LogicApp(ThreadCommunicator communicator, int maxUps)
	{
		renderCommunicator = communicator;
	}

	/**
	 * @return
	 */
	public double getTickRate()
	{
		return 0;
	}
	
}
