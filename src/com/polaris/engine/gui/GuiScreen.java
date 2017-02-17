package com.polaris.engine.gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.util.ArrayList;
import java.util.List;

import com.polaris.engine.App;
import com.polaris.engine.gui.element.Element;

public abstract class GuiScreen extends Gui
{

	private List<Element> elementList = new ArrayList<Element>();
	protected Element currentElement;

	public GuiScreen(App app)
	{
		super(app);
	}

	public GuiScreen(GuiScreen gui)
	{
		this(gui.application);
		parent = gui;
	}

	public void run()
	{
		super.run();
		for(Element element : elementList)
		{
			element.update();
		}
	}

	public void render(double delta)
	{
		super.render(delta);
		for(Element element : elementList)
		{
			element.render(delta);
		}
	}

	public boolean mouseClick(int mouseId)
	{
		for(Element element : elementList)
		{
			if(element.isInRegion())
			{
				boolean flag = element.nMouseClick(mouseId);
				if(flag && element != currentElement)
				{
					unbindCurrentElement(element);
				}
				return flag;
			}
		}
		unbindCurrentElement();
		return false;
	}

	public void mouseHeld(int mouseId)
	{
		if(currentElement != null && currentElement.nMouseHeld(mouseId))
		{
			unbindCurrentElement();
		}
	}

	public void mouseRelease(int mouseId)
	{
		if(currentElement != null && !currentElement.nMouseRelease(mouseId))
		{
			unbindCurrentElement();
		}
	}

	public void mouseScroll(double xOffset, double yOffset) 
	{
		if(currentElement != null && currentElement.nMouseScroll(xOffset, yOffset))
		{
			unbindCurrentElement();
		}
	}

	public int keyPressed(int keyId, int mods) 
	{
		if(currentElement != null)
		{
			return currentElement.nKeyPressed(keyId, mods);
		}
		if(keyId == GLFW_KEY_ESCAPE)
		{
			if((mods & 1) == 1)
			{
				gameSettings.changeWindowMode();
			}
			else
			{
				if(getParent() != null)
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
		if(currentElement != null)
		{
			return currentElement.nKeyHeld(keyId, called, mods);
		}
		return -1;
	}

	public void keyRelease(int keyId, int mods)
	{
		if(currentElement != null && currentElement.nKeyRelease(keyId, mods))
		{
			unbindCurrentElement();
		}
	}

	public void unbindCurrentElement(Element e)
	{
		unbindCurrentElement();
		currentElement = e;
	}

	public void unbindCurrentElement()
	{
		if(currentElement != null)
		{
			currentElement.unbind();
			currentElement = null;
		}
	}

	public void addElement(Element e)
	{
		e.setId(elementList.size());
		e.setGui(this);
		elementList.add(e);
	}

	public void removeElement(int i)
	{
		elementList.remove(i).close();
	}

	public void removeElements(int i, int i1)
	{
		for(int j = i1 - 1; j >= i; j--)
		{
			elementList.remove(j).close();
		}
	}

	public Element getElement(int i)
	{
		return elementList.get(i);
	}

	public int getSize()
	{
		return elementList.size();
	}

	public void elementUpdate(Element e, int actionId) {}

	public void clearElements()
	{
		elementList.clear();
	}

	public void close() 
	{
		this.currentElement = null;
	}

	public Element getCurrentElement()
	{
		return currentElement;
	}

	public void setCurrentElement(int id)
	{
		currentElement = this.getElement(id);
	}
	
	public Application getApplication()
	{
		return application;
	}

}
