package com.polaris.engine.gui.content;

import com.polaris.engine.options.Settings;

public abstract class GuiModeButton<T extends Settings> extends GuiButton<T>
{

	protected String[] modeNames;
	protected int mode = 0;

	public GuiModeButton(double x, double y, double width, double height, String ... list)
	{
		super(list[0], x, y, width, height);
		modeNames = list;
	}

	@Override
	public int mouseClick(int mouseId)
	{
		buttonText = modeNames[(mode = (mode + 1) % modeNames.length)];
		return 2;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int m)
	{
		buttonText = modeNames[(mode = m % modeNames.length)];
	}
	
}
