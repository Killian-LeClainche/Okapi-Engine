package com.polaris.engine.logic.collision;

import com.polaris.engine.util.Helper;

public class PixelBounds extends Shape2D
{

	@Override
	public Axis[] getAxes() 
	{
		return new Axis[] {new Axis(rotation), new Axis(rotation + Helper.HALFPI)};
	}

	@Override
	public Projection project(Axis axis) 
	{
		return null;
	}

}
