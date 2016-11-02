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
	private boolean releaseFlag;
	private boolean wasQuickPressed;
	
	private long pressTimer;
	private boolean isDoublePressed;
	
	public Key(String name, int code)
	{
		keyName = name;
		keyCode = code;
		isPressed = false;
		releaseFlag = false;
		wasQuickPressed = false;
		pressTimer = 0;
		isDoublePressed = false;
	}
	
	public final void setKeyCode(int code)
	{
		keyCode = code;
	}
	
	public final void press()
	{
		isPressed = true;
		wasQuickPressed = false;
		releaseFlag = false;
		
		long time = System.nanoTime();
		
		if(time - pressTimer < KEY_DOUBLE_PRESS)
			isDoublePressed = true;
		
		pressTimer = time;
	}
	
	public final void release()
	{
		isPressed = false;
		isDoublePressed = false;
		
		if(System.nanoTime() - pressTimer < KEY_DOUBLE_PRESS / (3 / 2))
			releaseFlag = true;
	}
	
	public final void update()
	{
		if(releaseFlag && getPressedTime() >= KEY_DOUBLE_PRESS)
			wasQuickPressed = true;
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
}
