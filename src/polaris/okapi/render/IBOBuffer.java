/**
 *
 */
package polaris.okapi.render;

/**
 * @author Killian Le Clainche
 */
public class IBOBuffer
{
	
	/*private IntBuffer iboBuffer;
	
	public IBOBuffer(int bufferSize)
	{
		iboBuffer = BufferUtils.createIntBuffer(bufferSize);
	}
	
	public void shrinkVBO(VBOBuffer vboBuffer, int strideLength)
	{
		FloatBuffer buffer = vboBuffer.getBuffer();
		
		int offset = strideLength;
		int i = 0;
		int j = vboBuffer.getBufferSize();
		
		List<Float[]> vertices = new ArrayList<Float[]>();
		List<Integer> verticeLoc = new ArrayList<Integer>();
		Iterator<Float[]> verticeIt;
		
		boolean hasNext;
		int indice;
		Float[] vertice;
		
		float x, y, z;
		
		buffer.clear();
		
		i = 0;
		
		while (i < j)
		{
			x = buffer.get(i);
			y = buffer.get(i + 1);
			z = buffer.get(i + 2);
			
			verticeIt = vertices.iterator();
			hasNext = verticeIt.hasNext();
			indice = 0;
			while (hasNext)
			{
				vertice = verticeIt.next();
				if (MathHelperKt.isEqual(vertice[0], x) && MathHelperKt.isEqual(vertice[1], y) && MathHelperKt.isEqual(vertice[2], z))
				{
					hasNext = false;
				}
				else
				{
					hasNext = verticeIt.hasNext();
					indice++;
				}
			}
			
			if (!verticeIt.hasNext())
			{
				vertices.add(new Float[]{x, y, z});
				verticeLoc.add(i);
			}
			
			iboBuffer.put(indice);
			
			i += offset;
		}
		
		indice = 0;
		
		buffer.clear();
		
		for (i = 0; i < verticeLoc.size(); i++, indice += offset)
		{
			j = 0;
			while (j < offset)
			{
				buffer.put(indice, buffer.get(verticeLoc.get(i) + j));
				j++;
			}
		}
	}
	
	public void add(int vertice)
	{
		iboBuffer.put(vertice);
	}
	
	public void addAll(int... vertices)
	{
		for (int vertex : vertices)
		{
			iboBuffer.put(vertex);
		}
	}
	
	/**
	 * @return
	 */
	/*public IntBuffer getBuffer()
	{
		return iboBuffer;
	}*/
	
}
