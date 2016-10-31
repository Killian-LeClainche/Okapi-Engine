package com.polaris.engine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Keyboard 
{
	
	private final App application;
	private final BidiMap<Integer, Key> keyMapping;
	
	public Keyboard(App app)
	{
		application = app;
		keyMapping = new DualHashBidiMap<Integer, Key>();
	}
	
	public void update(double delta)
	{
		
	}

}
