/**
 * 
 */
package com.polaris.engine.thread;

import com.polaris.engine.App;

/**
 * @author lec50
 *
 */
public class TerminateAppPacket extends AppPacket
{
	
	public TerminateAppPacket(App app, LogicApp logic)
	{
		super(app, logic);
	}
	
	@Override
	public void handle()
	{
		application.close();
	}
	
}
