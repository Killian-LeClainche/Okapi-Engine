/**
 * 
 */
package com.polaris.engine.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author Killian Le Clainche
 *
 */
public class VBO
{
	
	public static VBO createStaticVBO(FloatBuffer vertexBuffer, FloatBuffer colorBuffer)
	{
		int[] vboId = new int[2];
		FloatBuffer[] vboData = {vertexBuffer, colorBuffer};
		
		glGenBuffers(vboId);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboId[0]);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		return new VBO(vboId, vboData, GL_STATIC_DRAW);
	}
	
	/*public static VBO createStaticVBO(int verticeSize, int colorSize)
	{
		int[] vboId = new int[2];
		FloatBuffer[] vboData = new FloatBuffer[2];
		
		GL15.glGenBuffers(vboId);
		
		vboData[0] = BufferUtils.createFloatBuffer(verticeSize);
		vboData[1] = BufferUtils.createFloatBuffer(colorSize);
		
		return new VBO(vboId, vboData, GL_STATIC_DRAW);
	}*/
	
	private final int[] vboId;
	private final int[] states;
	private final FloatBuffer[] vboData;
	private final int drawType;
	
	private VBO(int[] vbo, FloatBuffer[] data, int draw)
	{
		vboId = vbo;
		states = null;
		vboData = data;
		drawType = draw;
	}
	
	public void draw()
	{
		
	}
	
	public int getVertexBufferId()
	{
		return vboId[0];
	}
	
	public int getColorBufferId()
	{
		return vboId[1];
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return vboData[0];
	}
	
	public FloatBuffer getColorBuffer()
	{
		return vboData[1];
	}
	
	public int getDrawType()
	{
		return drawType;
	}
	
}
