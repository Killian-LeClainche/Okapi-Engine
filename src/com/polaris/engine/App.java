package com.polaris.engine;

import static com.polaris.engine.options.Settings.getNextWindow;
import static com.polaris.engine.render.OpenGL.glClearBuffers;
import static com.polaris.engine.render.Texture.getTextureData;
import static com.polaris.engine.render.Texture.loadTextureData;
import static com.polaris.engine.render.Window.destroy;
import static com.polaris.engine.render.Window.getCurrentWindow;
import static com.polaris.engine.render.Window.setupWindow;
import static com.polaris.engine.render.Window.updateSize;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLCapabilities;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.logic.LogicGui;
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

	private GLCapabilities glCapabilities;

	public App()
	{
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
		
		logicThread.setLogicHandler(getStartGui());
		logicThread.run();
		
		OpenGL.glDefaults();
		
		glfwSetTime(0);
		while(!glfwWindowShouldClose(windowInstance) && isRunning)
		{
			double delta = glfwGetTime();
			glfwSetTime(0);

			if(checkUpdateWindow())
				break;
			
			input.update();
			
			glClearBuffers();
			currentGui.render(delta);
			glfwSwapBuffers(windowInstance);
		}

		destroy();
	}
	
	protected Settings loadSettings()
	{
		return new Settings();
	}
	
	/**
	 * sets up the environment for a window to be created.
	 * @return true for success, false otherwise
	 */
	public boolean create()
	{
		long instance;
		if(getNextWindow() == 0)
		{
			instance = createWindow();
		}
		else if(getNextWindow() == 1)
		{
			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwWindowHint(GLFW_RED_BITS, mode.redBits());
			glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
			glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
			glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
			instance = glfwCreateWindow(mode.width(), mode.height(), "", glfwGetPrimaryMonitor(), windowInstance);
		}
		else
		{
			instance = glfwCreateWindow(1920, 1280, "", glfwGetPrimaryMonitor(), windowInstance);
		}
		currentFullscreen = getNextWindow();
		if(instance == 0)
		{
			glfwTerminate();
			return false;
		}
		if(windowInstance != -1)
			glfwDestroyWindow(windowInstance);
		windowInstance = instance;
		input.init();
		glfwMakeContextCurrent(windowInstance);
		glfwSwapInterval(1);
		updateSize();
		glfwShowWindow(windowInstance);
		return true;
	}
	
	public int getMaxUPS()
	{
		return 60;
	}
	
	private boolean checkUpdateWindow()
	{
		if(getNextWindow() != getCurrentWindow())
		{
			Map<String, ByteBuffer> textureData = getTextureData();
			if(setupWindow(this) == -1)
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

	/**
	 * when the window focus changes
	 * @param focused : if the window focuses
	 */
	public void windowFocus(boolean focused) {}

	/**
	 * when the window iconify changes
	 * @param iconified : if the window iconifies
	 */
	public void windowIconify(boolean iconified) {}

	/**
	 * when the windows position changes
	 * @param xPos : new pos x of window
	 * @param yPos : new pos y of window
	 */
	public void windowPos(int xPos, int yPos) {}

	/**
	 * when the window is refreshed
	 */
	public void windowRefresh() {}

	/**
	 * when the windows size changes
	 * @param width : new width
	 * @param height : new height
	 */
	public void windowSize(int width, int height) 
	{
		if(width + height != 0)
			updateSize();
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
	
	public final long getWindow()
	{
		return windowInstance;
	}

	protected String getResourceLocation()
	{
		return "resources";
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
