package com.polaris.engine.render;

import static com.polaris.engine.options.Settings.getNextWindow;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import com.polaris.engine.App;

public class Window 
{
	/**
	 * Instance version of the application's window instance
	 */
	private static long windowInstance = -1;
	private static App window = null;
	private static int currentFullscreen = 0;
	private static boolean isRunning = true;

	private static int windowWidth = 0;
	private static int windowHeight = 0;
	public static final int scaleWidth = 1920;
	public static final int scaleHeight = 1080;

	private static final List<Integer> modKeys = Arrays.asList(GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT,
			GLFW.GLFW_KEY_LEFT_SUPER, GLFW.GLFW_KEY_RIGHT_SUPER);
	private static final Map<Integer, Integer> shiftKeys = new HashMap<Integer, Integer>();
	private static int currentModKeys = 0;
	
	static
	{
		shiftKeys.put((int)'0', (int)')');
		shiftKeys.put((int)'1', (int)'!');
		shiftKeys.put((int)'2', (int)'@');
		shiftKeys.put((int)'3', (int)'#');
		shiftKeys.put((int)'4', (int)'$');
		shiftKeys.put((int)'5', (int)'%');
		shiftKeys.put((int)'6', (int)'^');
		shiftKeys.put((int)'7', (int)'&');
		shiftKeys.put((int)'8', (int)'*');
		shiftKeys.put((int)'9', (int)'(');
		shiftKeys.put((int)'`', (int)'~');
		shiftKeys.put((int)'-', (int)'_');
		shiftKeys.put((int)'=', (int)'+');
		shiftKeys.put((int)'[', (int)'{');
		shiftKeys.put((int)']', (int)'}');
		shiftKeys.put((int)';', (int)':');
		shiftKeys.put((int)'\'', (int)'"');
		shiftKeys.put((int)',', (int)'<');
		shiftKeys.put((int)'.', (int)'>');
		shiftKeys.put((int)'/', (int)'?');
		shiftKeys.put((int)'\\', (int)'|');
	}

	public static boolean create()
	{
		return !glfwInit();
	}

	/**
	 * sets up the environment for a window to be created.
	 * @return true for success, false otherwise
	 */
	public static long setupWindow(App app)
	{
		window = app;
		long instance;
		if(getNextWindow() == 0)
		{
			instance = window.createWindow();
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
			return -1;
		}
		if(windowInstance != -1)
			glfwDestroyWindow(windowInstance);
		windowInstance = instance;
		setWindowEvents();
		glfwMakeContextCurrent(windowInstance);
		glfwSwapInterval(1);
		updateSize();
		glfwShowWindow(windowInstance);
		return instance;
	}

	/**
	 * creates the window events that handle input and window changes
	 */
	private static void setWindowEvents()
	{
		glfwSetCursorEnterCallback(windowInstance, cursorBounds);
		glfwSetCursorPosCallback(windowInstance, cursorPos);
		glfwSetMouseButtonCallback(windowInstance, cursorButton);
		glfwSetScrollCallback(windowInstance, cursorScroll);
		glfwSetKeyCallback(windowInstance, keyboard);
		glfwSetWindowCloseCallback(windowInstance, windowClose);
		glfwSetWindowFocusCallback(windowInstance, windowFocus);
		glfwSetWindowIconifyCallback(windowInstance, windowIconify);
		glfwSetWindowPosCallback(windowInstance, windowPos);
		glfwSetWindowRefreshCallback(windowInstance, windowRefresh);
		glfwSetWindowSizeCallback(windowInstance, windowSize);
	}

	public static boolean shouldClose()
	{
		return !glfwWindowShouldClose(windowInstance) && isRunning;
	}

	public static void pollEvents()
	{
		glfwPollEvents();
	}

	public static void swapBuffers()
	{
		glfwSwapBuffers(windowInstance);
	}

	/**
	 * Called to update the windows size
	 * @param windowInstance
	 */
	public static void updateSize() 
	{
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(windowInstance, width, height);
		windowWidth = width.get();
		windowHeight = height.get();
	}

	/**
	 * Call before performing 2d rendering
	 */
	public static void gl2d()
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1920, 1080, 0, -100, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * Call before performing 3d rendering
	 */
	public static void gl3d(final float fovy, final float zNear, final float zFar)
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

	/**
	 * close the application
	 */
	public static void close()
	{
		isRunning = false;
	}

	public static void destroy()
	{
		glfwDestroyWindow(windowInstance);
		GL.destroy();
		glfwTerminate();
	}

	public static double getTime()
	{
		return glfwGetTime();
	}

	public static void setTime(double time)
	{
		glfwSetTime(time);
	}

	public static double getTimeAndReset()
	{
		double delta = glfwGetTime();
		setTime(0);
		return delta;
	}

	public static int getModKeys()
	{
		return currentModKeys;
	}

	public static boolean notModKey(int key)
	{
		return !modKeys.contains(key);
	}
	
