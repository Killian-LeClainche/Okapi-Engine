package polaris.okapi.gui.content;


import polaris.okapi.gui.GuiScreen;
import polaris.okapi.options.Settings;
import org.joml.Vector4d;

public abstract class GuiContent
{
	protected int elementId = 0;
	
	protected Vector4d bounds;
	protected double zScale;
	
	protected double ticksExisted = 0;
	protected boolean highlighted = false;
	
	protected GuiScreen gui;
	protected Settings gameSettings;
	
	public GuiContent(double x, double y, double width, double height)
	{
		this(x, y, 0, width, height);
	}
	
	public GuiContent(double x, double y, double z, double width, double height)
	{
		bounds = new Vector4d(x, y, width, height);
		zScale = z;
	}
	
	public void init(GuiScreen g, int id)
	{
		elementId = id;
		gui = g;
		gameSettings = g.getSettings();
	}
	
	public void update(double delta)
	{
		ticksExisted += delta;
		highlighted = gui.getFocusedElement() == this;
	}
	
	public abstract void render(double delta);
	
	public final boolean nMouseClick(int mouseId)
	{
		int flag = mouseClick(mouseId);
		if (flag > 1) gui.elementUpdate(this, 0);
		return flag % 2 == 1;
	}
	
	protected int mouseClick(int mouseId)
	{
		return 0;
	}
	
	public final boolean nMouseHeld(int mouseId)
	{
		int flag = mouseHeld(mouseId);
		if (flag > 1) gui.elementUpdate(this, 1);
		return flag % 2 == 1;
	}
	
	protected int mouseHeld(int mouseId)
	{
		return 1;
	}
	
	public final boolean nMouseRelease(int mouseId)
	{
		int flag = mouseRelease(mouseId);
		if (flag > 1) gui.elementUpdate(this, 2);
		return flag % 2 == 1;
	}
	
	protected int mouseRelease(int mouseId)
	{
		return 1;
	}
	
	public final boolean nMouseScroll(double xOffset, double yOffset)
	{
		int flag = mouseScroll(xOffset, yOffset);
		if (flag > 1) gui.elementUpdate(this, 3);
		return flag % 2 == 1;
	}
	
	protected int mouseScroll(double xOffset, double yOffset)
	{
		return 0;
	}
	
	public final int nKeyPressed(int keyId, int mods)
	{
		int flag = keyPressed(keyId, mods);
		if ((flag & 0x0000FFFF) == 1) gui.elementUpdate(this, 4);
		return flag >>> 16;
	}
	
	protected int keyPressed(int keyId, int mods)
	{
		return 0;
	}
	
	public final int nKeyHeld(int keyId, int called, int mods)
	{
		int flag = keyHeld(keyId, called, mods);
		if ((flag & 0x0000FFFF) == 1) gui.elementUpdate(this, 5);
		return flag >>> 16;
	}
	
	protected int keyHeld(int keyId, int called, int mods)
	{
		return 0;
	}
	
	public final boolean nKeyRelease(int keyId, int mods)
	{
		int flag = keyRelease(keyId, mods);
		if (flag > 1) gui.elementUpdate(this, 6);
		return flag % 2 == 1;
	}
	
	protected int keyRelease(int keyId, int mods)
	{
		return 0;
	}
	
	public boolean equals(GuiContent e)
	{
		return getId() == e.getId();
	}
	
	public int getId()
	{
		return elementId;
	}
	
	public void close()
	{
	}
	
	public void unbind()
	{
	}
	
	public final Vector4d getBounds()
	{
		return bounds;
	}
}
