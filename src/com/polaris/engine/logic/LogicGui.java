/**
 * 
 */
package com.polaris.engine.logic;

import com.polaris.engine.App;
import com.polaris.engine.LogicApp;

/**
 * @author Killian Le Clainche
 *
 */
public class LogicGui
{
	
	protected final LogicApp logic;
	
	protected int ticksExisted;
	private LogicGui parent;
	
	public LogicGui(LogicApp app)
	{
		this(app, null);
	}
	
	public LogicGui(LogicApp app, LogicGui p)
	{
		logic = app;
		
		ticksExisted = 0;
		parent = p;
	}
	
	public void init() {}
	
	public void update()
	{
		ticksExisted++;
	}
	
	public void reinit() {}
	public void reload() {}
	
	public void close() {}
	
	public LogicGui getParent()
	{
		return parent;
	}
	
}
