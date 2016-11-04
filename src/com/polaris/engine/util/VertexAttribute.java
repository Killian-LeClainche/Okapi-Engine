/**
 * 
 */
package com.polaris.engine.util;


/**
 * @author Killian Le Clainche
 *
 */
public class VertexAttribute
{
	
	public static final VertexAttribute POSITION = new VertexAttribute(0, (byte) 3, (byte) 12);
	public static final VertexAttribute COLOR = new VertexAttribute(1, (byte) 4, (byte) 16);
	public static final VertexAttribute NORMAL = new VertexAttribute(2, (byte) 3, (byte) 12);
	public static final VertexAttribute TEXTURE = new VertexAttribute(3, (byte) 2, (byte) 8);
	
	private final int attributeId;
	private final byte attributeSize;
	private final byte attributeStride;
	
	private VertexAttribute(int id, byte attribSize, byte attribStride)
	{
		attributeId = id;
		attributeSize = attribSize;
		attributeStride = attribStride;
	}
	
	public int getAttribId()
	{
		return attributeId;
	}
	
	public byte getVertexSize()
	{
		return attributeSize;
	}
	
	public byte getVertexStride()
	{
		return attributeStride;
	}
	
}
