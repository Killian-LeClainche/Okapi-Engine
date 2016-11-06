/**
 * 
 */
package com.polaris.engine.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Killian Le Clainche
 *
 */
public class IBOBuffer
{

	private IntBuffer iboBuffer;
	
	public IBOBuffer(int bufferSize)
	{
		iboBuffer = BufferUtils.createIntBuffer(bufferSize);
	}
	
	public void triangleVBO(VBOBuffer vboBuffer, int strideLength)
	{
		FloatBuffer buffer = vboBuffer.getBuffer();
		int offset = strideLength / 4;
		int i = 0;
		int j = vboBuffer.getBufferSize();
		
		float x, y, z;
		
		while(i < j)
		{
			buffer.get(i);
			buffer.get(i + 1);
			buffer.get(i + 2);
			i += offset;
		}
		
	}
	
	public void squareVBO(VBOBuffer vboBuffer)
	{
		FloatBuffer buffer = vboBuffer.getBuffer();
	}
	
	public void add(int ... vertices)
	{
		for(int vertex : vertices)
		{
			iboBuffer.put(vertex);
		}
	}
	
	/**
	 * @return
	 */
	public IntBuffer getBuffer()
	{
		return iboBuffer;
	}
	
}
