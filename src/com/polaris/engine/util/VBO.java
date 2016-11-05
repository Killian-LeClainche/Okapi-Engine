/**
 * 
 */
package com.polaris.engine.util;

import static com.polaris.engine.util.VertexAttribute.COLOR;
import static com.polaris.engine.util.VertexAttribute.NORMAL;
import static com.polaris.engine.util.VertexAttribute.POSITION;
import static com.polaris.engine.util.VertexAttribute.TEXTURE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Killian Le Clainche
 *
 */
public class VBO
{
	
	private static final int[] vboIdWrapper = new int[1];
	
	public static final VertexAttribute[] POS = {POSITION}; 
	public static final int POS_STRIDE = POSITION.getStride();
	
	public static final VertexAttribute[] POS_COLOR = {POSITION, COLOR};
	public static final int POS_COLOR_STRIDE = POS_STRIDE + COLOR.getStride();
	
	public static final VertexAttribute[] POS_NORMAL = {POSITION, NORMAL};
	public static final int POS_NORMAL_STRIDE = POS_STRIDE + NORMAL.getStride();
	
	public static final VertexAttribute[] POS_COLOR_TEXTURE = {POSITION, COLOR, TEXTURE};
	public static final int POS_COLOR_TEXTURE_STRIDE = POS_COLOR_STRIDE + TEXTURE.getStride();
	
	public static final VertexAttribute[] POS_COLOR_NORMAL = {POSITION, COLOR, NORMAL};
	public static final int POS_COLOR_NORMAL_STRIDE = POS_COLOR_STRIDE + NORMAL.getStride();
	
	public static final VertexAttribute[] POS_NORMAL_TEXTURE = {POSITION, NORMAL, TEXTURE};
	public static final int POS_NORMAL_TEXTURE_STRIDE = POS_NORMAL_STRIDE + TEXTURE.getStride();
	
	public static final VertexAttribute[] POS_COLOR_NORMAL_TEXTURE = {POSITION, COLOR, NORMAL, TEXTURE};
	public static final int POS_COLOR_NORMAL_TEXTURE_STRIDE = POS_COLOR_NORMAL_STRIDE + TEXTURE.getStride();
	
	private static FloatBuffer mixBuffers(FloatBuffer[] buffers, int[] strides)
	{
		int bufferSize = 0;
		int i = 0, j = 0, k = 0;
		
		while(i < buffers.length)
		{
			bufferSize += buffers[i].capacity();
			i++;
		}
		
		FloatBuffer finalBuffer = BufferUtils.createFloatBuffer(bufferSize);
		
		i = 0;
		
		while(i < bufferSize)
		{
			while(j < buffers.length)
			{
				while(k < strides[j])
				{
					finalBuffer.put(buffers[j].get());
					k++;
				}
				k = 0;
				j++;
			}
			j = 0;
			i++;
		}
		
		i = 0;
		while(i < buffers.length)
		{
			buffers[i].reset();
			i++;
		}
		
		return finalBuffer;
	}
	
	public static VBO createStaticVBO(FloatBuffer vboBuffer, VertexAttribute[] attributes, int strideLength, int[] offsets, int drawStyle, int verticeSize)
	{		
		glGenBuffers(vboIdWrapper);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboIdWrapper[0]);
		glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		
		return new VBO(vboIdWrapper[0], vboBuffer, attributes, strideLength, offsets, drawStyle, verticeSize);
	}
	
	public static VBO createStaticVBO(FloatBuffer vboBuffer, VertexAttribute[] attributes, int strideLength, int[] offsets, int drawStyle)
	{
		return createStaticVBO(vboBuffer, attributes, strideLength, offsets, drawStyle, (vboBuffer.capacity() * 4) / strideLength);
	}
	
	public static VBO createStaticVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, FloatBuffer ... buffers)
	{
		int[] strides = new int[attributes.length];
		int i = 0;
		
		while(i < attributes.length)
		{
			strides[i] = attributes[i].getStride();
			i++;
		}
		
		FloatBuffer vboBuffer = mixBuffers(buffers, strides);
		
		i = attributes.length - 1;
		
		while(i >= 0)
		{
			strides[i + 1] = strides[i];
			i--;
		}
		
		return createStaticVBO(vboBuffer, attributes, strideLength, strides, drawStyle, (vboBuffer.capacity() * 4) / POS_COLOR_STRIDE);
	}
	
	private final int vboId;
	private final FloatBuffer vboBuffer;
	private final VertexAttribute[] vboAttribs;
	private final int vboStrideLength;
	private final int[] vboAttribOffsets;
	
	private final int glDraw;
	private final int verticeSize;
	
	private VBO(int id, FloatBuffer buffer, VertexAttribute[] attributes, int strideLength, int[] attributeOffsets, int drawStyle, int vertices)
	{
		vboId = id;
		vboBuffer = buffer;
		vboAttribs = attributes;
		vboStrideLength = strideLength;
		vboAttribOffsets = attributeOffsets;
		
		glDraw = drawStyle;
		verticeSize = vertices;
	}
	
	public void bind()
	{
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
	}
	
	public void enable()
	{
		int i = 0;
		
		while(i < vboAttribs.length)
		{
			glEnableVertexAttribArray(vboAttribs[i].getId());
			i++;
		}
	}
	
	public void setupDraw()
	{
		int i = 0;
		VertexAttribute attrib;
		
		while(i < vboAttribs.length)
		{
			attrib = vboAttribs[i];
			
			glVertexAttribPointer(attrib.getId(), attrib.getSize(), GL_FLOAT, false, vboStrideLength, vboAttribOffsets[i]);
			i++;
		}
	}
	
	public void setupDrawEnable()
	{
		int i = 0;
		VertexAttribute attrib;
		
		while(i < vboAttribs.length)
		{
			attrib = vboAttribs[i];
			
			glEnableVertexAttribArray(attrib.getId());
			glVertexAttribPointer(attrib.getId(), attrib.getSize(), GL_FLOAT, false, vboStrideLength, vboAttribOffsets[i]);
			i++;
		}
	}
	
	public void draw()
	{
		glDrawArrays(glDraw, 0, verticeSize);
	}
	
	public void disable()
	{
		int i = 0;
		
		while(i < vboAttribs.length)
		{
			glDisableVertexAttribArray(vboAttribs[i].getId());
			i++;
		}
	}
	
	public void destroy()
	{
		glDeleteBuffers(vboId);
	}
	
	public FloatBuffer getBuffer()
	{
		return vboBuffer;
	}
}
