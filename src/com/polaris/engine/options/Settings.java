package com.polaris.engine.options;

import java.io.File;

import org.lwjgl.opengl.GLCapabilities;

public class Settings
{
	
	private static int fullscreen = 0;
	private static File modelDirectory;
	private static File textureDirectory;
	private static File soundDirectory;
	
	public static void load(String resourceLocation)
	{
		modelDirectory = new File(resourceLocation + "/models");
		textureDirectory = new File(resourceLocation + "/textures");
		soundDirectory = new File(resourceLocation + "/sounds");
	}

	public static File getModelDirectory() 
	{
		return modelDirectory;
	}

	public static File getTextureDirectory() 
	{
		return textureDirectory;
	}
	
	public static File getSoundDirectory()
	{
		return soundDirectory;
	}

	public static int getNextWindow()
	{
		return fullscreen;
	}

	public static void setNextWindow(int fullscreen)
	{
		Settings.fullscreen = fullscreen;
	}
	
	public boolean initCapabilities()
	{
		return false;
	}
	
	public GLCapabilities getCapabilities()
	{
		return null;
	}

	/**
	 * @return
	 */
	public String getGLVersion()
	{
		return null;
	}

}
