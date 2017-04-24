/**
 *
 */
package com.polaris.engine.render;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @author lec50
 */
public class Draw
{
	public static void addColor(Vector4f color)
	{
		GL11.glColor4f(color.x, color.y, color.z, color.w);
	}
	
	public static void rect(float x, float y, float x1, float y1, float z)
	{
		addVertex(x, y1, z);
		addVertex(x1, y1, z);
		addVertex(x1, y, z);
		addVertex(x, y, z);
	}
	
	public static void addVertex(float x, float y, float z)
	{
		GL11.glVertex3f(x, y, z);
	}
	
	public static void rect(float x, float y, float x1, float y1, float z, Vector4f color)
	{
		addColorVertex(x, y1, z, color);
		addColorVertex(x1, y1, z, color);
		addColorVertex(x1, y, z, color);
		addColorVertex(x, y, z, color);
	}
	
	public static void addColorVertex(float x, float y, float z, Vector4f color)
	{
		GL11.glColor4f(color.x, color.y, color.z, color.w);
		GL11.glVertex3f(x, y, z);
	}
	
	public static void rectUV(float x, float y, float x1, float y1, float z)
	{
		addTextureVertex(x, y1, z, 0, 1);
		addTextureVertex(x1, y1, z, 1, 1);
		addTextureVertex(x1, y, z, 1, 0);
		addTextureVertex(x, y, z, 0, 0);
	}
	
	public static void addTextureVertex(float x, float y, float z, float u, float v)
	{
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3f(x, y, z);
	}
	
	public static void rectUV(float x, float y, float x1, float y1, float z, TexCoord texture)
	{
		float u = texture.getMinU();
		float v = texture.getMinV();
		float u1 = texture.getMaxU();
		float v1 = texture.getMaxV();
		
		addTextureVertex(x, y1, z, u, v1);
		addTextureVertex(x1, y1, z, u1, v1);
		addTextureVertex(x1, y, z, u1, v);
		addTextureVertex(x, y, z, u, v);
	}
	
	public static void rect(float x, float y, float x1, float y1, float z, Vector4f bottomLeft, Vector4f bottomRight, Vector4f topRight, Vector4f topLeft)
	{
		addColorVertex(x, y1, z, bottomLeft);
		addColorVertex(x1, y1, z, bottomRight);
		addColorVertex(x1, y, z, topRight);
		addColorVertex(x, y, z, topLeft);
	}
	
	public static void rect(float x, float y, float x1, float y1, float z, float thickness)
	{
		float xAdd = x + thickness;
		float yAdd = y + thickness;
		float xSub = x1 - thickness;
		float ySub = y1 - thickness;
		
		addVertex(xAdd, yAdd, z);
		addVertex(xSub, yAdd, z);
		addVertex(x1, y, z);
		addVertex(x, y, z);
		
		addVertex(x, y1, z);
		addVertex(xAdd, ySub, z);
		addVertex(xAdd, yAdd, z);
		addVertex(x, y, z);
		
		addVertex(x, y1, z);
		addVertex(x1, y1, z);
		addVertex(xSub, ySub, z);
		addVertex(xAdd, ySub, z);
		
		addVertex(xSub, ySub, z);
		addVertex(x1, y1, z);
		addVertex(x1, y, z);
		addVertex(xSub, yAdd, z);
	}
	
	public static void rect(float x, float y, float x1, float y1, float z, float thickness, Vector4f outColor, Vector4f inColor)
	{
		float xAdd = x + thickness;
		float yAdd = y + thickness;
		float xSub = x1 - thickness;
		float ySub = y1 - thickness;
		
		addColorVertex(xAdd, yAdd, z, inColor);
		addColorVertex(xSub, yAdd, z, inColor);
		addColorVertex(x1, y, z, outColor);
		addColorVertex(x, y, z, outColor);
		
		addColorVertex(x, y1, z, outColor);
		addColorVertex(xAdd, ySub, z, inColor);
		addColorVertex(xAdd, yAdd, z, inColor);
		addColorVertex(x, y, z, outColor);
		
		addColorVertex(x, y1, z, outColor);
		addColorVertex(x1, y1, z, outColor);
		addColorVertex(xSub, ySub, z, inColor);
		addColorVertex(xAdd, ySub, z, inColor);
		
		addColorVertex(xSub, ySub, z, inColor);
		addColorVertex(x1, y1, z, outColor);
		addColorVertex(x1, y, z, outColor);
		addColorVertex(xSub, yAdd, z, inColor);
	}
	
	public static void arc(float circleX, float circleY, float z, float radius, int resolution, float thickness)
	{
		arc(circleX, circleY, z, radius, 0, (float) Math.PI * 2, resolution, thickness);
	}
	
