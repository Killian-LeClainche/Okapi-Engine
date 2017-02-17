package com.polaris.engine.gui.content;

import com.polaris.engine.options.Settings;

public class GuiTextField<T extends Settings> extends GuiContent<T>
{

	protected String text = "";
	protected String nullText = null;
	
	public GuiTextField(double x, double y, double width, double height)
	{
		this(x, y, 0, width, height);
	}
	
	public GuiTextField(double x, double y, double z, double width, double height)
	{
		super(x, y, z, width, height);
	}
	
	public GuiTextField(double x, double y, double width, double height, String text)
	{
		this(x, y, 0, width, height);
	}
	
	public GuiTextField(double x, double y, double z, double width, double height, String text)
	{
		super(x, y, z, width, height);
		nullText = text;
	}

	@Override
	public void render(double delta) 
	{
		
	}

	@Override
	public int mouseClick(int mouseId) 
	{
		return 1;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String s)
	{
		text = s;
	}

}
