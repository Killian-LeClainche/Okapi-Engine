package com.polaris.engine.logic.collision;

import static com.polaris.engine.util.GeometryHelper.distanceFromToX;
import static com.polaris.engine.util.GeometryHelper.distanceFromToY;

import org.joml.Vector2d;
import org.joml.Vector3d;

import com.polaris.engine.util.GeometryHelper;
import com.polaris.engine.util.Helper;
import com.polaris.engine.util.MathHelper;
public class Rectangle extends Shape2D
{
	
	private double xMidLength;
	private double yMidLength;
	
	public Rectangle(Vector3d center, double xmid, double ymid)
	{
		this(center, xmid, ymid, 0);
	}
	
	public Rectangle(Vector3d center, double xmid, double ymid, double rot)
	{
		position = center;
		xMidLength = xmid;
		yMidLength = ymid;
		circleRadius = MathHelper.pythagoreon(xmid, ymid);
		rotation = rot;
	}
	
	public Rectangle(Vector2d topLeft, Vector2d bottomRight, double zPosition)
	{
		this(topLeft, bottomRight, zPosition, 0);
	}

	public Rectangle(Vector2d topLeft, Vector2d bottomRight, double zPosition, double rot)
	{
		position = new Vector3d(GeometryHelper.center(topLeft, bottomRight), zPosition);
		xMidLength = distanceFromToX(position, topLeft);
		yMidLength = distanceFromToY(position, topLeft);
		circleRadius = MathHelper.pythagoreon(xMidLength, yMidLength);
		rotation = rot;
	}
	
	@Override
	public Axis[] getAxes() 
	{
		return new Axis[] {new Axis(rotation), new Axis(rotation + Helper.HALFPI)};
	}

	@Override
	public Projection project(Axis axis) 
	{
        double cos = Math.abs(Math.cos(axis.getRotationX() - rotation));
        double sin = Math.abs(Math.sin(axis.getRotationX() - rotation));
        Vector2d min = new Vector2d(position.x, position.y).sub(xMidLength, yMidLength);
        Vector2d max = new Vector2d(position.x, position.y).add(xMidLength, yMidLength);
        double minPoint = GeometryHelper.distanceToSquared(new Vector2d(min.x * cos, min.y * sin));
        double maxPoint = GeometryHelper.distanceToSquared(new Vector2d(max.x * cos, max.y * sin));
		return new Projection(new Vector2d(min.x * cos, min.y * sin).length(), new Vector2d(max.x * cos, max.y * sin).length());
	}

}
