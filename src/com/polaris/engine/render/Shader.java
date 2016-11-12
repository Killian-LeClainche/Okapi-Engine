package com.polaris.engine.render;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL20;

import com.polaris.engine.App;
import com.polaris.engine.util.ResourceHelper;

public class Shader
{
	
	private static int shaderWrapperId = 0;
	private static int vertWrapperId = 0;
	private static int fragWrapperId = 0;
	
	public static final Shader POS;
	public static final Shader POS_COLOR;
	public static final Shader POS_TEXTURE;
	//public static final Shader POS_COLOR_TEXTURE;
	
	static
	{
		POS = createShader(new File("shaders/pos.vert"), new File("shaders/pos.frag"));
		
		POS_COLOR = createShader(new File("shaders/pos_color.vert"), new File("shaders/pos_color.frag"));
		
		POS_TEXTURE = createShader(new File("shaders/pos_texture.vert"), new File("shaders/pos_texture.frag"));
		
		//POS_COLOR_TEXTURE = createShader(new File("shaders/pos_color_texture.vert"), new File("shaders/pos_color_texture.frag"));
		//POS_COLOR_TEXTURE.bindAttrib(0, "in_Position");
		//POS_COLOR_TEXTURE.bindAttrib(1, "in_Color");
		//POS_COLOR_TEXTURE.bindAttrib(2, "in_TexCoord");
	}
	
	private static int createShaderId(File shaderFile, int type) throws IOException
	{	
		ByteBuffer data = ResourceHelper.ioResourceToByteBuffer(shaderFile);
		
		shaderWrapperId = GL20.glCreateShader(type);
		
		PointerBuffer strings = BufferUtils.createPointerBuffer(1);
		IntBuffer lengths = BufferUtils.createIntBuffer(1);
		strings.put(0, data);
		lengths.put(0, data.remaining());
		GL20.glShaderSource(shaderWrapperId, strings, lengths);
		GL20.glCompileShader(shaderWrapperId);
		int compiled = GL20.glGetShaderi(shaderWrapperId, GL20.GL_COMPILE_STATUS);
		String shaderLog = GL20.glGetShaderInfoLog(shaderWrapperId);
		if (shaderLog.trim().length() > 0) 
		{
			System.err.println(shaderLog);
		}
		if (compiled == 0) 
		{
			GL20.glDeleteShader(shaderWrapperId);
			throw new AssertionError("Could not compile shader");
		}
		return shaderWrapperId;
	}
	
	public static Shader createShader(File vertShaderFile, File fragShaderFile)
	{
		vertWrapperId = fragWrapperId = shaderWrapperId = 0;
		try
		{
			vertWrapperId = createShaderId(vertShaderFile, GL20.GL_VERTEX_SHADER);
			fragWrapperId = createShaderId(fragShaderFile, GL20.GL_FRAGMENT_SHADER);
			
			shaderWrapperId = GL20.glCreateProgram();
			GL20.glAttachShader(shaderWrapperId, vertWrapperId);
			GL20.glAttachShader(shaderWrapperId, fragWrapperId);
			GL20.glLinkProgram(shaderWrapperId);
			
			int linked = GL20.glGetProgrami(shaderWrapperId, GL20.GL_LINK_STATUS);
			String programLog = GL20.glGetProgramInfoLog(shaderWrapperId);
			if (programLog.trim().length() > 0) 
			{
				App.log.error(programLog);
			}
			
			if (linked == 0)
			{
				GL20.glDeleteProgram(shaderWrapperId);
				throw new AssertionError("Could not link program");
			}
			return new Shader(shaderWrapperId, vertWrapperId, fragWrapperId);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		if(vertWrapperId != 0)
		{
			GL20.glDeleteShader(vertWrapperId);
		}
		if(fragWrapperId != 0)
		{
			GL20.glDeleteShader(fragWrapperId);
		}
		return null;
	}
	
	private int programId;
	private int vertShaderId;
	private int fragShaderId;
	
	private Shader(int pId, int vId, int fId)
	{
		programId = pId;
		vertShaderId = vId;
		fragShaderId = fId;
		
		GL20.glBindAttribLocation(programId, 0, "in_Position");
		GL20.glBindAttribLocation(programId, 1, "in_Color");
		GL20.glBindAttribLocation(programId, 2, "in_Normal");
		GL20.glBindAttribLocation(programId, 3, "in_TexCoord");
	}
	
	public void bind()
	{
		GL20.glUseProgram(programId);
	}
	
	public void unbind()
	{
		GL20.glUseProgram(0);
	}
	
	public void destroy()
	{
		GL20.glDeleteShader(vertShaderId);
		GL20.glDeleteShader(fragShaderId);
		GL20.glDeleteProgram(programId);
	}
	
}
