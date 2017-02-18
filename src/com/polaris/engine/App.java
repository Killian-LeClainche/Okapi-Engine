package com.polaris.engine;

import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
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
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.network.Packet;
import com.polaris.engine.options.Input;
import com.polaris.engine.options.Monitor;
import com.polaris.engine.options.Settings;
import com.polaris.engine.options.WindowMode;
import com.polaris.engine.render.Texture;
import com.polaris.engine.render.TextureManager;
import com.polaris.engine.sound.OpenAL;

public abstract class App<T extends Settings>
{
	
	private static boolean hasSetup = false;
	
	public static void start(final App<?> app)
	{
		setup();
		
		app.init();
		app.run();
	}
	
	public static void setup()
	{
		if(!hasSetup)
		{
			if(!glfwInit())
			{
				//LOG.error("Failed to initialize application!");
				//LOG.debug("create() method caused crash.");
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
					//LOG.error("CRASH?");
				}
			}
			hasSetup = true;
		}
	}
	
	public static int scaleToWidth = 1920;
	public static int scaleToHeight = 1080;
	
	/**
	 * long instance of the window this application takes on.
	 */
	protected long windowInstance;
	
	protected final Set<Packet> incomingPackets;
	
	protected final ExecutorService taskExecutor;
	
	/**
	 * Instance of application's sound system.
	 */
	protected final OpenAL soundSystem;
	
	/**
	 * Instance of application's mouse.
	 */
	protected final Input input;
	
	/**
	 * The settings of the game, allows for inheritance.
	 */
	protected T gameSettings;
	
	protected TextureManager textureManager;
	
	protected boolean isRunning;
	
	/**
	 * Instance of current screen being displayed.
	 */
	protected Gui<T> currentGui;
	
	private double delta;
	
	protected App(boolean debug)
	{
		//Configuration.DISABLE_CHECKS.set(!debug);
		//Configuration.DEBUG.set(debug);
		//Configuration.GLFW_CHECK_THREAD0.set(!debug);
		
		windowInstance = -1;

		gameSettings = loadSettings();
		
		soundSystem = new OpenAL(gameSettings);
		input = new Input(gameSettings);
		
		isRunning = true;
		
		incomingPackets = new ConcurrentSkipListSet<Packet>();
		
		textureManager = new TextureManager();
		
		taskExecutor = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 2, 1));
	}
	
	
	protected void init()
	{		
		gameSettings.init(input);
		
		if(!create())
		{
			//LOG.error("Failed to initialize application!");
			//LOG.debug("setup() method caused crash.");
			return;
		}
		
		soundSystem.init();
		
		glfwSetFramebufferSizeCallback(windowInstance, GLFWFramebufferSizeCallback.create((window, width, height) -> {
			gameSettings.setWindowWidth(width);
			gameSettings.setWindowHeight(height);
		}));
		
		GL.createCapabilities();
	}
	
	/**
	 * Initializes a window application
	 */
	public void run()
	{
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
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
		
		Iterator<Packet> polledPackets;
		Packet packet;
		Future<?> finalTask;
		
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
				//packet.handle();
			}
			
			input.update();
			
			currentGui.createTasks(taskExecutor);
			
			finalTask = taskExecutor.submit(currentGui);
			
			while(!finalTask.isDone());
			
			glClear(GL_ACCUM_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			
			currentGui.render(delta);
			glfwSwapBuffers(windowInstance);
		}
		destroy();
	}
	
	protected void destroy()
	{
		glfwDestroyWindow(windowInstance);
		glfwTerminate();
		
		taskExecutor.shutdown();
		while(!taskExecutor.isTerminated());
	}
	
	protected abstract T loadSettings();
	
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
			destroy();
			return false;
		}
		
		if(windowInstance != -1)
			glfwDestroyWindow(windowInstance);
		
		windowInstance = instance;
		
		glfwMakeContextCurrent(windowInstance);		
		
		input.init(windowInstance);
		
		glfwSwapInterval(gameSettings.vsyncMode());
		
		int[] width = new int[1];
		int[] height = new int[1];
		
		glfwGetFramebufferSize(windowInstance, width, height);

		gameSettings.setWindowWidth(width[0]);
		gameSettings.setWindowHeight(height[0]);
		
		glfwShowWindow(windowInstance);
		
		return true;
	}
	
	/**
	 * create the window
	 */
	public long createWindow()
	{
		return createWindow(gameSettings.getWindowWidth(), gameSettings.getWindowHeight());
	}
	
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
	public void handlePacket(Packet packet)
	{
		incomingPackets.add(packet);
	}
	
	/**
	 * @param newGui
	 */
	public void sendPacket(Packet packet)
	{
		//logicThread.handlePacket(packet);
	}
	
	public final void initGui(Gui<T> newGui)
	{
		if(currentGui != null)
		{
			currentGui.close();
		}
		newGui.init();
		currentGui = newGui;
	}
	
	public final void reinitGui(Gui<T> newGui)
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
	
	public long createWindow(int width, int height)
	{
		return createWindow(width, height, gameSettings.getTitle(), 0);
	}
	
	public long createWindow(int width, int height, String title, long parentInstance)
	{
		long instance = glfwCreateWindow(width, height, title, 0, parentInstance);
		glfwSetWindowPos(instance, gameSettings.getWindowXPos(width), gameSettings.getWindowYPos(height));
		return instance;
	}
	
	/**
	 * Call before performing 2d rendering
	 */
	public void gl2d()
	{
		glViewport(0, 0, gameSettings.getWindowWidth(), gameSettings.getWindowHeight());
		/*glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1, 1, 0, -10, 10);
		glMatrixMode(GL_MODELVIEW);*/
	}
	
	/**
	 * Call before performing 3d rendering
	 */
	public void gl3d(final float fovy, final float zNear, final float zFar)
	{
		int windowWidth = gameSettings.getWindowWidth();
		int windowHeight = gameSettings.getWindowHeight();
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
	
	public float getWindowScaleX()
	{
		return (float) scaleToWidth / (float) gameSettings.getWindowWidth();
	}
	
	public float getWindowScaleY()
	{
		return (float) scaleToHeight / (float) gameSettings.getWindowHeight();
	}
	
	public final long getWindow()
	{
		return windowInstance;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public T getSettings()
	{
		return gameSettings;
	}
	public TextureManager getTextureManager()
	{
		return textureManager;
	}
	
	public Gui<T> getCurrentScreen()
	{
		return currentGui;
	}
	
	public final double getTickDelta()
	{
		return delta;
	}
	
}
