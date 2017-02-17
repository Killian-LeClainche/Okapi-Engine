package com.polaris.engine.gui.element;

public abstract class Slider<T> extends Element
{
	
	protected T minValue;
	protected T maxValue;
	protected T currentValue;
	
	public Slider(double x, double y, double width, double height, T min, T max, T current)
	{
		super(x, y, width, height);
		minValue = min;
		maxValue = max;
		currentValue = current;
	}
	
	@Override
	public void render(double delta)
	{
		
	}

	@Override
	public int mouseClick(int mouseId) 
	{
		return 2;
	}

}
