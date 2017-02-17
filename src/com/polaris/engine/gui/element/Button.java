package com.polaris.engine.gui.element;


public abstract class Button extends Element
{
	
	protected String buttonText;
	
	public Button(String name, double x, double y, double width, double height)
	{
		this(name, x, y, 0, width, height);
	}
	
	public Button(String name, double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
		buttonText = name;
	}
	
}
