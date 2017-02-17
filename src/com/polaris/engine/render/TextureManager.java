/**
 * 
 */
package com.polaris.engine.render;

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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.polaris.engine.App;


/**
 * @author lec50
 *
 */
public class TextureManager
{
	
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
			glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA, width, height);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
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
	
	public Texture genTexture(String textureName, File textureFile, int numMipmaps)
	{
		FileInputStream fileStream;
		FileChannel channel;
		MappedByteBuffer buffer;
		Image image;
		
		int textureId;
		Texture texture;
		
		try
		{
			fileStream = new FileInputStream(textureFile);
			channel = fileStream.getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			image = Toolkit.getDefaultToolkit().createImage(buffer.array());
			
			textureId = glGenTextures();
			texture = new Texture(textureName, textureId, image.getWidth(null), image.getHeight(null), buffer);

			genTexture(textureId, texture.getWidth(), texture.getHeight(), buffer, numMipmaps);
			
			textures.put(textureName, texture);
			
			fileStream.close();
			
			return texture;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Texture genTexture(String textureName, File textureFile)
	{
		return genTexture(textureName, textureFile);
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile, int numMipmaps)
	{
		FileInputStream fileStream;
		FileChannel channel;
		MappedByteBuffer buffer;
		TextureArray texture;
		
		try
		{
			fileStream = new FileInputStream(arrayFile);
			channel = fileStream.getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			
			texture = new TextureArray(genTexture(textureName, textureFile, numMipmaps));
			
			textures.put(textureName, texture);
			
			texture.loadArray(buffer);
			
			fileStream.close();
			
			return texture;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public TextureArray genTexture(String textureName, File textureFile, File arrayFile)
	{
		return genTexture(textureName, textureFile, arrayFile, 1);
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
