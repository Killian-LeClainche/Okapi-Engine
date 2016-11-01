/**
 * 
 */
package com.polaris.engine;

/**
 * @author lec50
 *
 */
public class Key
{
	
	private static final long KEY_DOUBLE_PRESS = 1000000000 / 5;
	
	private final String keyName;
	private int keyCode;
	
	private boolean isPressed;
	private boolean wasQuickReleased;
	
	private long timer;
	private boolean isDoublePressed;
	
	public Key(String name, int code)
	{
		keyName = name;
		keyCode = code;
		isPressed = false;
		wasQuickReleased = false;
		timer = 0;
		isDoublePressed = false;
	}
	
	public final void setKeyCode(int code)
	{
		keyCode = code;
	}
	
	public final void press()
	{
		isPressed = true;
		wasQuickReleased = false;
		
		long time = System.nanoTime();
		
		if(time - timer < KEY_DOUBLE_PRESS)
			isDoublePressed = true;
		
		timer = time;
	}
	
	public final void release()
	{
		isPressed = false;
		isDoublePressed = false;
	}
	
	public final void update()
	{
		if(!isPressed && getPressedTime() >= KEY_DOUBLE_PRESS)
			wasQuickReleased = true;
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
	
	public final boolean wasQuickReleased()
	{
		return wasQuickReleased;
	}
	
	public final long getPressedTime()
	{
		return System.nanoTime() - timer;
	}
	
	public final boolean isDoublePressed()
	{
		return isDoublePressed;
	}
}
