package com.polaris.engine;

import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_ACCUM_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DITHER;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glClearStencil;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import com.polaris.engine.options.Monitor;
import com.polaris.engine.options.Settings;
import com.polaris.engine.options.WindowMode;
import com.polaris.engine.render.Texture;
import com.polaris.engine.render.TextureManager;
import com.polaris.engine.sound.OpenAL;
import com.polaris.engine.thread.AppPacket;
import com.polaris.engine.thread.LogicApp;
import com.polaris.engine.thread.PacketComparator;

public abstract class App 
{
	
	public static final Log log = LogFactory.getLog(App.class);
	
	public int scaleToWidth = 1920;
	public int scaleToHeight = 1080;
	
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
	
	private int windowWidth = 0;
	private int windowHeight = 0;
	
	private TextureManager textureManager;
	
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
		
		windowInstance = -1;
		
		soundSystem = new OpenAL(this);
		input = new Input(this);
		gameSettings = loadSettings();
		
		isRunning = true;
		
		incomingPackets = new ConcurrentSkipListSet<AppPacket>(new PacketComparator());
		
		logicThread = new LogicApp(this, getMaxUPS());
		
		textureManager = new TextureManager(this);
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
		
		gameSettings.init();
		
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
		logicThread.start();
		
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glClearStencil(0);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, .05f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glDisable(GL_DITHER);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
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
			
			glClear(GL_ACCUM_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			currentGui.render(delta);
			glfwSwapBuffers(windowInstance);
		}
		
		logicThread.close();

		glfwDestroyWindow(windowInstance);
		GL.destroy();
		glfwTerminate();
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
		
		WindowMode mode = gameSettings.getWindowMode();
		
		if(mode == WindowMode.WINDOWED)
		{
			instance = createWindow();
		}
		else if(mode == WindowMode.FULLSCREEN)
		{
			glfwWindowHint(GLFW_RED_BITS, videoMode.redBits());
			glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits());
			glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits());
			glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate());
			instance = glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, windowInstance);
		}
		else
		{
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
		
		glfwSwapInterval(gameSettings.vsyncMode());
		
		int[] width = new int[1];
		int[] height = new int[1];
		
		glfwGetFramebufferSize(windowInstance, width, height);
		
		windowWidth = width[0];
		windowHeight = height[0];
		
		glfwShowWindow(windowInstance);
		
		return true;
	}
	
	/**
	 * initialize window
	 */
	protected void init() 
	{
		glfwSetFramebufferSizeCallback(windowInstance, GLFWFramebufferSizeCallback.create((window, width, height) -> {
			windowWidth = width;
			windowHeight = height;
		}));
	}

	/**
	 * create the window
	 */
	public abstract long createWindow();
	
	public int getMaxUPS()
	{
		return 60;
	}
	
	private boolean updateWindow()
	{
		if(gameSettings.shouldWindowUpdate())
		{
			Collection<Texture> textureData = textureManager.getTextures();
			
			textureManager.clear();
			
			if(!create())
				return true;
			
			textureManager.setTextures(textureData);
			
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
	
	public long createWindow(int width, int height)
	{
		return createWindow(width, height, gameSettings.getTitle(), 0);
	}
	
	public long createWindow(int width, int height, String title, long parentInstance)
	{
		long monitor = gameSettings.getMonitorInstance();
		long instance = glfwCreateWindow(width, height, title, 0, parentInstance);
		GLFWVidMode videoMode = glfwGetVideoMode(monitor);
		glfwSetWindowPos(instance, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
		return instance;
	}
	
	/**
	 * Call before performing 2d rendering
	 */
	public void gl2d()
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, scaleToWidth, scaleToHeight, 0, -100, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * Call before performing 3d rendering
	 */
	public void gl3d(final float fovy, final float zNear, final float zFar)
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		double ymax = zNear * Math.tan( fovy * Math.PI / 360.0 );
		double ymin = -ymax;
		double xmin = ymin * windowWidth / windowHeight;
		double xmax = ymax * windowWidth / windowHeight;
		glFrustum( xmin, xmax, ymin, ymax, zNear, zFar );
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
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
	
	public LogicApp getLogicHandler()
	{
		return logicThread;
	}
	
	public Input getInput()
	{
		return input;
	}

	public TextureManager getTextureManager()
	{
		return textureManager;
	}

}
