package com.polaris.engine.util;

import com.polaris.engine.render.model.Model;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Helper
{
	
	public static final ClassLoader classLoader = Helper.class.getClassLoader();
	
	public static final Map<String, Constructor<? extends Model>> modelFormats = new HashMap<String, Constructor<? extends Model>>();
	
	static
	{
		/*try
		{
			modelFormats.put("obj", ObjModel.class.getConstructor(File.class));
		} 
		catch (NoSuchMethodException | SecurityException e) {}*/
	}
	
}
