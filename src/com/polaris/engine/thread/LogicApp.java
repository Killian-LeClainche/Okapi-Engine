/**
 * 
 */
package com.polaris.engine.thread;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.polaris.engine.App;
import com.polaris.engine.LogicGui;

/**
 * @author Killian Le Clainche
 *
 */
public class LogicApp extends Thread
{
	public static final Log log = LogFactory.getLog(LogicApp.class);
	
	private final App application;
	private final Set<AppPacket> incomingPackets;
	
	private final long nanoTimeWait;
	private final double tickrate;
	
	private boolean isRunning;
	
	private long nextTickTime;
	
	private LogicGui logicHandler;
	
	public LogicApp(App app, int maxUps)
	{
		application = app;
		incomingPackets = new ConcurrentSkipListSet<AppPacket>(new PacketComparator());
		
		isRunning = true;
		
		nanoTimeWait = 1000000000L / maxUps;
		tickrate = 1.0D / maxUps;
	}
	
	@Override
	public void run()
	{
		Iterator<AppPacket> polledPackets;
		AppPacket packet;
		long waitTime;
		while(isRunning)
		{
			polledPackets = incomingPackets.iterator();
			while(polledPackets.hasNext())
			{
				packet = polledPackets.next();
				polledPackets.remove();
				packet.handle();
			}
			
			logicHandler.update();
			
			try
			{
				waitTime = (long) ((nextTickTime - System.nanoTime()) / 1000000L);
				if(waitTime > 0)
					Thread.sleep(waitTime);
			}
			catch (InterruptedException e)
			{
				log.debug("Somehow this thread was stopped unconventially.");
				isRunning = false;
			}
			finally
			{
				nextTickTime += nanoTimeWait;
			}
		}
	}
	
	public void setLogicHandler(LogicGui logic)
	{
		if(logic == null)
		{
			sendTerminatePacket();
			close();
			return;
		}
		if(logicHandler != null)
		{
			logicHandler.close();
			if(logicHandler.getParent() == logic)
			{
				logic.reinit();
				logicHandler = logic;
				sendReinitGuiPacket(logic);
				return;
			}
		}
		
		logic.init();
		logicHandler = logic;
		sendInitGuiPacket(logic);
	}
	
	public void close()
	{
		isRunning = false;
	}

	public final void sendTerminatePacket()
	{
		TerminateAppPacket packet = new TerminateAppPacket(application, this);
		application.sendPacket(packet);
	}
	
	public final void sendInitGuiPacket(LogicGui logic)
	{
		InitGuiPacket packet = new InitGuiPacket(application, this, logic);
		application.sendPacket(packet);
	}
	
	public final void sendReinitGuiPacket(LogicGui logic)
	{
		ReinitGuiPacket packet = new ReinitGuiPacket(application, this, logic);
		application.sendPacket(packet);
	}
	
	/**
	 * @return
	 */
	public double getTickrate()
	{
		return tickrate;
	}
	
	public App getApplication()
	{
		return application;
	}
	
}
