/**
 * 
 */
package com.polaris.engine.thread;

import com.polaris.engine.App;
import com.polaris.engine.Gui;
import com.polaris.engine.LogicGui;

/**
 * @author lec50
 *
 */
public class ReinitGuiPacket extends AppPacket
{
	
	private final Gui renderGui;

	/**
	 * @param app
	 * @param logic
	 */
	public ReinitGuiPacket(App app, LogicApp logic, LogicGui logicHandler)
	{
		super(app, logic);
		renderGui = logicHandler.getGui();
	}
	
	@Override
	public void handle()
	{
		application.reinitGui(renderGui);
	}
}
