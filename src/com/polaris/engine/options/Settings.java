package com.polaris.engine.options;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWMonitorCallback;

public class Settings implements java.io.Serializable
{
	private static final long serialVersionUID = -2407354674964783920L;
	
	private static boolean staticInitialized = false;
	private static List<Monitor> monitorList;
	
	public static void staticInit()
	{
		if(!staticInitialized)
		{
			monitorList = new ArrayList<Monitor>();
			glfwSetMonitorCallback(GLFWMonitorCallback.create((monitor, event) -> {
				if(event == GLFW_CONNECTED)
				{
					monitorList.add(Monitor.createMonitor(monitor));
				}
				else
				{
					for(int i = 0; i < monitorList.size(); i++)
					{
						if(monitorList.get(i).getInstance() == monitor)
						{
							monitorList.remove(i);
							i = monitorList.size();
						}
					}
				}
			}));
			PointerBuffer buffer = glfwGetMonitors();
			int i = 0;
			while(buffer.capacity() > i)
			{
				monitorList.add(Monitor.createMonitor(buffer.get(i)));
				i++;
			}
			staticInitialized = true;
		}
	}
	
	public static boolean hasMonitors()
	{
		return !monitorList.isEmpty();
	}
	
	public static Monitor getMonitor(long instance)
	{
		Monitor monitor;
		for(int i = 0; i < monitorList.size(); i++)
		{
			monitor = monitorList.get(i);
			if(monitor.getInstance() == instance)
			{
				return monitor;
			}
		}
		return null;
	}
	
	private Monitor monitor;
	
	private WindowMode defaultWindowMode;
	private WindowMode windowMode;
	private boolean updateWindow;
	
	private int windowPosX, windowPosY;
	
	protected int windowWidth, windowHeight;
	
	private String title;

	public void init()
	{
		monitor = getMonitor(glfwGetPrimaryMonitor());
		windowMode = WindowMode.WINDOWED;
		defaultWindowMode = windowMode;
		windowPosX = windowPosY = -1;
		windowWidth = windowHeight = 0;
		
		title = "Test";
	}

	/**
	 * @return
	 */
	public boolean shouldWindowUpdate()
	{
		boolean shouldUpdate = updateWindow;
		updateWindow = false;
		return shouldUpdate;
	}
	
	public void changeWindowMode()
	{
		if(defaultWindowMode == windowMode)
			windowMode = WindowMode.FULLSCREEN;
		else
			windowMode = defaultWindowMode;
	}

	/**
	 * @return
	 */
	public WindowMode getWindowMode()
	{
		return windowMode;
	}

	/**
	 * @return
	 */
	public Monitor getMonitor()
	{
		return monitor;
	}
	
	public long getMonitorInstance()
	{
		return getMonitor().getInstance();
	}
	
	public String getTitle()
	{
		return title;
	}

	public int getWindowXPos(int w)
	{
		if(windowPosX == -1)
			windowPosX = (monitor.getVideoMode().width() - w) / 2;
		return windowPosX;
	}
	
	public int getWindowYPos(int h)
	{
		if(windowPosY == -1)
			windowPosY = (monitor.getVideoMode().height() - h) / 2;
		return windowPosY;
	}
	
	public void setWindowWidth(int w)
	{
		windowWidth = w;
		windowPosX = (monitor.getVideoMode().width() - w) / 2;
	}
	
	public void setWindowHeight(int h)
	{
		windowHeight = h;
		windowPosY = (monitor.getVideoMode().height() - h) / 2;
	}
	
	public int getWindowWidth()
	{
		return windowWidth;
	}
	
	public int getWindowHeight()
	{
		return windowHeight;
	}
	
	/**
	 * @return
	 */
	public int vsyncMode()
	{
		return 1;
	}

}
