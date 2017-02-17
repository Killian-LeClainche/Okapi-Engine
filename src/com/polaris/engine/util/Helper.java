package com.polaris.engine.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.polaris.engine.render.model.Model;
import com.polaris.engine.render.model.ObjModel;

public class Helper 
{

	/**
	 * Current OS that this application is running on. Though, this will only give generic os names.
	 * @value windows, linux, osx
	 */
	public static final String osName;
	
	public static final ClassLoader classLoader = Helper.class.getClassLoader();
	
	public static final Map<String, Constructor<? extends Model>> modelFormats = new HashMap<String, Constructor<? extends Model>>();

	static
	{
		String s = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if(s.contains("windows"))
			osName = "windows";
		else if(s.contains("linux"))
			osName = "linux";
		else
			osName = "osx";
		try
		{
			modelFormats.put("obj", ObjModel.class.getConstructor(File.class));
		} 
		catch (NoSuchMethodException | SecurityException e) {}
	}
	
}
