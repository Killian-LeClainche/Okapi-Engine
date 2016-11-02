/**
 * 
 */
package com.polaris.engine;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.*;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.polaris.engine.options.Key;

/**
 * @author Killian Le Clainche
 *
 */
public class Input
{
	
	private final App application;
	private Vector2d position;
	private Vector2d delta;
	private Vector2d scrollDelta;
	
	private final BidiMap<Integer, Key> keyboardMapping;
	private final BidiMap<Integer, Key> mouseMapping;
	private final BidiMap<String, Key> nameMapping;
	
	private String textInput;
	
	public Input(App app)
	{
		application = app;
		keyboardMapping = new DualHashBidiMap<Integer, Key>();
		mouseMapping = new DualHashBidiMap<Integer, Key>();
		nameMapping = new DualHashBidiMap<String, Key>();
		textInput = new String();
	}
	
	public void init()
	{
		glfwSetCursorPosCallback(application.getWindow(), GLFWCursorPosCallback.create((window, xpos, ypos) -> {
			setDelta(position.x - xpos, position.y - ypos);
			position.x = xpos;
			position.y = ypos;
		}));
		
		glfwSetMouseButtonCallback(application.getWindow(), GLFWMouseButtonCallback.create((window, button, action, mods) -> {
			Key mouseKey = mouseMapping.get(button);
			if(action == GLFW.GLFW_PRESS)
			{
				mouseKey.press();
			}
			else if(action == GLFW.GLFW_RELEASE)
			{
				mouseKey.release();
			}
		}));
		
		glfwSetScrollCallback(application.getWindow(), GLFWScrollCallback.create((window, xoffset, yoffset) ->{
			addScrollDelta(xoffset, yoffset);
		}));
		
		glfwSetKeyCallback(application.getWindow(), GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
			if(key != -1)
			{
				Key keyboardKey = keyboardMapping.get(key);
				if(action == GLFW.GLFW_PRESS)
				{
					keyboardKey.press();
				}
				else if(action == GLFW.GLFW_RELEASE)
				{
					keyboardKey.release();
				}
			}
		}));
		
		glfwSetCharCallback(application.getWindow(), GLFWCharCallback.create((window, codepoint) -> {
			textInput += (char) codepoint;
		}));
	}

	public void update()
	{
		textInput = new String();
		setDelta(0, 0);
		setScrollDelta(0, 0);
		
		glfwPollEvents();
		
		for(Key key : mouseMapping.values())
		{
			key.update();
		}
		
		for(Key key : keyboardMapping.values())
		{
			key.update();
		}
	}
	
	public void setCursorMode(int mode)
	{
		glfwSetInputMode(application.getWindow(), GLFW_CURSOR, mode);
	}
	
	public Vector2d getPos()
	{
		return position;
	}
	
	public Vector2d getDelta()
	{
		return delta;
	}
	
	public Vector2d getScrollDelta()
	{
		return scrollDelta;
	}
	
	public void setPos(double x, double y)
	{
		position.set(x, y);
	}
	
	public void setDelta(double dx, double dy)
	{
		delta.set(dx, dy);
	}
	
	public void setScrollDelta(double dx, double dy)
	{
		scrollDelta.set(dx, dy);
	}
	
	public void addScrollDelta(double dx, double dy)
	{
		scrollDelta.add(dx, dy);
	}
	
}
