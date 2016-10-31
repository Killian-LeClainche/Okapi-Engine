package com.polaris.engine;

import org.joml.Vector2d;

public class Mouse 
{
	
	private final App application;
	private Vector2d position;
	private Vector2d delta;
	
	public Mouse(App app)
	{
		application = app;
	}
	
	public Vector2d getPos()
	{
		return position;
	}
	
	public Vector2d getDelta()
	{
		return delta;
	}
	
	public void setPos(double x, double y)
	{
		position.set(x, y);
	}
	
	public void setDelta(double dx, double dy)
	{
		delta.set(dx, dy);
	}

}