	public static void arc(float circleX, float circleY, float z, float radius, float startAngle, float endAngle, int resolution, float thickness)
	{
		float theta = (endAngle - startAngle) / (resolution);
		
		float cos = (float) cos(startAngle);
		float sin = (float) sin(startAngle);
		
		float x = radius * cos;
		float y = radius * sin;
		
		float nullArea = radius - thickness;
		
		while (resolution > 0)
		{
			addVertex(circleX + x, circleY + y, z);
			addVertex(circleX + nullArea * cos, circleY + nullArea * sin, z);
			
			startAngle += theta;
			
			cos = (float) cos(startAngle);
			sin = (float) sin(startAngle);
			
			x = radius * cos;
			y = radius * sin;
			
			addVertex(circleX + nullArea * cos, circleY + nullArea * sin, z);
			addVertex(circleX + x, circleY + y, z);
			
			resolution--;
		}
	}
	
	public static void arc(float circleX, float circleY, float z, float radius, int resolution, float thickness, Vector4f startColor, Vector4f endColor)
	{
		arc(circleX, circleY, z, radius, 0, (float) Math.PI * 2, resolution, thickness, startColor, endColor);
	}
	
	public static void arc(float circleX, float circleY, float z, float radius, float startAngle, float endAngle, int resolution, float thickness, Vector4f startColor, Vector4f endColor)
	{
		float theta = (endAngle - startAngle) / (resolution);
		
		float cos = (float) cos(startAngle);
		float sin = (float) sin(startAngle);
		
		float x = radius * cos;
		float y = radius * sin;
		
		float nullArea = radius - thickness;
		
		endColor.sub(startColor);
		endColor.div(resolution);
		
		while (resolution > 0)
		{
			addColorVertex(circleX + x, circleY + y, z, startColor);
			addColorVertex(circleX + nullArea * cos, circleY + nullArea * sin, z, startColor);
			
			startColor.add(endColor);
			
			startAngle += theta;
			
			cos = (float) cos(startAngle);
			sin = (float) sin(startAngle);
			
			x = radius * cos;
			y = radius * sin;
			
			addColorVertex(circleX + nullArea * cos, circleY + nullArea * sin, z, startColor);
			addColorVertex(circleX + x, circleY + y, z, startColor);
			
			resolution--;
		}
		
		endColor.mul(resolution);
		endColor.add(startColor);
	}
	
	public static void arcCircle(float circleX, float circleY, float z, float radius, int resolution, float thickness, Vector4f outColor, Vector4f inColor)
	{
		float angle = 0;
		float theta = (float) Math.PI * 2 / resolution;
		
		float cos = 1;
		float sin = 0;
		
		float x = radius * cos;
		float y = 0;
		
		float nullArea = radius - thickness;
		
		while (resolution > 0)
		{
			addColorVertex(circleX + x, circleY + y, z, outColor);
			addColorVertex(circleX + nullArea * cos, circleY + nullArea * sin, z, inColor);
			
			angle += theta;
			
			cos = (float) cos(angle);
			sin = (float) sin(angle);
			
			x = radius * cos;
			y = radius * sin;
			
			addColorVertex(circleX + nullArea * cos, circleY + nullArea * sin, z, inColor);
			addColorVertex(circleX + x, circleY + y, z, outColor);
			
			resolution--;
		}
	}
	
	public static void circle(float circleX, float circleY, float z, float radius, int resolution)
	{
		circle(circleX, circleY, z, radius, 0, (float) Math.PI * 2, resolution);
	}
	
	public static void circle(float circleX, float circleY, float z, float radius, float startAngle, float endAngle, int resolution)
	{
		float theta = (endAngle - startAngle) / resolution;
		addVertex(circleX, circleY, z);
		while (resolution >= 0)
		{
			addVertex(circleX + radius * (float) cos((endAngle -= theta)), circleY + radius * (float) sin(endAngle), z);
			
			resolution--;
		}
	}
	
	public static void circle(float circleX, float circleY, float z, float radius, int resolution, Vector4f outColor, Vector4f inColor)
	{
		circle(circleX, circleY, z, radius, 0, (float) Math.PI * 2, resolution, outColor, inColor);
	}
	
	public static void circle(float circleX, float circleY, float z, float radius, float startAngle, float endAngle, int resolution, Vector4f outColor, Vector4f inColor)
	{
		float theta = (endAngle - startAngle) / resolution;
		addColorVertex(circleX, circleY, z, inColor);
		while (resolution >= 0)
		{
			addColorVertex(circleX + radius * (float) cos((endAngle -= theta)), circleY + radius * (float) sin(endAngle), z, outColor);
			
			resolution--;
		}
	}
}
