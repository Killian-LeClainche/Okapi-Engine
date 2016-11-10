/**
 * 
 */
package com.polaris.engine.render;


/**
 * @author Killian Le Clainche
 *
 */
public class TexCoord
{
	
	private float textureMinU;
	private float textureMinV;
	private float textureMaxU;
	private float textureMaxV;
	
	public TexCoord(float minU, float minV, float maxU, float maxV)
	{
		textureMinU = minU;
		textureMinV = minV;
		textureMaxU = maxU;
		textureMaxV = maxV;
	}

	public float getMinU()
	{
		return textureMinU;
	}
	
	public float getMinV()
	{
		return textureMinV;
	}

	public float getMaxU()
	{
		return textureMaxU;
	}

	public float getMaxV()
	{
		return textureMaxV;
	}
	
}
