/**
 * 
 */
package com.polaris.engine;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.thread.LogicApp;

/**
 * @author Killian Le Clainche
 *
 */
public abstract class GameState
{
	
	protected final LogicApp logic;
	
	protected int ticksExisted;
	private GameState parent;
	
	public GameState(LogicApp app)
	{
		this(app, null);
	}
	
	public GameState(LogicApp app, GameState p)
	{
		logic = app;
		
		ticksExisted = 0;
		parent = p;
	}
	
	public abstract Gui getGui();
	
	public void init() {}
	
	public void update()
	{
		ticksExisted++;
	}
	
	public void reinit() {}
	public void reload() {}
	
	public void close() {}
	
	public GameState getParent()
	{
		return parent;
	}
	
}
