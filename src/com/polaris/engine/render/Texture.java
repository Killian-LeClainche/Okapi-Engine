package com.polaris.engine.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.nio.ByteBuffer;

public class Texture
{
	private TextureManager manager;
	
	private String textureName;
	private int textureId;
	private int textureWidth;
	private int textureHeight;
	private ByteBuffer textureData;

	/**
	 * @param textureName
	 * @param width
	 * @param height
	 * @param imageData
	 */
	public Texture(String name, int id, int width, int height, ByteBuffer data)
	{
		textureName = name;
		textureId = id;
		textureWidth = width;
		textureHeight = height;
		textureData = data;
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, textureId);
	}
	
	public void setId(int id)
	{
		if(textureId != 0)
		{
			manager.deleteTexture(this);
		}
		textureId = id;
	}
	
	public String getName()
	{
		return textureName;
	}	
	
	public int getId()
	{
		return textureId;
	}
	
	public int getWidth()
	{
		return textureWidth;
	}

	public int getHeight()
	{
		return textureHeight;
	}
	
	public ByteBuffer getImage()
	{
		return textureData;
	}

	
}
