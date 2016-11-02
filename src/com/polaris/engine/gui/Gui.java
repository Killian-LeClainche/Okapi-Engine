package com.polaris.engine.gui;

import static com.polaris.engine.render.Window.gl2d;

import com.polaris.engine.App;

public abstract class Gui
{
	
	protected final App application;
	
	protected double ticksExisted;

	public Gui(App app)
	{
		this(app, 0);
	}
	
	public Gui(App app, double ticks)
	{
		application = app;
		ticksExisted = ticks;
	}
	
	public void init() {}

	public void render(double delta)
	{
		gl2d();
	}

	public void reinit() {}
	public void reload() {}

	public void close() {}

}
