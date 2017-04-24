package com.polaris.engine.sound;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

public class StaticSound
{
	
	/**
	 * Buffers hold sound data.
	 */
	protected IntBuffer buffer = BufferUtils.createIntBuffer(1);
	
	/**
	 * Sources are points emitting sound.
	 */
	protected IntBuffer source = BufferUtils.createIntBuffer(1);
	
	
	protected StaticSound(String location)
	{
		
	}
	
	
	public boolean isFinished()
	{
		return false;
	}
	
	public void close()
	{
	
	}
	
}
