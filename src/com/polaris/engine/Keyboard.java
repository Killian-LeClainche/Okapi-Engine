package com.polaris.engine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Keyboard 
{
	
	private final App application;
	private final BidiMap<Integer, Key> keyMapping;
	private final BidiMap<String, Key> nameMapping;
	
	public Keyboard(App app)
	{
		application = app;
		keyMapping = new DualHashBidiMap<Integer, Key>();
		nameMapping = new DualHashBidiMap<String, Key>();
	}

}
