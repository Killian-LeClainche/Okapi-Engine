/**
 * 
 */
package com.polaris.engine.options;

/**
 * @author lec50
 *
 */
public class Key
{
	
	private static final long KEY_DOUBLE_PRESS = 1000000000 / 5;
	
	private final String keyName;
	private final int keyCode;
	
	private boolean isPressed;
	private boolean wasQuickPressed;
	
	private long pressTimer;
	private boolean isDoublePressed;
	
	public Key(String name, int code)
	{
		keyName = name;
		keyCode = code;
		
		isPressed = false;
		wasQuickPressed = false;
		
		pressTimer = 0;
		isDoublePressed = false;
	}
	
	public final void press()
	{
		isPressed = true;
		wasQuickPressed = false;
		
		long time = System.nanoTime();
		
		if(time - pressTimer < KEY_DOUBLE_PRESS)
			isDoublePressed = true;
		
		pressTimer = time;
	}
	
	public final void release()
	{
		isPressed = false;
		isDoublePressed = false;
		
		if(getPressedTime() <= KEY_DOUBLE_PRESS)
			wasQuickPressed = true;
	}
	
	public final void update()
	{
		if(wasQuickPressed)
			wasQuickPressed = false;
	}
	
	public final String getName()
	{
		return keyName;
	}
	
	public final int getKey()
	{
		return keyCode;
	}
	
	public final boolean isPressed()
	{
		return isPressed;
	}
	
	public final boolean wasQuickPressed()
	{
		return wasQuickPressed;
	}
	
	public final long getPressedTime()
	{
		return System.nanoTime() - pressTimer;
	}
	
	public final boolean isDoublePressed()
	{
		return isDoublePressed;
	}
	
	public String toString()
	{
		return "Key: " + keyName + ", Key Code: " + keyCode + ", isPressed: " + isPressed + ", wasQuickPressed: " + wasQuickPressed + ", \n\t pressTimer: " + pressTimer + ", isDoublePressed: " + isDoublePressed;
	}
}
