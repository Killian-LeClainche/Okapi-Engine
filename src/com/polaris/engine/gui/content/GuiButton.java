package com.polaris.engine.gui.content;

import com.polaris.engine.options.Settings;

public abstract class GuiButton<T extends Settings> extends GuiContent<T>
{
	
	protected String buttonText;
	
	public GuiButton(String name, double x, double y, double width, double height)
	{
		this(name, x, y, 0, width, height);
	}
	
	public GuiButton(String name, double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
		buttonText = name;
	}
	
}
