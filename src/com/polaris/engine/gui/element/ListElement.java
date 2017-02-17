package com.polaris.engine.gui.element;

import static com.polaris.engine.Application.getMouseX;
import static com.polaris.engine.Application.getMouseY;

import java.util.ArrayList;
import java.util.List;

public abstract class ListElement<T> extends Element
{
	protected ListItem<?>[] elementList;
	protected double shiftWidth = 0;
	protected double extraWidth = 0;
	protected double shiftHeight = 0;
	protected double extraHeight = 0;
	protected boolean clicked = false;

	public ListElement(double x, double y, double width, double height, ListItem<?> ... list)
	{
		this(x, y, 0, width, height, list);
	}
	
	public ListElement(double x, double y, double z, double width, double height, ListItem<?> ... list)
	{
		super(x, y, width, height);
		elementList = list;
		setListDimensions();
	}

	public ListElement(double x, double y, double width, double sWidth, double eWidth, double height, double sHeight, double eHeight, ListItem<?> ... list)
	{
		this(x, y, 0, width, sWidth, eWidth, height, sHeight, eHeight, list);
	}
	
	public ListElement(double x, double y, double z, double width, double sWidth, double eWidth, double height, double sHeight, double eHeight, ListItem<?> ... list)
	{
		this(x, y, z, width, height, list);
		shiftWidth = sWidth;
		extraWidth = eWidth;
		shiftHeight = sHeight;
		extraHeight = eHeight;
	}

	protected abstract void setListDimensions();

	@Override
	public void update(double delta)
	{
		ticksExisted++;
		if(clicked)
		{
			for(int i = 0; i < elementList.length; i++)
			{
				elementList[i].update(delta);
			}
		}
	}

	@Override
	public void render(double delta)
	{
		if(clicked)
		{
			for(int i = 0; i < elementList.length; i++)
			{
				elementList[i].render(delta);
			}
		}
	}

	@Override
	public int mouseClick(int mouseId) 
	{
		if(!clicked)
		{
			clicked = true;
		}
		else if(super.isInRegion())
		{
			clicked = false;
		}
		return 2;
	}

	@SuppressWarnings("unchecked")
	public T getValue(int i)
	{
		return (T) elementList[i].getValue();
	}

	public int getListSize()
	{
		return elementList.length;
	}

	public boolean isInRegion()
	{
		return super.isInRegion() || (clicked && getMouseX() >= position.x - shiftWidth && getMouseY() >= position.y - shiftHeight && getMouseX() <= (position.x - shiftWidth + extraWidth) && getMouseY() <= (position.y + elementHeight - shiftHeight + extraHeight));
	}
	
	public void mouseOutOfRegion(int mouseId)
	{
		clicked = false;
	}

}
