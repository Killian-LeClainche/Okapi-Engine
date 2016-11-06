package com.polaris.engine.logic;

public class Force 
{

	public double xComponent;
	public double yComponent;
	public String name;
	
	public Force(String n, double x, double y)
	{
		name = n;
		xComponent = x;
		yComponent = y;
	}

	public void setForce(Force force)
	{
		xComponent = force.xComponent;
		yComponent = force.yComponent;
	}
	
}
