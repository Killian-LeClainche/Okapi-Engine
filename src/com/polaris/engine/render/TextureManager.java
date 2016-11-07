/**
 * 
 */
package com.polaris.engine.render;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

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
	
	public void genTexture(Texture texture, int numMipmaps)
	{
		int textureId = GL11.glGenTextures();
		
		texture.setId(textureId;)
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
		byte[] imageData;
		Image image;
		
		int textureId;
		Texture texture;
		
		try
		{
			fileStream = new FileInputStream(file);
			channel = fileStream.getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			imageData = buffer.array();
			image = Toolkit.getDefaultToolkit().createImage(imageData);
			
			textureId = GL11.glGenTextures();
			texture = new Texture(textureName, image.getWidth(null), image.getHeight(null), imageData);
			
			texture.setId(textureId);
			
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
