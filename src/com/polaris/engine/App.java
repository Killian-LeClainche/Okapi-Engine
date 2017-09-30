package com.polaris.engine;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.network.Packet;
import com.polaris.engine.options.Input;
import com.polaris.engine.options.Monitor;
import com.polaris.engine.options.Settings;
import com.polaris.engine.options.WindowMode;
import com.polaris.engine.render.Texture;
import com.polaris.engine.render.TextureManager;
import com.polaris.engine.sound.OpenAL;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import java.util.*;
import java.util.concurrent.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Killian Le Clainche on April 23, 2017.
 * The base file which all applications should extend to provide functionality.
 *
 * @param <T> The Settings Class which should be used for all of the system.
 */
public abstract class App<T extends Settings>
{
	
	/**
	 * The dimensions that all rendering should be bound to, it will automatically scale things properly.
	 */
	public static int scaleToWidth = 1920, scaleToHeight = 1080;
	/**
	 * In case there are two windows created in one Java run, this prevents reinitializing everything.
	 */
	private static boolean hasSetup = false;
	/**
	 * Static counter meant to determine if glfw should be terminated.
	 */
	private static int applicationCount = 0;
	
	/**
	 * The static method to call from your static void main function (or where ever, whenever) that will create
	 * the application for you.
	 *
	 * @param app The application object to initialize and run.
	 */
	public static void start(final App<?> app)
	{
		setup();
		
		app.init();
		try
		{
			app.run();
		}
		catch (ExecutionException | InterruptedException e)
		{
			e.printStackTrace();
		}
		
		applicationCount ++;
	}
	