	public static void addModKey(int key)
	{
		for(int i = 0; i < modKeys.size(); i += 2)
		{
			if(key == modKeys.get(i) || key == modKeys.get(i + 1))
			{
				currentModKeys += (int) (Math.pow(2, i / 2));
				break;
			}
		}
	}
	
	public static void removeModKey(int key)
	{
		for(int i = 0; i < modKeys.size(); i += 2)
		{
			if(key == modKeys.get(i) || key == modKeys.get(i + 1))
			{
				currentModKeys &= ~(int) (Math.pow(2, i / 2));
				break;
			}
		}
	}

	public static int getWindowWidth()
	{
		return windowWidth;
	}

	public static int getWindowHeight()
	{
		return windowHeight;
	}

	/**
	 * Set hints to window and others mostly not used with this method
	 * @param target <br><b>WINDOW</b>
	 * <br> - <b>GLFW_FOCUSED</b> : window is focused (D TRUE)
	 * <br> - <b>GLFW_ICONIFIED</b> : window is minimized (D FALSE)
	 * <br> - <b>GLFW_VISIBLE</b> : window is visible (D TRUE)
	 * <br> - <b>GLFW_RESIZEABLE</b> : window is resizeable (D TRUE)
	 * <br> - <b>GLFW_DECORATED</b> : window has borders (D TRUE)
	 * <br> - <b>GLFW_FLOATING</b> : window is always-on-top (D FALSE)
	 * @param value : true or false
	 */
	public static void setHint(int target, boolean value)
	{
		glfwWindowHint(target, value ? 1 : 0);
	}
	
	public static void setHint(int target, int value)
	{
		glfwWindowHint(target, value);
	}

	/**
	 * Centers the window and returns its instance long variable
	 * @param width : window width
	 * @param height : window height
	 * @param title : window title
	 * @param monitor : window monitor
	 * @param share : window share
	 * @return
	 */
	public static long createAndCenter(int width, int height, String title, int monitor)
	{
		long instance = glfwCreateWindow(width, height, title, monitor, windowInstance == -1 ? 0 : windowInstance);
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(instance, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
		return instance;
	}

	public static int getKey(int key)
	{
		return glfwGetKey(windowInstance, key);
	}
	
	public static int getShiftKey(int key)
	{
		return shiftKeys.containsKey(key) ? shiftKeys.get(key) : key;
	}

	/**
	 * @return windows instance
	 */
	public static long getWindowInstance()
	{
		return windowInstance;
	}

	public static int getCurrentWindow()
	{
		return currentFullscreen;
	}

	private static GLFWCursorEnterCallback cursorBounds = new GLFWCursorEnterCallback () {

		public void invoke(long window, boolean entered) 
		{
			Window.window.cursorMoveBounds(entered);
		}
	};
	private static GLFWCursorPosCallback cursorPos = new GLFWCursorPosCallback () {

		public void invoke(long window, double xpos, double ypos) 
		{
			Window.window.cursorMove(xpos, ypos);
		}
	};
	private static GLFWMouseButtonCallback cursorButton = new GLFWMouseButtonCallback () {

		public void invoke(long window, int button, int action, int mods)
		{
			Window.window.cursorClick(button, action);
		}
	};
	private static GLFWScrollCallback cursorScroll = new GLFWScrollCallback () {

		public void invoke(long window, double xoffset, double yoffset)
		{
			Window.window.cursorScroll(xoffset, yoffset);
		}
	};
	private static GLFWKeyCallback keyboard = new GLFWKeyCallback () {

		public void invoke(long window, int key, int scancode, int action, int mods)
		{
			Window.window.keyboardClick(key, action, mods);
		}
	};
	private static GLFWWindowCloseCallback windowClose = new GLFWWindowCloseCallback () {

		public void invoke(long window) 
		{
			Window.window.windowClose();
		}
	};
	private static GLFWWindowFocusCallback windowFocus = new GLFWWindowFocusCallback () {

		public void invoke(long window, boolean focused)
		{
			Window.window.windowFocus(focused);
		}
	};
	private static GLFWWindowIconifyCallback windowIconify = new GLFWWindowIconifyCallback () {

		public void invoke(long window, boolean iconified) 
		{
			Window.window.windowIconify(iconified);
		}
	};
	private static GLFWWindowPosCallback windowPos = new GLFWWindowPosCallback () {

		public void invoke(long window, int xpos, int ypos)
		{
			Window.window.windowPos(xpos, ypos);
		}
	};
	private static GLFWWindowRefreshCallback windowRefresh = new GLFWWindowRefreshCallback () {

		public void invoke(long window) 
		{
			Window.window.windowRefresh();
		}
	};
	private static GLFWWindowSizeCallback windowSize = new GLFWWindowSizeCallback () {

		public void invoke(long window, int width, int height)
		{
			Window.window.windowSize(width, height);
		}
	};

}
