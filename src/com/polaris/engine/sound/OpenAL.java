/**
 *
 */
package com.polaris.engine.sound;

import com.polaris.engine.options.Settings;
import org.lwjgl.openal.*;

/**
 * @author lec50
 */
public class OpenAL
{
	
	protected final Settings gameSettings;
	
	protected ALCCapabilities alcCapabilities;
	protected ALCapabilities alCapabilities;
	
	public OpenAL(Settings settings)
	{
		gameSettings = settings;
	}
	
	public void init()
	{
		String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		
		long device = ALC10.alcOpenDevice(defaultDeviceName);
		int[] attributes = {0};
		long context = ALC10.alcCreateContext(device, attributes);
		
		ALC10.alcMakeContextCurrent(context);
		
		alcCapabilities = ALC.createCapabilities(device);
		alCapabilities = AL.createCapabilities(alcCapabilities);
	}
	
	public void close()
	{
	
	}
	
}
