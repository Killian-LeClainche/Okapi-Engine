/**
 * 
 */
package com.polaris.engine.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.BufferUtils;

import static com.polaris.engine.util.MathHelper.*;

/**
 * @author Killian Le Clainche
 *
 */
public class IBOBuffer
{

	private IntBuffer iboBuffer;
	
	public IBOBuffer(int bufferSize)
	{
		iboBuffer = BufferUtils.createIntBuffer(bufferSize);
	}
	
	public void calculateVBO(VBOBuffer vboBuffer, int strideLength)
	{
		FloatBuffer buffer = vboBuffer.getBuffer();
		
		int offset = strideLength / 4;
		int i = 0;
		int j = vboBuffer.getBufferSize();
		
		List<Float[]> vertices = new ArrayList<Float[]>();
		Iterator<Float[]> verticeIt;
		
		boolean hasNext;
		int indice;
		Float[] vertice;
		
		float x, y, z;
		
		while(i < j)
		{
			x = buffer.get(i);
			y = buffer.get(i + 1);
			z = buffer.get(i + 2);
			
			verticeIt = vertices.iterator();
			hasNext = verticeIt.hasNext();
			indice = 0;
			while(hasNext)
			{
				vertice = verticeIt.next();
				if(isEqual(vertice[0], x) && isEqual(vertice[1], y) && isEqual(vertice[2], z))
				{
					hasNext = false;
				}
				else
				{
					hasNext = verticeIt.hasNext();
					indice++;
				}
			}
			
			if(!verticeIt.hasNext())
			{
				vertices.add(new Float[] {x, y, z});
			}
			
			iboBuffer.put(indice);
			
			i += offset;
		}
	}
	
	public void add(int vertice)
	{
		iboBuffer.put(vertice);
	}
	
	public void addAll(int ... vertices)
	{
		for(int vertex : vertices)
		{
			iboBuffer.put(vertex);
		}
	}
	
	/**
	 * @return
	 */
	public IntBuffer getBuffer()
	{
		return iboBuffer;
	}
	
}
