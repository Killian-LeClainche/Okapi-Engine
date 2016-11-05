/**
 * 
 */
package com.polaris.engine.util;


/**
 * @author Killian Le Clainche
 *
 */
public enum VertexAttribute
{
	
	POSITION((byte) 0, (byte) 3, (byte) 12), COLOR((byte) 1, (byte) 4, (byte) 16), NORMAL((byte) 2, (byte) 3, (byte) 12), 
	TEXTURE((byte) 3, (byte) 2, (byte) 8);
	
	private final byte attributeId;
	private final byte attributeSize;
	private final byte attributeStride;
	
	private VertexAttribute(byte id, byte attribSize, byte attribStride)
	{
		attributeId = id;
		attributeSize = attribSize;
		attributeStride = attribStride;
	}
	
	public byte getId()
	{
		return attributeId;
	}
	
	public byte getSize()
	{
		return attributeSize;
	}
	
	public byte getStride()
	{
		return attributeStride;
	}
	
}
