package com.polaris.engine;

import static com.polaris.engine.render.OpenGL.glClearBuffers;
import static com.polaris.engine.render.Texture.getTextureData;
import static com.polaris.engine.render.Texture.loadTextureData;
import static com.polaris.engine.render.Window.destroy;
import static com.polaris.engine.render.Window.updateSize;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Configuration;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.logic.LogicGui;
import com.polaris.engine.options.Monitor;
import com.polaris.engine.options.Settings;
import com.polaris.engine.render.OpenGL;
import com.polaris.engine.sound.OpenAL;
import com.polaris.engine.thread.AppPacket;
import com.polaris.engine.thread.LogicApp;
import com.polaris.engine.thread.PacketComparator;

public abstract class App 
{
	
	public static final Log log = LogFactory.getLog(App.class);
	
	/**
	 * long instance of the window this application takes on.
	 */
	private long windowInstance;
	
	private final Set<AppPacket> incomingPackets;
	
	private final LogicApp logicThread;
	
	/**
	 * Instance of application's sound system.
	 */
	private final OpenAL soundSystem;
	
	/**
	 * Instance of application's mouse.
	 */
	private final Input input;
	
	/**
	 * The settings of the game, allows for inheritance.
	 */
	private Settings gameSettings;
	
	private boolean isRunning;
	
	/**
	 * Instance of current screen being displayed.
	 */
	private Gui currentGui;

	public App(boolean debug)
	{
		Configuration.DISABLE_CHECKS.set(!debug);
		Configuration.DEBUG.set(debug);
		Configuration.GLFW_CHECK_THREAD0.set(!debug);
		
		soundSystem = new OpenAL(this);
		input = new Input(this);
		gameSettings = loadSettings();
		
		isRunning = true;
		
		incomingPackets = new ConcurrentSkipListSet<AppPacket>(new PacketComparator());
		
		logicThread = new LogicApp(this, getMaxUPS());
	}
	
	/**
	 * Initializes a window application
	 */
	public void run()
	{
		if(!glfwInit())
		{
			log.error("Failed to initialize application!");
			log.debug("create() method caused crash.");
			return;
		}
		
		Settings.staticInit();
		
		while(!Settings.hasMonitors())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				log.error("CRASH?");
			}
		}
		
		if(!create())
		{
			log.error("Failed to initialize application!");
			log.debug("setup() method caused crash.");
			return;
		}
		
		if(!gameSettings.createCapabilities())
		{
			log.error("OpenGL Creation Failed");
			if(gameSettings.getCapabilities() != null)
			{
				log.debug("The GL version is not to the needs of this application.");
				log.debug("Requires " + gameSettings.getGLVersion());
			}
			else
			{
				log.debug("There is no GL Capabilities being created.");
			}
			return;
		}
		
		init();
		
		soundSystem.init();
		
		gameSettings.init();
		
		logicThread.setLogicHandler(getStartGui());
		logicThread.run();
		
		OpenGL.glDefaults();
		
		double delta;
		Iterator<AppPacket> polledPackets;
		AppPacket packet;
		while(!glfwWindowShouldClose(windowInstance) && isRunning)
		{
			glfwMakeContextCurrent(windowInstance);
			
			delta = glfwGetTime();
			glfwSetTime(0);

			if(updateWindow())
				break;
			
			polledPackets = incomingPackets.iterator();
			while(polledPackets.hasNext())
			{
				packet = polledPackets.next();
				polledPackets.remove();
				packet.handle();
			}
			
			input.update();
			
			glClearBuffers();
			currentGui.render(delta);
			glfwSwapBuffers(windowInstance);
		}

		destroy();
	}
	
	protected Settings loadSettings()
	{
		return new Settings(this);
	}
	
	/**
	 * sets up the environment for a window to be created.
	 * @return true for success, false otherwise
	 */
	public boolean create()
	{
		long instance;
		
		String gameTitle = gameSettings.getTitle();
		
		Monitor monitor = gameSettings.getMonitor();
		long monitorInstance = monitor.getInstance();
		GLFWVidMode videoMode = monitor.getVideoMode();
		
		switch(gameSettings.getWindowMode())
		{
			case WINDOWED:
				instance = createWindow();
				break;
			case FULLSCREEN:
				glfwWindowHint(GLFW_RED_BITS, videoMode.redBits());
				glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits());
				glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits());
				glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate());
				instance = glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, windowInstance);
				break;
			default:
				instance = glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, windowInstance);
		}
		
		if(instance == 0)
		{
			glfwTerminate();
			return false;
		}
		
		if(windowInstance != -1)
			glfwDestroyWindow(windowInstance);
		
		windowInstance = instance;
		
		glfwMakeContextCurrent(windowInstance);
		
		input.init();
		
		glfwSwapInterval(1);
		updateSize();
		glfwShowWindow(windowInstance);
		
		return true;
	}
	
	public int getMaxUPS()
	{
		return 60;
	}
	
	private boolean updateWindow()
	{
		if(gameSettings.shouldWindowUpdate())
		{
			Map<String, ByteBuffer> textureData = getTextureData();
			if(!create())
				return true;
			loadTextureData(textureData);
			currentGui.reload();
		}
		return false;
	}
	
	/**
	 * @param packet
	 */
	public void sendPacket(AppPacket packet)
	{
		incomingPackets.add(packet);
	}
	
	public final void initGui(Gui newGui)
	{
		if(currentGui != null)
		{
			currentGui.close();
		}
		newGui.init();
		currentGui = newGui;
	}
	
	public final void reinitGui(Gui newGui)
	{
		currentGui.close();
		newGui.reinit();
		currentGui = newGui;
	}

	public void close()
	{
		isRunning = false;
		//OpenAL.closeAL();
	}

	protected abstract LogicGui getStartGui();
	
	/**
	 * initialize window
	 */
	protected void init() {}

	/**
	 * create the window
	 */
	public abstract long createWindow();
	
	public long createWindow(int width, int height, String title)
	{
		return createWindow(width, height, title, 0);
	}
	
	public long createWindow(int width, int height, String title, long parentInstance)
	{
		long instance = glfwCreateWindow(width, height, gameSettings.getTitle(), gameSettings.getMonitorInstance(), parentInstance);
		GLFWVidMode videoMode = glfwGetVideoMode(gameSettings.getMonitorInstance());
		glfwSetWindowPos(instance, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
		return instance;
	}
	
	public final long getWindow()
	{
		return windowInstance;
	}
	
	public final Gui getCurrentScreen()
	{
		return currentGui;
	}

	/**
	 * @return mouse position
	 */
	public final double getMouseX()
	{
		return input.getPos().x;
	}

	/**
	 * @return mouse position
	 */
	public final double getMouseY()
	{
		return input.getPos().y;
	}

	public final double getMouseDeltaX()
	{
		return input.getDelta().x;
	}

	public final double getMouseDeltaY()
	{
		return input.getDelta().y;
	}
	
	public final double getScrollDeltaX()
	{
		return input.getScrollDelta().x;
	}
	
	public final double getScrollDeltaY()
	{
		return input.getScrollDelta().y;
	}

}
