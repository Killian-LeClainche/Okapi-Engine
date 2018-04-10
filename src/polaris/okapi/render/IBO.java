/**
 *
 */
package polaris.okapi.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author Killian Le Clainche
 */
public class IBO implements IRenderObject
{
	private static int iboIdWrapper = 0;
	
	public static IBO createIBO(VBO vbo, IBOBuffer iboBuffer)
	{
		iboIdWrapper = glGenBuffers();
		
		iboBuffer.getBuffer().flip();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboIdWrapper);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, iboBuffer.getBuffer(), vbo.getDrawMode());
		
		return new IBO(iboIdWrapper, iboBuffer, vbo);
	}
	
	private final int iboId;
	private final IBOBuffer iboBuffer;
	private final VBO vbo;
	
	private IBO(int id, IBOBuffer buffer, VBO vertices)
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
	
	public IBOBuffer getBuffer()
	{
		return iboBuffer;
	}
	
	public VBO getVBO()
	{
		return vbo;
	}
	
}
