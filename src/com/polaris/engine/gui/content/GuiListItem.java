package com.polaris.engine.gui.content;

public abstract class GuiListItem<T>
{

	protected boolean highlighted = false;
	protected double posX = 0;
	protected double posY = 0;
	protected double width = 0;
	protected double height = 0;
	private T value;
	
	public GuiListItem(T val)
	{
		value = val;
	}
	
	public T getValue()
	{
		return value;
	}
	
	protected void setValue(T val)
	{
		value = val;
	}
	
	public void setDimensions(double x, double y, double w, double h)
	{
		posX = x;
		posY = y;
		width = w;
		height = h;
	}
	
	public abstract void render(double delta);
	
}
