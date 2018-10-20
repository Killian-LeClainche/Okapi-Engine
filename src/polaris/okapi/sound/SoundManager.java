package polaris.okapi.sound;

import polaris.okapi.options.Settings;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import polaris.okapi.options.Settings;

import java.nio.IntBuffer;

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

/**
 * @author lec50
 */
public class SoundManager
{
	
	protected final Settings gameSettings;
	
	protected ALCCapabilities alcCapabilities;
	protected ALCapabilities alCapabilities;
	
	protected float[] listenerPosition;
	protected float[] listenerVelocity;
	protected float[] listenerOrientation;
	
	public SoundManager(Settings settings)
	{
		gameSettings = settings;
	}
	
	public boolean init()
	{
		String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		
		long device = ALC10.alcOpenDevice(defaultDeviceName);
		
		IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
		
		contextAttribList.put(ALC10.ALC_REFRESH);
		contextAttribList.put(gameSettings.getAlcRefreshRate());
		
		contextAttribList.put(ALC10.ALC_SYNC);
		contextAttribList.put(gameSettings.getAlcSync() ? ALC10.ALC_TRUE : ALC10.ALC_FALSE);
		
		
		contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
		contextAttribList.put(2);
		
		contextAttribList.put(0);
		contextAttribList.flip();
		
		long context = ALC10.alcCreateContext(device, contextAttribList);
		
		ALC10.alcMakeContextCurrent(context);
		
		alcCapabilities = ALC.createCapabilities(device);
		alCapabilities = AL.createCapabilities(alcCapabilities);
		
		return true;
	}
	
	public void update()
	{
		AL10.alListenerfv(AL10.AL_POSITION, listenerPosition);
		AL10.alListenerfv(AL10.AL_VELOCITY, listenerVelocity);
		AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOrientation);
	}
	
	public void close()
	{
	
	}
	
}
