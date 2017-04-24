package com.polaris.engine.gui.content;

import com.polaris.engine.options.Settings;

public abstract class GuiList<T, I extends Settings> extends GuiContent<I>
{
	protected GuiListItem<?>[] elementList;
	protected double shiftWidth = 0;
	protected double extraWidth = 0;
	protected double shiftHeight = 0;
	protected double extraHeight = 0;
	protected boolean clicked = false;

	public GuiList(double x, double y, double width, double height, GuiListItem<?>... list)
	{
		this(x, y, 0, width, height, list);
	}
	
	public GuiList(double x, double y, double z, double width, double height, GuiListItem<?>... list)
	{
		super(x, y, width, height);
		elementList = list;
		setListDimensions();
	}

	protected abstract void setListDimensions();
	
	public GuiList(double x, double y, double width, double sWidth, double eWidth, double height, double sHeight, double eHeight, GuiListItem<?>... list)
	{
		this(x, y, 0, width, sWidth, eWidth, height, sHeight, eHeight, list);
	}

	public GuiList(double x, double y, double z, double width, double sWidth, double eWidth, double height, double sHeight, double eHeight, GuiListItem<?>... list)
	{
		this(x, y, z, width, height, list);
		shiftWidth = sWidth;
		extraWidth = eWidth;
		shiftHeight = sHeight;
		extraHeight = eHeight;
	}

	@Override
	public void update(double delta)
	{
		ticksExisted++;
		if (clicked)
		{
			for (int i = 0; i < elementList.length; i++)
			{
				//elementList[i].update(delta);
			}
		}
	}

	@Override
	public void render(double delta)
	{
		if (clicked)
		{
			for (int i = 0; i < elementList.length; i++)
			{
				elementList[i].render(delta);
			}
		}
	}

	@Override
	public int mouseClick(int mouseId)
	{
		if (!clicked)
		{
			clicked = true;
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
	
	public void mouseOutOfRegion(int mouseId)
	{
		clicked = false;
	}

}
