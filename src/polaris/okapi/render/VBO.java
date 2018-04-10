/**
 *
 */
package polaris.okapi.render;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Killian Le Clainche
 */
public class VBO implements IRenderObject
{
	
	public static final VertexAttribute[] POS = {VertexAttribute.POSITION};
	public static final int POS_STRIDE = VertexAttribute.POSITION.getStride();
	public static final int[] POS_OFFSET = {0};
	public static final VertexAttribute[] POS_COLOR = {VertexAttribute.POSITION, VertexAttribute.COLOR};
	public static final int POS_COLOR_STRIDE = POS_STRIDE + VertexAttribute.COLOR.getStride();
	public static final int[] POS_COLOR_OFFSET = {0, POS_STRIDE};
	public static final VertexAttribute[] POS_NORMAL = {VertexAttribute.POSITION, VertexAttribute.NORMAL};
	public static final int POS_NORMAL_STRIDE = POS_STRIDE + VertexAttribute.NORMAL.getStride();
	public static final int[] POS_NORMAL_OFFSET = {0, POS_STRIDE};
	public static final VertexAttribute[] POS_TEXTURE = {VertexAttribute.POSITION, VertexAttribute.TEXTURE};
	public static final int POS_TEXTURE_STRIDE = POS_STRIDE + VertexAttribute.TEXTURE.getStride();
	public static final int[] POS_TEXTURE_OFFSET = {0, POS_STRIDE};
	public static final VertexAttribute[] POS_COLOR_TEXTURE = {VertexAttribute.POSITION, VertexAttribute.COLOR, VertexAttribute.TEXTURE};
	public static final int POS_COLOR_TEXTURE_STRIDE = POS_COLOR_STRIDE + VertexAttribute.TEXTURE.getStride();
	public static final int[] POS_COLOR_TEXTURE_OFFSET = {0, POS_STRIDE, POS_COLOR_STRIDE};
	public static final VertexAttribute[] POS_COLOR_NORMAL = {VertexAttribute.POSITION, VertexAttribute.COLOR, VertexAttribute.NORMAL};
	public static final int POS_COLOR_NORMAL_STRIDE = POS_COLOR_STRIDE + VertexAttribute.NORMAL.getStride();
	public static final int[] POS_COLOR_NORMAL_OFFSET = {0, POS_STRIDE, POS_NORMAL_STRIDE};
	public static final VertexAttribute[] POS_NORMAL_TEXTURE = {VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TEXTURE};
	public static final int POS_NORMAL_TEXTURE_STRIDE = POS_NORMAL_STRIDE + VertexAttribute.TEXTURE.getStride();
	public static final int[] POS_NORMAL_TEXTURE_OFFSET = {0, POS_STRIDE, POS_NORMAL_STRIDE};
	public static final VertexAttribute[] POS_COLOR_NORMAL_TEXTURE = {VertexAttribute.POSITION, VertexAttribute.COLOR, VertexAttribute.NORMAL, VertexAttribute.TEXTURE};
	public static final int POS_COLOR_NORMAL_TEXTURE_STRIDE = POS_COLOR_NORMAL_STRIDE + VertexAttribute.TEXTURE.getStride();
	public static final int[] POS_COLOR_NORMAL_TEXTURE_OFFSET = {
			0, POS_STRIDE, POS_COLOR_STRIDE, POS_COLOR_NORMAL_STRIDE
	};
	private static int vboIdWrapper = 0;
	
	public static VBO createStaticVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, int[] offsets, int verticeSize, VBOBuffer vboBuffer)
	{
		return createVBO(drawStyle, attributes, strideLength, offsets, verticeSize, GL_STATIC_DRAW, vboBuffer);
	}
	
	private static VBO createVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, int[] offsets, int verticeSize, int glDraw, VBOBuffer vboBuffer)
	{
		vboIdWrapper = glGenBuffers();
		
		FloatBuffer buffer = vboBuffer.getBuffer();
		
		buffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vboIdWrapper);
		glBufferData(GL_ARRAY_BUFFER, buffer, glDraw);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		return new VBO(vboIdWrapper, vboBuffer, attributes, strideLength - POS_STRIDE, offsets, drawStyle, verticeSize);
	}
	
	public static VBO createStaticVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, int[] offsets, VBOBuffer vboBuffer)
	{
		return createVBO(drawStyle, attributes, strideLength, offsets, (vboBuffer.getBufferSize() * 4) / strideLength, GL_STATIC_DRAW, vboBuffer);
	}
	
	public static VBO createStaticVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, VBOBuffer... buffers)
	{
		return createVBO(drawStyle, attributes, strideLength, buffers);
	}
	
	private static VBO createVBO(int drawStyle, VertexAttribute[] attributes, int strideLength, VBOBuffer... buffers)
	{
		int[] strides = new int[attributes.length];
		int i = 0;
		
		while (i < attributes.length)
		{
			strides[i] = attributes[i].getStride();
			i++;
		}
		
		VBOBuffer vboBuffer = mixBuffers(buffers);
		
		i = attributes.length - 1;
		
		while (i >= 0)
		{
			strides[i + 1] = strides[i];
			i--;
		}
		
		return createVBO(drawStyle, attributes, strideLength, strides, vboBuffer.getBufferSize() / POS_COLOR_STRIDE, GL_STATIC_DRAW, vboBuffer);
	}
	
	private static VBOBuffer mixBuffers(VBOBuffer[] buffers)
	{
		int bufferSize = 0;
		int i = 0;
		
		while (i < buffers.length)
		{
			bufferSize += buffers[i].getBufferSize();
			i++;
		}
		
		FloatBuffer finalBuffer = BufferUtils.createFloatBuffer(bufferSize);
		
		i = 0;
		
		while (i < buffers.length)
		{
			finalBuffer.put(buffers[i].getBuffer());
			i++;
		}
		
		return new VBOBuffer(finalBuffer);
	}
	private final int vboId;
	private final VBOBuffer vboBuffer;
	private final VertexAttribute[] vboAttribs;
	private final int vboStrideLength;
	private final int[] vboAttribOffsets;
	
	private final int glDraw;
	private final int verticeSize;
	
	private VBO(int id, VBOBuffer buffer, VertexAttribute[] attributes, int strideLength, int[] attributeOffsets, int drawStyle, int vertices)
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
		
		while (i < vboAttribs.length)
		{
			glEnableVertexAttribArray(vboAttribs[i].getId());
			i++;
		}
	}
	
	public void setupDraw()
	{
		int i = 0;
		VertexAttribute attrib;
		
		while (i < vboAttribs.length)
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
		
		while (i < vboAttribs.length)
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
		
		while (i < vboAttribs.length)
		{
			glDisableVertexAttribArray(vboAttribs[i].getId());
			i++;
		}
	}
	
	public void destroy()
	{
		glDeleteBuffers(vboId);
	}
	
	public int getId()
	{
		return vboId;
	}
	
	public VBOBuffer getBuffer()
	{
		return vboBuffer;
	}
	
	public VertexAttribute[] getAttributes()
	{
		return vboAttribs;
	}
	
	public int getStrideLength()
	{
		return vboStrideLength;
	}
	
	public int[] getAttributeOffsets()
	{
		return vboAttribOffsets;
	}
	
	public int getDrawMode()
	{
		return glDraw;
	}
	
	public int getVerticeCount()
	{
		return verticeSize;
	}
	
}
