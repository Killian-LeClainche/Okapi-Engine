/**
 *
 */
package com.polaris.engine.render;

import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.SimpleBufferedImageFactory;
import org.apache.sanselan.formats.png.PngImageParser;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.polaris.engine.util.ResourceHelper.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;


/**
 * @author lec50
 */
public class TextureManager
{
	
	public static final ImageParser PNG_PARSER;
	private static Map<String, Object> params;
	
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
	
	public Texture genTexture(String textureName, File textureFile, ImageParser parser)
	{
		return genTexture(textureName, textureFile, parser, 1);
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
			
			for (int i = 0; i < height; i++)
			{
				for (j = 0; j < width; j++)
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
		catch (IOException | ImageReadException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void genTexture(int textureId, int width, int height, ByteBuffer data, int numMipmaps)
	{
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		if (numMipmaps > 1)
		{
			glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, width, height);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		else
		{
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
	}
	
	public Texture genTexture(String textureName, File textureFile, int numMipmaps)
	{
		return genTexture(textureName, textureFile, PNG_PARSER, numMipmaps);
	}
	
	public Texture genTexture(String textureName, File textureFile)
	{
		return genTexture(textureName, textureFile, PNG_PARSER, 1);
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile, ImageParser parser)
	{
		return genTexture(textureName, textureFile, arrayFile, parser, 1);
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
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
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
	
	public void clear()
	{
		Iterator<Texture> textureIt = getTextures().iterator();
		
		while (textureIt.hasNext())
		{
			deleteTexture(textureIt.next());
		}
	}
	
	public Collection<Texture> getTextures()
	{
		return textures.values();
	}
	
	public void deleteTexture(Texture texture)
	{
		glDeleteTextures(texture.getId());
	}
	
	public void setTextures(Collection<Texture> textures)
	{
		Iterator<Texture> textureIt = textures.iterator();
		
		while (textureIt.hasNext())
		{
			genTexture(textureIt.next());
		}
	}
	
	public void genTexture(Texture texture)
	{
		genTexture(texture, 1);
	}
	
	public void genTexture(Texture texture, int numMipmaps)
	{
		int textureId = texture.getId();
		
		if (textureId == 0)
		{
			textureId = glGenTextures();
			
			texture.setId(textureId);
		}
		
		genTexture(textureId, texture.getWidth(), texture.getHeight(), texture.getImage(), numMipmaps);
		
		textures.put(texture.getName(), texture);
	}
	
	public Texture getTexture(String texture)
	{
		return textures.get(texture);
	}
	
}
