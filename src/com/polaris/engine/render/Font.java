/**
 * 
 */
package com.polaris.engine.render;

import static com.polaris.engine.util.ResourceHelper.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBTTBakedChar.malloc;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar.Buffer;

import com.polaris.engine.util.MathHelper;
/**
 * @author lec50
 *
 */
public class Font
{
	
	private static int fontIdWrapper = 0;
	
	public static Font createFont(File fontFile, int pointFont, int width, int height)
	{
		fontIdWrapper = GL11.glGenTextures();
		
		Buffer cdata = malloc(96);
		
		try
		{
			ByteBuffer data = ioResourceToByteBuffer(fontFile);
			
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height);
			
			stbtt_BakeFontBitmap(data, pointFont, pixels, width, height, 32, cdata);
			
			glBindTexture(GL_TEXTURE_2D, fontIdWrapper);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, width, height, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			
			return new Font(fontIdWrapper, pointFont, width, height, cdata);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			cdata.free();
		}
		
		return null;
	}
	
	public static Font createFont(File fontFile, int pointFont)
	{
		int width = (int) Math.round(Math.pow(2, Math.ceil(MathHelper.log(2, pointFont * 8))));
		int height = (int) Math.round(Math.pow(2, MathHelper.log(2, pointFont * 12)));
		return createFont(fontFile, pointFont, width, height);
	}
	
	private int fontTextureId;
	private int fontSize;
	private int fontWidth;
	private int fontHeight;
	private Buffer fontChardata;
	private FloatBuffer xBuffer;
	private FloatBuffer yBuffer;
	
	private Font(int id, int size, int width, int height, Buffer data)
	{
		fontTextureId = id;
		fontSize = size;
		fontWidth = width;
		fontHeight = height;
		fontChardata = data;
		
		xBuffer = BufferUtils.createFloatBuffer(1);
		yBuffer = BufferUtils.createFloatBuffer(1);
		
		xBuffer.put(0, 0);
		yBuffer.put(0, 0);
	}
	
	public void bind()
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId);
	}
	
	public VBO draw(String text, float x, float y, float z)
	{
		int bufferSize = text.length() * 4 * 5;
		VBOBuffer vboBuffer = new VBOBuffer(bufferSize);
		IBOBuffer iboBuffer = new IBOBuffer(bufferSize);
		STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
		VBO vbo;
		xBuffer.put(0, x);
		yBuffer.put(0, y);
		
		//GL11.glColor4d(1, 1, 1, 1);
		//GL11.glBegin(GL11.GL_QUADS);
		
		char c;
		for(int i = 0; i < text.length(); i++)
		{
			c = text.charAt(i);
			if(c == '\n')
			{
				xBuffer.put(0, x);
				yBuffer.put(0, yBuffer.get(0) + fontSize);
				continue;
			}
			
			stbtt_GetBakedQuad(fontChardata, fontWidth, fontHeight, c - 32, xBuffer, yBuffer, quad, true);
			
			/*GL11.glTexCoord2d(quad.s0(), quad.t1());
			GL11.glVertex3d(quad.x0(), quad.y1(), z);
			GL11.glTexCoord2d(quad.s1(), quad.t1());
			GL11.glVertex3d(quad.x1(), quad.y1(), z);
			GL11.glTexCoord2d(quad.s1(), quad.t0());
			GL11.glVertex3d(quad.x1(), quad.y0(), z);
			GL11.glTexCoord2d(quad.s0(), quad.t0());
			GL11.glVertex3d(quad.x0(), quad.y0(), z);*/
			
			vboBuffer.addTextureVertex(quad.x0(), quad.y1(), z, quad.s0(), quad.t1());
			vboBuffer.addTextureVertex(quad.x1(), quad.y1(), z, quad.s1(), quad.t1());
			vboBuffer.addTextureVertex(quad.x1(), quad.y0(), z, quad.s1(), quad.t0());
			vboBuffer.addTextureVertex(quad.x0(), quad.y0(), z, quad.s0(), quad.t0());
		}
		
		//GL11.glEnd();
		
		quad.free();
		
		iboBuffer.shrinkVBO(vboBuffer, VBO.POS_TEXTURE_STRIDE);
		
		vbo = VBO.createStaticVBO(GL15.GL_STATIC_DRAW, VBO.POS_TEXTURE, VBO.POS_TEXTURE_STRIDE, VBO.POS_TEXTURE_OFFSET, vboBuffer);
		return vbo;
		//return null;
	}
	
	public void destroy()
	{
		fontChardata.free();
		GL11.glDeleteTextures(fontTextureId);
	}
	
	public int getId()
	{
		return fontTextureId;
	}
	
	public int getSize()
	{
		return fontSize;
	}
	
}
