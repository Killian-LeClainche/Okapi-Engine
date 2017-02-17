package com.polaris.engine.gui.element;


import org.joml.Vector3d;

import com.polaris.engine.gui.GuiScreen;

public abstract class Element 
{
	
	protected Vector3d position;
	protected double elementWidth = 0;
	protected double elementHeight = 0;
	protected int elementId = 0;
	protected int ticksExisted = 0;
	protected GuiScreen gui;
	protected boolean highlighted = false;
	
	public Element(double x, double y, double width, double height)
	{
		this(x, y, 0, width, height);
	}
	
	public Element(double x, double y, double z, double width, double height)
	{
		position = new Vector3d(x, y, z);
		elementWidth = width;
		elementHeight = height;
	}

	public void update()
	{
		ticksExisted++;
		highlighted = isInRegion() || gui.getCurrentElement() == this;
	}
	
	public abstract void render(double delta);
	
	public final boolean nMouseClick(int mouseId)
	{
		int flag = mouseClick(mouseId);
		if(flag > 1)
			gui.elementUpdate(this, 0);
		return flag % 2 == 1;
	}
	
	protected int mouseClick(int mouseId) {return 0;}
	
	public final boolean nMouseHeld(int mouseId)
	{
		int flag = mouseHeld(mouseId);
		if(flag > 1)
			gui.elementUpdate(this, 1);
		return flag % 2 == 1;
	}
	
	protected int mouseHeld(int mouseId) {return 1;}
	
	public final boolean nMouseRelease(int mouseId)
	{
		int flag = mouseRelease(mouseId);
		if(flag > 1)
			gui.elementUpdate(this, 2);
		return flag % 2 == 1;
	}
	
	protected int mouseRelease(int mouseId) {return 1;}
	
	public final boolean nMouseScroll(double xOffset, double yOffset)
	{
		int flag = mouseScroll(xOffset, yOffset);
		if(flag > 1)
			gui.elementUpdate(this, 3);
		return flag % 2 == 1;
	}
	
	protected int mouseScroll(double xOffset, double yOffset) {return 0;}
	
	public final int nKeyPressed(int keyId, int mods)
	{
		int flag = keyPressed(keyId, mods);
		if((flag & 0x0000FFFF) == 1)
			gui.elementUpdate(this, 4);
		return flag >>> 16;
	}
	
	protected int keyPressed(int keyId, int mods) {return 0;}
	
	public final int nKeyHeld(int keyId, int called, int mods)
	{
		int flag = keyHeld(keyId, called, mods);
		if((flag & 0x0000FFFF) == 1)
			gui.elementUpdate(this, 5);
		return flag >>> 16;
	}
	
	protected int keyHeld(int keyId, int called, int mods) {return 0;}
	
	public final boolean nKeyRelease(int keyId, int mods)
	{
		int flag = keyRelease(keyId, mods);
		if(flag > 1)
			gui.elementUpdate(this, 6);
		return flag % 2 == 1;
	}
	
	protected int keyRelease(int keyId, int mods) {return 0;}

	public void setId(int id) {elementId = id;}
	
	public int getId() {return elementId;}
	
	public boolean equals(Element e) {return getId() == e.getId();}
	
	public void setGui(GuiScreen g) {gui = g;}
	
	public GuiScreen getGui() {return gui;}
	
	public void close() {}

	public void unbind() {}
}
