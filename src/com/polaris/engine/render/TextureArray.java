package com.polaris.engine.render;

import java.nio.ByteBuffer;

public class TextureArray extends Texture
{
	
	private TexCoord[] textures;
	
	public TextureArray(String name, int id, int width, int height, ByteBuffer data)
	{
		super(name, id, width, height, data);
	}

	public TextureArray(Texture texture)
	{
		super(texture.getName(), texture.getId(), texture.getWidth(), texture.getHeight(), texture.getImage());
	}

	public void loadArray(ByteBuffer buffer)
	{
		textures = new TexCoord[buffer.getInt()];
		
		int index = 0;
		
		while(buffer.hasRemaining())
		{
			textures[index] = new TexCoord(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
			index++;
		}
	}
	
	public TexCoord[] getArray()
	{
		return textures;
	}
	
	public TexCoord getTexCoord(int i)
	{
		return textures[i];
	}
	
}
