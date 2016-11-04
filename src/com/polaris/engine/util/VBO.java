/**
 * 
 */
package com.polaris.engine.util;

import static com.polaris.engine.util.VertexAttribute.COLOR;
import static com.polaris.engine.util.VertexAttribute.POSITION;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author Killian Le Clainche
 *
 */
public class VBO
{
	
	public static final VertexAttribute[] POS = {POSITION}; 
	public static final VertexAttribute[] POS_COLOR = {POSITION, COLOR};
	
	public static VBO createStaticVBO(FloatBuffer vboBuffer, VertexAttribute[] attributes, int[] offsets)
	{
		int[] vboId = new int[1];
		
		glGenBuffers(vboId);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboId[0]);
		glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		
		return new VBO(vboId[0], vboBuffer, attributes, offsets);
	}
	
	public static VBO createStaticVBO(FloatBuffer vertexBuffer, FloatBuffer colorBuffer)
	{
		int vertexSize = vertexBuffer.capacity();
		int colorSize = colorBuffer.capacity();
		FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(vertexSize + colorSize);
		
		vboBuffer.put(vertexBuffer);
		vboBuffer.put(colorBuffer);
		
		return createStaticVBO(vboBuffer, POS_COLOR, new int[] {0, vertexSize});
	}
	
	private final int vboId;
	private final FloatBuffer vboBuffer;
	private final VertexAttribute[] vboAttribs;
	private final int[] vboAttribOffsets;
	
	private VBO(int id, FloatBuffer buffer, VertexAttribute[] attributes, int[] attributeOffsets)
	{
		vboId = id;
		vboBuffer = buffer;
		vboAttribs = attributes;
		vboAttribOffsets = attributeOffsets;
	}
	
	public void bind()
	{
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
	}
	
	public void enable()
	{
		for(VertexAttribute attrib : vboAttribs)
		{
			GL20.glEnableVertexAttribArray(attrib.getAttribId());
		}
	}
	
	public void draw()
	{
		for(int i = 0; i < vboAttribs.length; i++)
		{
			VertexAttribute attrib = vboAttribs[i];
			
			GL20.glVertexAttribPointer(attrib.getAttribId(), attrib.getVertexSize(), GL11.GL_FLOAT, false, attrib.getVertexStride(), vboAttribOffsets[i]);
		}
	}
	
	public void drawEnable()
	{
		for(int i = 0; i < vboAttribs.length; i++)
		{
			VertexAttribute attrib = vboAttribs[i];
			
			GL20.glEnableVertexAttribArray(attrib.getAttribId());
			GL20.glVertexAttribPointer(attrib.getAttribId(), attrib.getVertexSize(), GL11.GL_FLOAT, false, attrib.getVertexStride(), vboAttribOffsets[i]);
		}
	}
	
	public void disable()
	{
		for(VertexAttribute attrib : vboAttribs)
		{
			GL20.glDisableVertexAttribArray(attrib.getAttribId());
		}
	}
	
	public void destroy()
	{
		glDeleteBuffers(vboId);
	}
	
}
