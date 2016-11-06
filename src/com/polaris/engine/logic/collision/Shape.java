package com.polaris.engine.logic.collision;

import org.joml.Vector3d;

public abstract class Shape
{
	protected Vector3d position;
	protected double circleRadius;
	
	public Vector3d getPosition()
	{
		return position;
	}
	
	public abstract double getRotationX();
	public abstract double getRotationY();
	public abstract double getRotationZ();
	
	public abstract Axis[] getAxes();
	public abstract Projection project(Axis axis);
	
	public boolean isSimpleColliding(Shape collision)
	{
		double length = collision.circleRadius + circleRadius;
		return getPosition().distanceSquared(collision.getPosition()) < length*length;
	}

}