	/**
	 * Private method for guarenteeing that the proper things have been initialized. I keep it private so that the
	 * autocomplete isn't cluttered with non called methods.
	 */
	private static void setup()
	{
		if (!hasSetup)
		{
			if (!glfwInit())
			{
				//LOG.error("Failed to initialize application!");
				//LOG.debug("create() method caused crash.");
				return;
			}
			
			Settings.staticInit();
			
			while (!Settings.hasMonitors())
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
	
	/**
	 * I'm trying to get the server -> client, client -> server network properly implemented. Currently,
	 * I have very little idea on how I'll be doing it and minimizing overhead and maximizing potential.
	 */
	protected final Set<Packet> incomingPackets;
	/**
	 * Task system is the current proper implementation for multi-threading. I would like to maximize the potential of
	 * the products I decide to make with this.
	 */
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
	 * long instance of the window this application takes on.
	 */
	protected long windowInstance;
	/**
	 * The settings of the game, allows for inheritance.
	 */
	protected T gameSettings;
	/**
	 * Handles all textures involved in the applications environment. I have to overhaul it again to try and find
	 * that perfect balance that I feel comfortable with.
	 */
	protected TextureManager textureManager;
	/**
	 * The protected field that determines if this application should continue to run.
	 */
	protected boolean isRunning;
	/**
	 * Instance of current screen being displayed.
	 */
	protected Gui<T> currentGui;
	/**
	 * The change of time since the previous delta setup call
	 * (which is basically glfwGetTime(); glfwSetTime(0);)
	 */
	private double delta;
	
	/**
	 * Protected constructor of the Application.
	 * I do this because I don't want people to see this constructor.
	 *
	 * @param debug     Called from children constructors to determine whether the application should be debugging or
	 *                  not in the console.
	 */
	protected App(boolean debug)
	{
		// Set up debugging if necessary
		Configuration.DISABLE_CHECKS.set(!debug);
		Configuration.DEBUG.set(debug);
		Configuration.GLFW_CHECK_THREAD0.set(!debug);
		
		windowInstance = -1;
		
		gameSettings = loadSettings();
		
		soundSystem = new OpenAL(gameSettings);
		input = new Input(gameSettings);
		
		isRunning = true;
		
		incomingPackets = new ConcurrentSkipListSet<>();
		
		textureManager = new TextureManager();
		
		taskExecutor = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
		
		delta = 0;
	}
	
	protected abstract T loadSettings();
	
	protected void init()
	{
		gameSettings.init(input);
		
		if (!create())
		{
			//LOG.error("Failed to initialize application!");
			//LOG.debug("setup() method caused crash.");
			return;
		}
		
		soundSystem.init();
		
		glfwSetFramebufferSizeCallback(windowInstance, GLFWFramebufferSizeCallback.create((window, width, height) ->
		{
			gameSettings.setWindowWidth(width);
			gameSettings.setWindowHeight(height);
		}));
		
		GL.createCapabilities();
	}
	
	/**
	 * Initializes a window application
	 */
	public void run() throws ExecutionException, InterruptedException
	{
		setupGL();
		
		Iterator<Packet> polledPackets;
		Packet packet;
		
		List<Runnable> runnableList = new LinkedList<>();
		List<Future> futureList = new LinkedList<>();
		
		glfwSetTime(0);
		
		while (!glfwWindowShouldClose(windowInstance) && isRunning)
		{
			glfwMakeContextCurrent(windowInstance);
			
			delta = glfwGetTime();
			glfwSetTime(0);
			
			if (updateWindow()) break;
			
			polledPackets = incomingPackets.iterator();
			while (polledPackets.hasNext())
			{
				packet = polledPackets.next();
				polledPackets.remove();
				packet.handle(null);
			}
			
			input.update(delta);
			
			currentGui.createTasks(runnableList);
			
			for (Runnable r : runnableList)
			{
				futureList.add(taskExecutor.submit(r));
			}
			
			runnableList.clear();
			
			futureList.add(taskExecutor.submit(currentGui));
			
			for (Future f : futureList)
			{
				f.get();
			}
			
			glClear(GL_ACCUM_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			
			currentGui.render(delta);
			glfwSwapBuffers(windowInstance);
		}
		destroy();
	}
	
	protected void setupGL()
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
	}
	
	protected void destroy()
	{
		applicationCount --;
		
		soundSystem.close();
		
		glfwDestroyWindow(windowInstance);
		
		if(applicationCount == 0)
			glfwTerminate();
		
		taskExecutor.shutdown();
		
		try
		{
			taskExecutor.awaitTermination(1, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * sets up the environment for a window to be created.
	 *
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
		
		if (mode == WindowMode.WINDOWED)
		{
			instance = createWindowed();
		}
		else if (mode == WindowMode.FULLSCREEN)
		{
			instance = createFullscreen(videoMode, gameTitle, monitorInstance);
		}
		else
		{
			instance = createBorderless(videoMode, gameTitle, monitorInstance);
		}
		
		if (instance == 0)
		{
			destroy();
			return false;
		}
		
		if (windowInstance != -1)
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
	 * Creates the window in windowed mode.
	 */
	public long createWindowed()
	{
		return createWindow(gameSettings.getWindowWidth(), gameSettings.getWindowHeight());
	}
	
	/**
	 * Creates the window in fullscreen mode.
	 */
	public long createFullscreen(GLFWVidMode videoMode, String gameTitle, long monitorInstance)
	{
		glfwWindowHint(GLFW_RED_BITS, videoMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate());
		return glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, windowInstance);
	}
	
	/**
	 * Creates the window in borderless mode.
	 */
	public long createBorderless(GLFWVidMode videoMode, String gameTitle, long monitorInstance)
	{
		return glfwCreateWindow(videoMode.width(), videoMode.height(), gameTitle, monitorInstance, windowInstance);
		
	}
	
	private boolean updateWindow()
	{
		if (gameSettings.shouldWindowUpdate())
		{
			Collection<Texture> textureData = textureManager.getTextures();
			
			textureManager.clear();
			
			if (!create())
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
	
	public final void initGui(Gui<T> newGui)
	{
		if (currentGui != null)
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
	}
	
	/**
	 * Call before performing 3d rendering
	 */
	public void gl3d(final float fieldOfViewY, final float zNear, final float zFar)
	{
		int windowWidth = gameSettings.getWindowWidth();
		int windowHeight = gameSettings.getWindowHeight();
		
		double maxY = zNear * Math.tan(fieldOfViewY * Math.PI / 360.0);
		double minY = -maxY;
		double maxX = maxY * windowWidth / windowHeight;
		double minX = minY * windowWidth / windowHeight;
		
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glFrustum(minX, maxX, minY, maxY, zNear, zFar);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	public int getMaxUPS()
	{
		return 60;
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
