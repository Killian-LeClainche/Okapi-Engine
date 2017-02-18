/**
 * 
 */
package com.polaris.engine.render;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author Killian Le Clainche
 *
 */
public class VAO
{
	
	private static int vaoIdWrapper = 0;
	
	public static VAO createVAO(IRenderObject render)
	{
		vaoIdWrapper = glGenVertexArrays();
		
		glBindVertexArray(vaoIdWrapper);
		render.bind();
		render.setupDrawEnable();
		glBindVertexArray(0);
		render.disable();
		
		return new VAO(vaoIdWrapper, render);
	}
	
	private final int vaoId;
	private final IRenderObject drawCall;
	
	private VAO(int id, IRenderObject render)
	{
		vaoId = id;
		drawCall = render;
	}
	
	public void draw()
	{
		glBindVertexArray(vaoId);
		drawCall.draw();
	}
	
	public void destroy()
	{
		glDeleteVertexArrays(vaoId);
	}
	
	public int getId()
	{
		return vaoId;
	}
	
	public IRenderObject getDrawCall()
	{
		return drawCall;
	}
	
}
