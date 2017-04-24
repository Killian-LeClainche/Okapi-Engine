package com.polaris.engine.gui;

import com.polaris.engine.App;
import com.polaris.engine.gui.content.GuiContent;
import com.polaris.engine.options.Settings;
import com.polaris.engine.util.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class GuiScreen<T extends Settings> extends Gui<T>
{
	
	protected GuiContent<T> focusedElement;
	private List<GuiContent<T>> elementList;
	private int elementCounter = 0;
	
	public GuiScreen(App<T> app)
	{
		super(app);
		
		elementList = new ArrayList<GuiContent<T>>();
	}
	
	public GuiScreen(GuiScreen<T> gui)
	{
		super(gui.application, gui, 0);
		
		elementList = new ArrayList<GuiContent<T>>();
	}
	
	public void update(double delta)
	{
		super.update(delta);
		
		double mouseX = input.getMouseX();
		double mouseY = input.getMouseY();
		
		if (input.getKey(GLFW_KEY_ESCAPE).isPressed())
		{
			if (input.getKey(GLFW.GLFW_KEY_LEFT_SHIFT).isPressed())
			{
				gameSettings.changeWindowMode();
			}
			else
			{
				if (this.getParent() != null)
				{
					application.reinitGui(this.getParent());
				}
				else
				{
					application.close();
				}
			}
		}
		
		for (GuiContent<T> element : elementList)
		{
			if (MathHelper.inBounds(mouseX, mouseY, element.getBounds()))
			{
				//flag = element.handleInput();
			}
			element.update(delta);
		}
	}
	
	public void render(double delta)
	{
		super.render(delta);
		for (GuiContent<T> element : elementList)
		{
			element.render(delta);
		}
	}
	
	public void close()
	{
		this.focusedElement = null;
	}
	
	public boolean mouseClick(int mouseId)
	{
		for (GuiContent<T> element : elementList)
		{
			if (MathHelper.inBounds(input.getMouseX(), input.getMouseY(), element.getBounds()))
			{
				boolean flag = element.nMouseClick(mouseId);
				if (flag && element != focusedElement)
				{
					unbindCurrentElement(element);
				}
				return flag;
			}
		}
		unbindCurrentElement();
		return false;
	}
	
	public void unbindCurrentElement(GuiContent<T> e)
	{
		unbindCurrentElement();
		focusedElement = e;
	}
	
	public void unbindCurrentElement()
	{
		if (focusedElement != null)
		{
			focusedElement.unbind();
			focusedElement = null;
		}
	}
	
	public void mouseHeld(int mouseId)
	{
		if (focusedElement != null && focusedElement.nMouseHeld(mouseId))
		{
			unbindCurrentElement();
		}
	}
	
	public void mouseRelease(int mouseId)
	{
		if (focusedElement != null && !focusedElement.nMouseRelease(mouseId))
		{
			unbindCurrentElement();
		}
	}
	
	public void mouseScroll(double xOffset, double yOffset)
	{
		if (focusedElement != null && focusedElement.nMouseScroll(xOffset, yOffset))
		{
			unbindCurrentElement();
		}
	}
	
	public int keyPressed(int keyId, int mods)
	{
		if (focusedElement != null)
		{
			return focusedElement.nKeyPressed(keyId, mods);
		}
		if (keyId == GLFW_KEY_ESCAPE)
		{
			if ((mods & 1) == 1)
			{
				gameSettings.changeWindowMode();
			}
			else
			{
				if (getParent() != null)
				{
					getParent().reinit();
					application.reinitGui(getParent());
					return 0;
				}
				else
				{
					application.close();
				}
			}
		}
		return -1;
	}
	
	public int keyHeld(int keyId, int called, int mods)
	{
		if (focusedElement != null)
		{
			return focusedElement.nKeyHeld(keyId, called, mods);
		}
		return -1;
	}
	
	public void keyRelease(int keyId, int mods)
	{
		if (focusedElement != null && focusedElement.nKeyRelease(keyId, mods))
		{
			unbindCurrentElement();
		}
	}
	
	public void addElement(GuiContent<T> e)
	{
		e.init(this, elementCounter);
		elementCounter++;
		elementList.add(e);
	}
	
	public void removeElement(int i)
	{
		elementList.remove(i).close();
	}
	
	public void removeElements(int i, int i1)
	{
		for (int j = i1 - 1; j >= i; j--)
		{
			elementList.remove(j).close();
		}
	}
	
	public int getSize()
	{
		return elementList.size();
	}
	
	public void elementUpdate(GuiContent<T> e, int actionId)
	{
	}
	
	public void clearElements()
	{
		elementList.clear();
	}
	
	public GuiContent<T> getCurrentElement()
	{
		return focusedElement;
	}
	
	public void setCurrentElement(int id)
	{
		focusedElement = this.getElement(id);
	}
	
	public GuiContent<T> getElement(int i)
	{
		return elementList.get(i);
	}
	
}
