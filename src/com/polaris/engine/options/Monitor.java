/**
 *
 */
package com.polaris.engine.options;

import org.lwjgl.glfw.GLFWGammaRamp;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author lec50
 */
public class Monitor
{
	
	public static Monitor createMonitor(long monitor)
	{
		String name;
		int[] physicalSizeX = new int[1];
		int[] physicalSizeY = new int[1];
		int[] xpos = new int[1];
		int[] ypos = new int[1];
		GLFWVidMode videoMode;
		GLFWGammaRamp gammaRamp;
		
		name = glfwGetMonitorName(monitor);
		glfwGetMonitorPhysicalSize(monitor, physicalSizeX, physicalSizeY);
		glfwGetMonitorPos(monitor, xpos, ypos);
		videoMode = glfwGetVideoMode(monitor);
		gammaRamp = glfwGetGammaRamp(monitor);
		
		return new Monitor(name, monitor, physicalSizeX[0], physicalSizeY[0], xpos[0], ypos[0], videoMode, gammaRamp);
	}
	
	private final String monitorName;
	private final long monitorInstance;
	private final int physicalSizeX;
	private final int physicalSizeY;
	private final int monitorPosX;
	private final int monitorPosY;
	private final GLFWVidMode videoMode;
	private final GLFWGammaRamp gammaRamp;
	
	private Monitor(String name, long instance, int sizeX, int sizeY, int xpos, int ypos, GLFWVidMode mode, GLFWGammaRamp ramp)
	{
		monitorName = name;
		monitorInstance = instance;
		
		physicalSizeX = sizeX;
		physicalSizeY = sizeY;
		monitorPosX = xpos;
		monitorPosY = ypos;
		
		videoMode = mode;
		gammaRamp = ramp;
	}
	
	public String getName()
	{
		return monitorName;
	}
	
	public long getInstance()
	{
		return monitorInstance;
	}
	
	public int getSizeX()
	{
		return physicalSizeX;
	}
	
	public int getSizeY()
	{
		return physicalSizeY;
	}
	
	public int getPosX()
	{
		return monitorPosX;
	}
	
	public int getPosY()
	{
		return monitorPosY;
	}
	
	public GLFWVidMode getVideoMode()
	{
		return videoMode;
	}
	
	public GLFWGammaRamp getGammaRamp()
	{
		return gammaRamp;
	}
	
}
