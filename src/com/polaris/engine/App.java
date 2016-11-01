package com.polaris.engine;

import static com.polaris.engine.options.Settings.getNextWindow;
import static com.polaris.engine.render.OpenGL.glClearBuffers;
import static com.polaris.engine.render.Texture.getTextureData;
import static com.polaris.engine.render.Texture.loadTextureData;
import static com.polaris.engine.render.Window.addModKey;
import static com.polaris.engine.render.Window.close;
import static com.polaris.engine.render.Window.create;
import static com.polaris.engine.render.Window.destroy;
import static com.polaris.engine.render.Window.getCurrentWindow;
import static com.polaris.engine.render.Window.getKey;
import static com.polaris.engine.render.Window.getModKeys;
import static com.polaris.engine.render.Window.getTimeAndReset;
import static com.polaris.engine.render.Window.notModKey;
import static com.polaris.engine.render.Window.pollEvents;
import static com.polaris.engine.render.Window.removeModKey;
import static com.polaris.engine.render.Window.setTime;
import static com.polaris.engine.render.Window.setupWindow;
import static com.polaris.engine.render.Window.shouldClose;
import static com.polaris.engine.render.Window.swapBuffers;
import static com.polaris.engine.render.Window.updateSize;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLCapabilities;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.options.Settings;
import com.polaris.engine.render.OpenGL;
import com.polaris.engine.render.Window;

public abstract class App 
{
	
	public static final Log log = LogFactory.getLog(App.class);
	
	/**
	 * long instance of the window this application takes on.
	 */
	private long windowInstance;
	
	/**
	 * Instance of application's sound system.
	 */
	private final OpenAL soundSystem;
	
	/**
	 * Instance of application's mouse.
	 */
	private final Mouse mouse;
	
	/**
	 * Instance of application's keyboard.
	 */
	private final Keyboard keyboard;
	
	/**
	 * The settings of the game, allows for inheritance.
	 */
	private Settings gameSettings;
	
	/**
	 * Instance of current screen being displayed.
	 */
	private Gui currentGui;

	private GLCapabilities glCapabilities;

	public App()
	{
		mouse = new Mouse(this);
		keyboard = new Keyboard(this); 
		soundSystem = new OpenAL(this);
	}
	
	/**
	 * Initializes a window application
	 */
	public void run()
	{
		gameSettings = loadSettings();
		
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
		
		if(!gameSettings.initCapabilities())
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
		
		OpenGL.glDefaults();
		
		glfwSetTime(0);
		while(shouldClose())
		{
			double delta = glfwGetTime();
			glfwSetTime(0);

			if(checkUpdateWindow())
				break;
			
			glfwPollEvents();
			
			handleKeyInput(delta);
			
			glClearBuffers();
			update(delta);
			render(delta);
			mouse.setDelta(0, 0);
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
		setWindowEvents();
		glfwMakeContextCurrent(windowInstance);
		glfwSwapInterval(1);
		updateSize();
		glfwShowWindow(windowInstance);
		return true;
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
	
	private void handleKeyInput(double delta)
	{
		for(Integer key : keyboardPress.keySet().toArray(new Integer[keyboardPress.size()]))
		{
			Vector2d vector = keyboardPress.get(key);
			
			if(getKey(key) == 1)
			{
				if((vector.x -= delta) <= .015)
				{
					vector.x = currentGui.keyHeld(key, (int)vector.y, getModKeys()) / 60d;
					vector.y++;
					if(vector.x <= 0)
					{
						if(notModKey(key))
							keyboardPress.remove(key);
					}
				}
			}
			else
			{
				if(!notModKey(key))
					removeModKey(key);
				currentGui.keyRelease(key, getModKeys());
				keyboardPress.remove(key);
			}
		}
	}

	/**
	 * @param newGui : the new gui the screen will adopt, if set to null then the application will close.
	 */
	public void setGui(Gui newGui)
	{
		if(newGui == null)
		{
			close();
			return;
		}
		if(currentGui != null)
		{
			currentGui.close();
		}
		newGui.init();
		currentGui = newGui;
	}

	protected boolean checkOpenGL()
	{
		return glCapabilities.OpenGL11;
	}

	/**
	 * when the mouse leaves the window, or enters
	 * @param entered : true if mouse enters window
	 */
	public void cursorMoveBounds(boolean entered) {}

	/**
	 * when the mouse moves in the window
	 * @param mouseX : new mouse x
	 * @param mouseY : new mouse y
	 */
	public void cursorMove(double mX, double mY) 
	{
		mouseDeltaX = (mX - mouseX) * Window.scaleWidth / Window.getWindowWidth();
		mouseDeltaY = (mY - mouseY) * Window.scaleHeight / Window.getWindowHeight();
		mouseX = mX * Window.scaleWidth / Window.getWindowWidth();
		mouseY = mY* Window.scaleHeight / Window.getWindowHeight();
	}

	/**
	 * when the mouse clicks
	 * <br><b>DON'T CALL super.cursorClick(button, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param button : the mouse button
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 */
	public void cursorClick(int button, int action) 
	{
		switch(action)
		{
		case 1:
			currentGui.mouseClick(button);
			break;
		case 2:
			currentGui.mouseHeld(button);
			break;
		case 0:
			currentGui.mouseRelease(button);
		}
	}

	/**
	 * when the mouse wheel scrolls
	 * <br><b>DON'T CALL super.cursorScroll(xOffset, yOffset) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param xOffset : mouse wheel offset x
	 * @param yOffset : mouse wheel offset y
	 */
	public void cursorScroll(double xOffset, double yOffset) 
	{
		currentGui.mouseScroll(xOffset, yOffset);
	}

	/**
	 * when the keyboard clicks
	 * <br><b>DON'T CALL super.keyboardClick(key, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param key : the key id
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 * @param mods 
	 */
	public void keyboardClick(int key, int action, int mods) 
	{
		switch(action)
		{
		case 1:
			double timer = currentGui.keyPressed(key, mods) / 60d;
			if(notModKey(key))
			{
				if(timer > 0)
				{
					keyboardPress.put(key, new Vector2d(timer, 0));
				}
			}
			else
			{
				addModKey(key);
				keyboardPress.put(key, new Vector2d(Math.max(0, timer), 0));
			}
			break;
		case 0:

			if(!keyboardPress.containsKey(key))
			{
				if(!notModKey(key))
				{
					removeModKey(key);
				}
				currentGui.keyRelease(key, mods);
			}
		}
	}

	/**
	 * when the window closes
	 */
	public void windowClose() 
	{
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

	/**
	 * Update method called every n times / second 
	 * <br><b>DON'T CALL super.update(delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void update(double delta) 
	{
		currentGui.update(delta);
	}

	/**
	 * Render method capped at n times / second
	 * <br><b>DON'T CALL super.render(delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void render(double delta) 
	{
		currentGui.render(delta);
	}

	/**
	 * initialize window
	 */
	protected abstract void init();

	/**
	 * create the window
	 */
	public abstract long createWindow();

	protected String getResourceLocation()
	{
		return "resources";
	}
	
	public Gui getCurrentScreen()
	{
		return currentGui;
	}

	/**
	 * @return mouse position
	 */
	public static double getMouseX()
	{
		return mouseX;
	}

	/**
	 * @return mouse position
	 */
	public static double getMouseY()
	{
		return mouseY;
	}

	public static double getMouseDeltaX()
	{
		return mouseDeltaX;
	}

	public static double getMouseDeltaY()
	{
		return mouseDeltaY;
	}

}
