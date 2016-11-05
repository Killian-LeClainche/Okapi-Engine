/**
 * 
 */
package com.polaris.engine.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.IntBuffer;

/**
 * @author Killian Le Clainche
 *
 */
public class IBO implements IRenderObject
{
	private static int iboIdWrapper = 0;
	
	public static IBO createIBO(VBO vbo, IntBuffer buffer)
	{
		iboIdWrapper = glGenBuffers();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboIdWrapper);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, vbo.getDrawMode());
		
		return new IBO(iboIdWrapper, buffer, vbo);
	}
	
	private final int iboId;
	private final IntBuffer iboBuffer;
	private final VBO vbo;
	
	private IBO(int id, IntBuffer buffer, VBO vertices)
	{
		iboId = id;
		iboBuffer = buffer;
		vbo = vertices;
	}
	
	public void bind()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
		vbo.bind();
	}
	
	public void enable()
	{
		vbo.enable();
	}
	
	public void setupDraw()
	{
		vbo.setupDraw();
	}
	
	public void setupDrawEnable()
	{
		vbo.setupDrawEnable();
	}
	
	public void draw()
	{
		glDrawElements(vbo.getDrawMode(), vbo.getVerticeCount(), GL_FLOAT, 0);
	}
	
	public void disable()
	{
		vbo.disable();
	}
	
	public void destroy()
	{
		vbo.destroy();
		glDeleteBuffers(iboId);
	}
	
	public int getId()
	{
		return iboId;
	}
	
	public IntBuffer getBuffer()
	{
		return iboBuffer;
	}
	
	public VBO getVBO()
	{
		return vbo;
	}
	
}
