/**
 * 
 */
package com.polaris.engine.thread;

import com.polaris.engine.App;

/**
 * @author lec50
 *
 */
public abstract class AppPacket extends ThreadPacket
{
	
	protected final App application;
	protected final LogicApp logicThread;
	
	public AppPacket(App app, LogicApp logic)
	{
		application = app;
		logicThread = logic;
	}
	
	public abstract void handle();
	
}
