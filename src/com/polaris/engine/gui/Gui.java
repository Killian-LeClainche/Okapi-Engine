package com.polaris.engine.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.polaris.engine.App;
import com.polaris.engine.options.Input;
import com.polaris.engine.options.Settings;

public abstract class Gui<T extends Settings> implements Runnable
{

	protected double ticksExisted;
	
	protected final Gui<T> parent;
	
	protected final App<T> application;
	protected final Input input;
	protected final T gameSettings;

	public Gui(App<T> app)
	{
		this(app, null, 0);
	}
	
	public Gui(App<T> app, Gui<T> p, double ticks)
	{
		ticksExisted = ticks;
		
		parent = p;
		
		application = app;
		
		input = app.getInput();
		gameSettings = app.getSettings();
	}
	
	public void init() {}
	
	public void createTasks(ExecutorService service) {}
	
	public void run()
	{
		this.update(application.getTickDelta());
	}
	
	public void update(double delta)
	{
		ticksExisted += delta;
	}

	public void render(double delta)
	{
		application.gl2d();
	}

	public void reinit() {}
	public void reload() {}

	public void close() {}
	
	public Gui<T> getParent()
	{
		return parent;
	}
	
	public App<T> getApplication()
	{
		return application;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public T getSettings()
	{
		return gameSettings;
	}

}
