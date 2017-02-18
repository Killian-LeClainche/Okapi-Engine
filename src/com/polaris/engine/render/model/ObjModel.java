package com.polaris.engine.render.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjModel //extends Model
{

	/*public ObjModel(File modelLocation)
	{
		super(modelLocation);
	}

	protected void loadPolygons(BufferedReader reader) throws IOException
	{
		List<Short[]> faceList = new ArrayList<Short[]>();
		List<Float[]> vertexList = new ArrayList<Float[]>();
		List<Float[]> textureCoordList = new ArrayList<Float[]>();
		String line = null;
		supportsQuads = false;
		while((line = reader.readLine()) != null)
		{
			if(line.startsWith("v "))
			{
				String[] desc = line.substring(2).split(" ");
				vertexList.add(new Float[]{parse(desc[0]), parse(desc[1]), parse(desc[2])});
			}
			else if(line.startsWith("vt "))
			{
				String[] desc = line.substring(3).split(" ");
				textureCoordList.add(new Float[]{parse(desc[0]), parse(desc[1])});
			}
			else if(line.startsWith("f "))
			{
				String[] desc = line.substring(2).split(" ");
				Short[] coordArray = new Short[desc.length * 2];
				if(desc.length == 4)
				{
					supportsQuads = true;
				}
				for(int i = 0; i < desc.length; i++)
				{
					String[] coord = desc[i].split("/");
					coordArray[i * 2] = parse1(coord[0]);
					coordArray[i * 2 + 1] = parse1(coord[1]);
				}
				faceList.add(coordArray);
			}
		}
		faceArray = new short[faceList.size()][(supportsQuads ? 4 : 3) * 2];
		vertexArray = new float[vertexList.size()][3];
		textureCoordArray = new float[textureCoordList.size()][2];
		int i;
		int j;
		for(i = 0; i < vertexList.size(); i++)
		{
			Float[] vertex = vertexList.get(i);
			for(j = 0; j < vertex.length; j++)
			{
				vertexArray[i][j] = vertex[j];
			}
		}
		for(i = 0; i < textureCoordList.size(); i++)
		{
			Float[] textureCoord = textureCoordList.get(i);
			for(j = 0; j < textureCoord.length; j++)
			{
				textureCoordArray[i][j] = textureCoord[j];
			}
		}
		for(i = 0; i < faceList.size(); i++)
		{
			Short[] coords = faceList.get(i);
			for(j = 0; j < coords.length; j++)
			{
				faceArray[i][j] = (short) (coords[j] - 1);
			}
		}
	}*/
}
