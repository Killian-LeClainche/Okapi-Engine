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
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;

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

import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;


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
	
	public Collection<Texture> getTextures()
	{
		return textures.values();
	}
	
	public void setTextures(Collection<Texture> textures)
	{
		Iterator<Texture> textureIt = textures.iterator();
		Texture texture;
		while(textureIt.hasNext())
		{
			texture = textureIt.next();
			
			genTexture(texture);
		}
	}
	
	public void genTexture(int textureId, int width, int height, byte[] data, int numMipmaps)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		if(numMipmaps > 1)
		{
			GL11.glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, width, height);
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
			GL30.glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		else
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
	}
	
	public void genTexture(Texture texture, int numMipmaps)
	{
		int textureId = GL11.glGenTextures();
		
		texture.setId(textureId);
		genTexture(textureId, texture.getWidth(), texture.getHeight(), texture.getTextureData(), numMipmaps);
	}
	
	public void genTexture(Texture texture)
	{
		genTexture(texture, 1);
	}
	
	public void genTexture(String textureName, File file, int numMipmaps)
	{
		FileInputStream fileStream;
		FileChannel channel;
		MappedByteBuffer buffer;
		ByteBuffer imageData;
		Image image;
		
		int textureId;
		Texture texture;
		
		try
		{
			fileStream = new FileInputStream(file);
			channel = fileStream.getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			imageData = buffer.compact();
			image = Toolkit.getDefaultToolkit().createImage(buffer.array());
			
			textureId = GL11.glGenTextures();
			texture = new Texture(textureName, image.getWidth(null), image.getHeight(null), imageData);
			
			texture.setId(textureId);
			genTexture(textureId, texture.getWidth(), texture.getHeight(), texture.getTextureData(), numMipmaps);
			
			fileStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void genTexture(String textureName, File file)
	{
		genTexture(textureName, file);
	}
	
	public void destroy()
	{
		Iterator<Texture> textureIt = getTextures().iterator();
		Texture texture;
		
		while(textureIt.hasNext())
		{
			texture = textureIt.next();
			
			deleteTexture(texture);
		}
	}
	
}
