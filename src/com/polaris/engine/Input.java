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
		
		position = new Vector2d(0);
		delta = new Vector2d(0);
		scrollDelta = new Vector2d(0);
		
		keyboardMapping = new DualHashBidiMap<Integer, Key>();
		mouseMapping = new DualHashBidiMap<Integer, Key>();
		nameMapping = new DualHashBidiMap<String, Key>();
		textInput = new String();
		
		addKey(GLFW.GLFW_KEY_APOSTROPHE);
		addKey(GLFW.GLFW_KEY_TAB);
		addKey(GLFW.GLFW_KEY_LEFT_SHIFT);
		addKey(GLFW.GLFW_KEY_LEFT);
		addKey(GLFW.GLFW_KEY_LEFT_ALT);
		addKey(GLFW.GLFW_KEY_LEFT_BRACKET);
		addKey(GLFW.GLFW_KEY_LEFT_CONTROL);
		addKey(GLFW.GLFW_KEY_LEFT_SHIFT);
		addKey(GLFW.GLFW_KEY_SPACE);
		addKey(GLFW.GLFW_KEY_DELETE);
		addKey(GLFW.GLFW_KEY_RIGHT);
		addKey(GLFW.GLFW_KEY_RIGHT_ALT);
		addKey(GLFW.GLFW_KEY_RIGHT_BRACKET);
		addKey(GLFW.GLFW_KEY_RIGHT_CONTROL);
		addKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
		addKey(GLFW.GLFW_KEY_UP);
		addKey(GLFW.GLFW_KEY_DOWN);
		addKey(GLFW.GLFW_KEY_COMMA);
		addKey(GLFW.GLFW_KEY_PERIOD);
		addKey(GLFW.GLFW_KEY_SLASH);
		addKey(GLFW.GLFW_KEY_BACKSLASH);
		addKey(GLFW.GLFW_KEY_BACKSLASH);
		addKey(GLFW.GLFW_KEY_EQUAL);
		addKey(GLFW.GLFW_KEY_ESCAPE);
		addKey(GLFW.GLFW_KEY_GRAVE_ACCENT);
		addKey(GLFW.GLFW_KEY_MINUS);
		addKey(GLFW.GLFW_KEY_ENTER);
		
		for(int i = GLFW.GLFW_KEY_0; i <= GLFW.GLFW_KEY_9; i++)
		{
			addKey(i);
		}
		
		for(int i = GLFW.GLFW_KEY_A; i <= GLFW.GLFW_KEY_Z; i++)
		{
			addKey(i);
		}
		
		for(int i = 0; i <= 7; i++)
		{
			Key key = new Key("MOUSE" + i, i);
			mouseMapping.put(i, key);
			nameMapping.put(key.getName(), key);
		}
	}
	
	private void addKey(int i)
	{
		Key key = new Key(GLFW.glfwGetKeyName(i, 0), i);
		keyboardMapping.put(i, key);
		nameMapping.put(key.getName(), key);
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
			
			if(mouseKey == null)
				return;
			
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
				
				if(keyboardKey == null)
					return;
				
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
	
	public Key getKey(int keyCode)
	{
		return keyboardMapping.get(keyCode);
	}
	
}
