package com.polaris.engine.logic.collision;

public class Axis 
{
	
	private double axisRotationX;
	private double axisRotationY;
	private double axisRotationZ;
	
	public Axis(double rotationX)
	{
		axisRotationX = rotationX;
		axisRotationY = axisRotationZ = 0;
	}
	
	public Axis(double rotationX, double rotationY, double rotationZ)
	{
		axisRotationX = rotationX;
		axisRotationY = rotationY;
		axisRotationZ = rotationZ;
	}
	
	public double getRotationX()
	{
		return axisRotationX;
	}
	
	public double getRotationY()
	{
		return axisRotationY;
	}
	
	public double getRotationZ()
	{
		return axisRotationZ;
	}

}
