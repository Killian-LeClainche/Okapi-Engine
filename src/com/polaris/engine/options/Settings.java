package com.polaris.engine.options;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import com.polaris.engine.App;

public class Settings
{
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
			while(buffer.hasRemaining())
			{
				monitorList.add(Monitor.createMonitor(buffer.get()));
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
	
	private final App application;
	
	private Monitor monitor;
	
	private WindowMode windowMode;
	private boolean updateWindow;
	
	private String title;
	
	private GLCapabilities glCapabilities;
	
	public Settings(App app)
	{
		application = app;
	}
	
	public void init()
	{
		monitor = getMonitor(glfwGetPrimaryMonitor());
		windowMode = WindowMode.WINDOWED;
	}
	
	public boolean createCapabilities()
	{
		glCapabilities = GL.createCapabilities();
		return true;
	}
	
	public GLCapabilities getCapabilities()
	{
		return glCapabilities;
	}

	/**
	 * @return
	 */
	public String getGLVersion()
	{
		return glCapabilities.toString();
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
		return "Vandy";
	}

	/**
	 * @return
	 */
	public int vsyncMode()
	{
		return 1;
	}

	public App getApplication()
	{
		return application;
	}
	
}
