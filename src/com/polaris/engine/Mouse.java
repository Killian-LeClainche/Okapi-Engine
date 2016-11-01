package com.polaris.engine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.joml.Vector2d;

public class Mouse 
{
	
	private final App application;
	private Vector2d position;
	private Vector2d delta;
	
	private final BidiMap<Integer, Key> keyMapping;
	private final BidiMap<String, Key> nameMapping;
	
	public Mouse(App app)
	{
		application = app;
		keyMapping = new DualHashBidiMap<Integer, Key>();
		nameMapping = new DualHashBidiMap<String, Key>();
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
