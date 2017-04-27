/**
 *
 */
package com.polaris.engine.options;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.polaris.engine.App;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Killian Le Clainche
 */
public class Input
{
	
	private final Settings gameSettings;
	private Vector2d position;
	private Vector2d delta;
	private Vector2d scrollDelta;
	private StringBuilder textInput;
	
	public Input(Settings settings)
	{
		gameSettings = settings;
		
		position = new Vector2d(0);
		delta = new Vector2d(0);
		scrollDelta = new Vector2d(0);
		
		textInput = new StringBuilder();
		
	}
	
	public void init(long windowInstance)
	{
		glfwSetCursorPosCallback(windowInstance, GLFWCursorPosCallback.create((window, xpos, ypos) ->
		{
			xpos /= (double) gameSettings.getWindowWidth();
			ypos /= (double) gameSettings.getWindowHeight();
			xpos *= App.scaleToWidth;
			ypos *= App.scaleToHeight;
			setDelta(position.x - xpos, position.y - ypos);
			position.x = xpos;
			position.y = ypos;
		}));
		
		glfwSetMouseButtonCallback(windowInstance, GLFWMouseButtonCallback.create((window, button, action, mods) ->
		{
			Key mouseKey = gameSettings.getMouseKey(button);
			
			if (mouseKey == null) return;
			
			if (action == GLFW.GLFW_PRESS)
			{
				mouseKey.press();
			}
			else if (action == GLFW.GLFW_RELEASE)
			{
				mouseKey.release();
			}
		}));
		
		glfwSetScrollCallback(windowInstance, GLFWScrollCallback.create((window, xoffset, yoffset) ->
		{
			addScrollDelta(xoffset, yoffset);
		}));
		
		glfwSetKeyCallback(windowInstance, GLFWKeyCallback.create((window, key, scancode, action, mods) ->
		{
			if (key != -1)
			{
				Key keyboardKey = gameSettings.getKey(key);
				
				if (keyboardKey == null) return;
				
				if (action == GLFW.GLFW_PRESS)
				{
					keyboardKey.press();
				}
				else if (action == GLFW.GLFW_RELEASE)
				{
					keyboardKey.release();
				}
			}
		}));
		
		glfwSetCharCallback(windowInstance, GLFWCharCallback.create((window, codepoint) ->
		{
			textInput.append((char) codepoint);
		}));
	}
	
	private final void setDelta(double dx, double dy)
	{
		delta.set(dx, dy);
	}
	
	private final void addScrollDelta(double dx, double dy)
	{
		scrollDelta.add(dx, dy);
	}
	
	public void update(double delta)
	{
		textInput.setLength(0);
		setDelta(0, 0);
		setScrollDelta(0, 0);
		
		gameSettings.update(delta);
		
		glfwPollEvents();
	}
	
	private final void setScrollDelta(double dx, double dy)
	{
		scrollDelta.set(dx, dy);
	}
	
	public void setCursorMode(long windowInstance, int mode)
	{
		glfwSetInputMode(windowInstance, GLFW_CURSOR, mode);
	}
	
	public final double getMouseX()
	{
		return position.x;
	}
	
	public final double getMouseY()
	{
		return position.y;
	}
	
	public final double getMouseXDelta()
	{
		return delta.x;
	}
	
	public final double getMouseYDelta()
	{
		return delta.y;
	}
	
	public final double getMouseScrollX()
	{
		return scrollDelta.x;
	}
	
	public final double getMouseScrollY()
	{
		return scrollDelta.y;
	}
	
	public final String getInputText()
	{
		return textInput.toString();
	}
	
}