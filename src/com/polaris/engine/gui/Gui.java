package com.polaris.engine.gui;

import java.util.concurrent.ExecutorService;

import com.polaris.engine.App;
import com.polaris.engine.options.Settings;

public abstract class Gui implements Runnable
{
	
	protected final App application;
	
	protected double ticksExisted;
	
	protected Gui parent;
	protected Settings gameSettings;

	public Gui(App app)
	{
		this(app, null, 0);
	}
	
	public Gui(App app, Gui p, double ticks)
	{
		application = app;
		ticksExisted = ticks;
		gameSettings = app.getSettings();
	}
	
	public void init() {}
	
	public void createTasks(ExecutorService service) {}
	
	public void run()
	{
		ticksExisted ++;
	}

	public void render(double delta)
	{
		application.gl2d();
	}

	public void reinit() {}
	public void reload() {}

	public void close() {}
	
	public Gui getParent()
	{
		return parent;
	}

}
