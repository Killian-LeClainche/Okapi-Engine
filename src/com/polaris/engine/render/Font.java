/**
 *
 */
package com.polaris.engine.render;

import com.polaris.engine.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTBakedChar.Buffer;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static com.polaris.engine.util.ResourceHelper.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTTBakedChar.malloc;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

/**
 * @author lec50
 */
public class Font
{
	
	private static int fontIdWrapper = 0;
	
	public static Font createFont(File fontFile, int pointFont)
	{
		int width = (int) Math.round(Math.pow(2, Math.ceil(MathHelper.log(2, pointFont * 8))));
		int height = (int) Math.round(Math.pow(2, MathHelper.log(2, pointFont * 12)));
		return createFont(fontFile, pointFont, width, height);
	}
	
	public static Font createFont(File fontFile, int pointFont, int width, int height)
	{
		fontIdWrapper = GL11.glGenTextures();
		
		Buffer cdata = malloc(96);
		
		try
		{
			ByteBuffer data = ioResourceToByteBuffer(fontFile);
			
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height);
			
			STBTTFontinfo info = STBTTFontinfo.malloc();
			
			if (STBTruetype.stbtt_InitFont(info, data) == false)
			{
				cdata.free();
				info.free();
				return null;
			}
			
			stbtt_BakeFontBitmap(data, pointFont, pixels, width, height, 32, cdata);
			
			pixels.clear();
			
			glBindTexture(GL_TEXTURE_2D, fontIdWrapper);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, width, height, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			
			return new Font(fontIdWrapper, pointFont, width, height, info, cdata);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			cdata.free();
		}
		return null;
	}
	private int fontTextureId;
	private int fontSize;
	private int fontWidth;
	private int fontHeight;
	private STBTTFontinfo fontInfo;
	private Buffer fontChardata;
	private FloatBuffer xBuffer;
	private FloatBuffer yBuffer;
	
	private Font(int id, int size, int width, int height, STBTTFontinfo info, Buffer data)
	{
		fontTextureId = id;
		fontSize = size;
		fontWidth = width;
		fontHeight = height;
		fontInfo = info;
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
	
	public float getWidth(String text, float scale)
	{
		return getWidth(text) * scale;
	}
	
	public float getWidth(String text)
	{
		float length = 0f;
		for (int i = 0; i < text.length(); i++)
		{
			STBTTBakedChar data = fontChardata.get(text.charAt(i) - 32);
			length += data.xadvance();
		}
		return length;
	}
	
	public VBO draw(String text, float x, float y, float z, float scale)
	{
		//int bufferSize = text.length() * 6 * 5;
		//VBOBuffer vboBuffer = new VBOBuffer(bufferSize);
		//IBOBuffer iboBuffer = new IBOBuffer(bufferSize);
		STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
		//VBO vbo;
		xBuffer.put(0, 0);
		yBuffer.put(0, 0);
		
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y, z);
		GL11.glBegin(GL11.GL_QUADS);
		
		char c;
		float x0, y0, x1, y1;
		for (int i = 0; i < text.length(); i++)
		{
			c = text.charAt(i);
			if (c == '\n')
			{
				xBuffer.put(0, 0);
				yBuffer.put(0, yBuffer.get(0) + fontSize);
				continue;
			}
			
			x = xBuffer.get(0);
			stbtt_GetBakedQuad(fontChardata, fontWidth, fontHeight, c - 32, xBuffer, yBuffer, quad, true);
			
			x0 = quad.x0();
			y0 = quad.y0();
			x1 = quad.x1();
			y1 = quad.y1();
			
			x1 = x0 + (x1 - x0) * scale;
			y0 = y1 + (y0 - y1) * scale;
			
			xBuffer.put(0, x + (xBuffer.get(0) - x) * scale);
			
			GL11.glTexCoord2d(quad.s0(), quad.t1());
			GL11.glVertex3d(x0, y1, z);
			GL11.glTexCoord2d(quad.s1(), quad.t1());
			GL11.glVertex3d(x1, y1, z);
			GL11.glTexCoord2d(quad.s1(), quad.t0());
			GL11.glVertex3d(x1, y0, z);
			GL11.glTexCoord2d(quad.s0(), quad.t0());
			GL11.glVertex3d(x0, y0, z);
			
			/*vboBuffer.addVertex(x0, y1, z);
			vboBuffer.addVertex(x1, y1, z);
			vboBuffer.addVertex(x0, y0, z);

			vboBuffer.addVertex(x0, y0, z);
			vboBuffer.addVertex(x1, y1, z);
			vboBuffer.addVertex(x1, y0, z);*/
			
			/*vboBuffer.addTextureVertex(x0, y1, z, quad.s0(), quad.t1());
			vboBuffer.addTextureVertex(x1, y1, z, quad.s1(), quad.t1());
			vboBuffer.addTextureVertex(x0, y0, z, quad.s0(), quad.t0());
			
			vboBuffer.addTextureVertex(x0, y0, z, quad.s0(), quad.t0());
			vboBuffer.addTextureVertex(x1, y1, z, quad.s1(), quad.t1());
			vboBuffer.addTextureVertex(x1, y0, z, quad.s1(), quad.t0());*/
		}
		
		GL11.glEnd();
		
		GL11.glPopMatrix();
		quad.free();
		
		//iboBuffer.shrinkVBO(vboBuffer, VBO.POS_TEXTURE_STRIDE);
		
		//vbo = VBO.createStaticVBO(GL11.GL_TRIANGLES, VBO.POS_TEXTURE, VBO.POS_TEXTURE_STRIDE, VBO.POS_TEXTURE_OFFSET, vboBuffer);
		//return vbo;
		return null;
	}
	
	public void unbind()
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
