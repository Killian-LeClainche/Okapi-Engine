package com.polaris.engine;

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
		application.gl2d();
	}

	public void reinit() {}
	public void reload() {}

	public void close() {}

}
