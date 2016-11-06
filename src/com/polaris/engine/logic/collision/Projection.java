package com.polaris.engine.logic.collision;

public class Projection 
{
	
	private double projectMax;
	private double projectLength;
	
	public Projection(double min, double max)
	{
		projectMax = max;
		projectLength = max - min;
	}
	
	public double getOverlap(Projection otherProjection)
	{
		System.out.print(otherProjection.projectLength - Math.abs(otherProjection.projectMax - projectMax) + " ");
		System.out.println(otherProjection.projectLength + " " + projectLength + " " + otherProjection.projectMax + " " + projectMax);
		return otherProjection.projectLength - Math.abs(otherProjection.projectMax - projectMax);
	}

}
