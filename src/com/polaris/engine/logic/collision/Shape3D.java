package com.polaris.engine.logic.collision;

import org.joml.Vector3d;

public abstract class Shape3D extends Shape
{

	protected Vector3d rotation;
	
	@Override
	public double getRotationX()
	{
		return rotation.x;
	}

	@Override
	public double getRotationY() 
	{
		return rotation.y;
	}

	@Override
	public double getRotationZ()
	{
		return rotation.z;
	}

}
