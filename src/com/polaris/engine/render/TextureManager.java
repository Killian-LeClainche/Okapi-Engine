/**
 * 
 */
package com.polaris.engine.render;

import static com.polaris.engine.util.ResourceHelper.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.SimpleBufferedImageFactory;
import org.apache.sanselan.formats.png.PngImageParser;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


/**
 * @author lec50
 *
 */
public class TextureManager
{
	
	private static Map<String, Object> params;
	public static final ImageParser PNG_PARSER;
	
	static
	{
		params = new HashMap<String, Object>();
		PNG_PARSER = new PngImageParser();
		
		params.put(ImageParser.BUFFERED_IMAGE_FACTORY, new SimpleBufferedImageFactory());
	}
	
	private Map<String, Texture> textures;
	
	public TextureManager()
	{
		textures = new HashMap<String, Texture>();
	}
	
	public void genTexture(int textureId, int width, int height, ByteBuffer data, int numMipmaps)
	{
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		if(numMipmaps > 1)
		{
			glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, width, height);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		else
		{
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
	}
	
	public void genTexture(Texture texture, int numMipmaps)
	{
		int textureId = texture.getId();
		
		if(textureId == 0)
		{
			textureId = glGenTextures();
			
			texture.setId(textureId);
		}
		
		genTexture(textureId, texture.getWidth(), texture.getHeight(), texture.getImage(), numMipmaps);	
		
		textures.put(texture.getName(), texture);
	}
	
	public void genTexture(Texture texture)
	{
		genTexture(texture, 1);
	}
	
	public Texture genTexture(String textureName, File textureFile, ImageParser parser, int numMipmaps)
	{
		try
		{
			BufferedImage image = parser.getBufferedImage(textureFile, params);
			int width = image.getWidth();
			int height = image.getHeight();
			
			ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
			
			int textureId = glGenTextures();
			int j, pixel;
			
			for(int i = 0; i < height; i++)
			{
				for(j = 0; j < width; j++)
				{
					pixel = image.getRGB(j, i);
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) (pixel & 0xFF));
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
			
			buffer.flip();
			
			Texture texture = new Texture(textureName, textureId, width, height, buffer);
			
			genTexture(textureId, width, height, buffer, numMipmaps);
			
			textures.put(textureName, texture);
			
			image.flush();
			
			return texture;
		}
		catch(IOException | ImageReadException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Texture genTexture(String textureName, File textureFile, ImageParser parser)
	{
		return genTexture(textureName, textureFile, parser, 1);
	}
	
	public Texture genTexture(String textureName, File textureFile, int numMipmaps)
	{
		return genTexture(textureName, textureFile, PNG_PARSER, numMipmaps);
	}
	
	public Texture genTexture(String textureName, File textureFile)
	{
		return genTexture(textureName, textureFile, PNG_PARSER, 1);
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile, ImageParser parser, int numMipmaps)
	{
		ByteBuffer buffer;
		TextureArray texture;
		
		try
		{
			buffer = ioResourceToByteBuffer(textureFile);
			
			texture = new TextureArray(genTexture(textureName, textureFile, parser, numMipmaps));
			
			textures.put(textureName, texture);
			
			texture.loadArray(buffer);
			
			return texture;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile, ImageParser parser)
	{
		return genTexture(textureName, textureFile, arrayFile, parser, 1);
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile, int numMipmaps)
	{
		return genTexture(textureName, textureFile, arrayFile, PNG_PARSER, numMipmaps);
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile)
	{
		return genTexture(textureName, textureFile, arrayFile, PNG_PARSER, 1);
	}
	
	public void deleteTexture(int textureId)
	{
		glDeleteTextures(textureId);
	}
	
	public void deleteTexture(Texture texture)
	{
		glDeleteTextures(texture.getId());
	}
	
	public Collection<Texture> getTextures()
	{
		return textures.values();
	}
	
	public void setTextures(Collection<Texture> textures)
	{
		Iterator<Texture> textureIt = textures.iterator();
		
		while(textureIt.hasNext())
		{
			genTexture(textureIt.next());
		}
	}
	
	public void clear()
	{
		Iterator<Texture> textureIt = getTextures().iterator();
		
		while(textureIt.hasNext())
		{
			deleteTexture(textureIt.next());
		}
	}
	
	public Texture getTexture(String texture)
	{
		return textures.get(texture);
	}
	
}
