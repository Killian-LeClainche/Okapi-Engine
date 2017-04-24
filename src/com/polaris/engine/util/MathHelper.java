package com.polaris.engine.util;

import net.jafama.FastMath;
import net.jafama.StrictFastMath;
import org.joml.Vector4d;

public class MathHelper
{
	
	private static final double LOG_2 = 1 / Math.log(2);
	
	public static boolean inBounds(double mx, double my, Vector4d bounds)
	{
		return bounds.x <= mx && bounds.z >= mx && bounds.y <= my && bounds.w >= my;
	}
	
	/**
	 * Retrieves a linear number from one number to another
	 *
	 * @param value    : The current value
	 * @param toValue  : The value for which value wants to become
	 * @param modifier : How fast for the value to get there (in seconds)
	 *
	 * @return either (value - modifier) or (value + modifier) with a cap at toValue
	 */
	public static double getLinearValue(double value, double toValue, double modifier)
	{
		if (value > toValue) return Math.max(toValue, value - modifier);
		return Math.min(toValue, value + modifier);
	}
	
	/**
	 * Retrieves a linear number from one number to another
	 *
	 * @param value    : The current value
	 * @param toValue  : The value for which value wants to become
	 * @param modifier : How fast for the value to get there (in seconds)
	 * @param delta    : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 *
	 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
	 */
	public static double getLinearValue(double value, double toValue, double modifier, double delta)
	{
		if (value > toValue) return Math.max(toValue, value - modifier * delta);
		return Math.min(toValue, value + modifier * delta);
	}
	
	/**
	 * Retrieves a linear number from one number to another
	 *
	 * @param value    : The current value
	 * @param toValue  : The value for which value wants to become
	 * @param modifier : How fast for the value to get there (in seconds)
	 *
	 * @return either (value - modifier) or (value + modifier) with a cap at toValue
	 */
	public static double getLinearDistance(double value, double toValue, double modifier)
	{
		if (value > toValue) return Math.max(toValue, value - modifier) - value;
		return Math.min(toValue, value + modifier) - value;
	}
	
	/**
	 * Retrieves a linear number from one number to another
	 *
	 * @param value    : The current value
	 * @param toValue  : The value for which value wants to become
	 * @param modifier : How fast for the value to get there (in seconds)
	 * @param delta    : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 *
	 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
	 */
	public static double getLinearDistance(double value, double toValue, double modifier, double delta)
	{
		if (value > toValue) return Math.max(toValue, value - modifier * delta) - value;
		return Math.min(toValue, value + modifier * delta) - value;
	}
	
	public static double getExpValue(double value, double toValue, double modifier)
	{
		return value + (toValue - value) / modifier;
	}
	
	public static double getExpValue(double value, double toValue, double modifier, double delta)
	{
		return value + ((toValue - value) / modifier) * delta;
	}
	
	public static double getExpDistance(double value, double toValue, double modifier)
	{
		return (toValue - value) / modifier;
	}
	
	public static double getExpDistance(double value, double toValue, double modifier, double delta)
	{
		return ((toValue - value) / modifier) * delta;
	}
	
	public static boolean isEqual(double value, double value1)
	{
		return Math.abs(value - value1) < .0001;
	}
	
	public static boolean isEqual(double value, double value1, double tolerance)
	{
		return Math.abs(value - value1) < tolerance;
	}
	
	public static int random(int maxValue)
	{
		return (int) (Math.random() * (maxValue + 1));
	}
	
	public static float random(float maxValue)
	{
		return (float) (Math.random() * maxValue);
	}
	
	public static double random(double maxValue)
	{
		return Math.random() * maxValue;
	}
	
	public static int random(int minValue, int maxValue)
	{
		return (int) StrictFastMath.round((Math.random() * (maxValue - minValue + 1)) + minValue);
	}
	
	public static float random(float minValue, float maxValue)
	{
		return (float) (Math.random() * (maxValue - minValue)) + minValue;
	}
	
	public static double random(double minValue, double maxValue)
	{
		return Math.random() * (maxValue - minValue) + maxValue;
	}
	
	public static double clamp(double min, double max, double value)
	{
		if (min > value) return min;
		if (max < value) return max;
		return value;
	}
	
	public static boolean isNegative(double value)
	{
		return value < 0;
	}
	
	public static double log2(double value)
	{
		return FastMath.logQuick(value) * LOG_2;
	}
	
	public static double log(double base, double value)
	{
		return FastMath.logQuick(value) / FastMath.logQuick(base);
	}
	
	public static double pythagoreon(double a, double b)
	{
		return FastMath.sqrtQuick(a * a + b * b);
	}
	
	public static double pythagoreon(double a, double b, double c)
	{
		return FastMath.sqrtQuick(a * a + b * b + c * c);
	}
	
}
