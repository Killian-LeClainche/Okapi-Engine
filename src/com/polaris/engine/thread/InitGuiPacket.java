/**
 * 
 */
package com.polaris.engine.thread;

import com.polaris.engine.App;
import com.polaris.engine.GameState;
import com.polaris.engine.gui.Gui;

/**
 * @author lec50
 *
 */
public class InitGuiPacket extends AppPacket
{
	
	private final Gui renderGui;

	/**
	 * @param app
	 * @param logic
	 */
	public InitGuiPacket(App app, LogicApp logic, GameState logicHandler)
	{
		super(app, logic);
		renderGui = logicHandler.getGui();
	}
	
	@Override
	public void handle()
	{
		application.initGui(renderGui);
	}
	
}
