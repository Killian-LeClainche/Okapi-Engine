package com.polaris.engine.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader
{
	
	private static int shaderWrapperId = 0;
	private static int vertWrapperId = 0;
	private static int fragWrapperId = 0;
	
	//public static final Shader POS;
	//public static final Shader POS_COLOR;
	//public static final Shader POS_TEXTURE;
	//public static final Shader POS_COLOR_TEXTURE;
	
	static
	{
		//POS = createShader(new File("shaders/pos.vert"), new File("shaders/pos.frag"));
		
		//POS_COLOR = createShader(new File("shaders/pos_color.vert"), new File("shaders/pos_color.frag"));
		
		//POS_TEXTURE = createShader(new File("shaders/pos_texture.vert"), new File("shaders/pos_texture.frag"));
		
		//POS_COLOR_TEXTURE = createShader(new File("shaders/pos_color_texture.vert"), new File("shaders/pos_color_texture.frag"));
	}
	
	/*private static int createShaderId(File shaderFile, int type) throws IOException
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
	}*/
	
    private static int createShader(File filename, int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
             
            if(shader == 0)
                return 0;
             
            ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);
             
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
             
            return shader;
        }
        catch(Exception exc) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            System.out.println(exc.getMessage());
            return -1;
        }
    }
     
    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
     
    private static String readFileAsString(File file) throws Exception {
        StringBuilder source = new StringBuilder();
         
        FileInputStream in = new FileInputStream(file);
         
        Exception exception = null;
         
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
             
            Exception innerExc= null;
            try {
                String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
                exception = exc;
            }
            finally {
                try {
                    reader.close();
                }
                catch(Exception exc) {
                    if(innerExc == null)
                        innerExc = exc;
                    else
                        exc.printStackTrace();
                }
            }
             
            if(innerExc != null)
                throw innerExc;
        }
        catch(Exception exc) {
            exception = exc;
        }
        finally {
            try {
                in.close();
            }
            catch(Exception exc) {
                if(exception == null)
                    exception = exc;
                else
                    exc.printStackTrace();
            }
             
            if(exception != null)
                throw exception;
        }
         
        return source.toString();
    }
	
	public static Shader createShader(File vertShaderFile, File fragShaderFile)
	{
		vertWrapperId = fragWrapperId = shaderWrapperId = 0;
		
			vertWrapperId = createShader(vertShaderFile, GL20.GL_VERTEX_SHADER);
			fragWrapperId = createShader(fragShaderFile, GL20.GL_FRAGMENT_SHADER);
			
			shaderWrapperId = ARBShaderObjects.glCreateProgramObjectARB();
	         
	        if(shaderWrapperId == 0)
	            return null;
	         
	        /*
	        * if the vertex and fragment shaders setup sucessfully,
	        * attach them to the shader program, link the sahder program
	        * (into the GL context I suppose), and validate
	        */
	        ARBShaderObjects.glAttachObjectARB(shaderWrapperId, vertWrapperId);
	        ARBShaderObjects.glAttachObjectARB(shaderWrapperId, fragWrapperId);
	         
	        ARBShaderObjects.glLinkProgramARB(shaderWrapperId);
	        if (ARBShaderObjects.glGetObjectParameteriARB(shaderWrapperId, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
	            System.err.println(getLogInfo(shaderWrapperId));
	            return null;
	        }
	         
	        ARBShaderObjects.glValidateProgramARB(shaderWrapperId);
	        if (ARBShaderObjects.glGetObjectParameteriARB(shaderWrapperId, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
	            System.err.println(getLogInfo(shaderWrapperId));
	            return null;
	        }
			
			/*shaderWrapperId = GL20.glCreateProgram();
			GL20.glAttachShader(shaderWrapperId, vertWrapperId);
			GL20.glAttachShader(shaderWrapperId, fragWrapperId);
			GL20.glLinkProgram(shaderWrapperId);
			
			int linked = GL20.glGetProgrami(shaderWrapperId, GL20.GL_LINK_STATUS);
			String programLog = GL20.glGetProgramInfoLog(shaderWrapperId);
			
			if (linked == 0)
			{
				GL20.glDeleteProgram(shaderWrapperId);
				throw new AssertionError("Could not link program " + programLog);
			}*/
			return new Shader(shaderWrapperId, vertWrapperId, fragWrapperId);
		
		/*if(vertWrapperId != 0)
		{
			GL20.glDeleteShader(vertWrapperId);
		}
		if(fragWrapperId != 0)
		{
			GL20.glDeleteShader(fragWrapperId);
		}*/
		//return null;
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
		//GL20.glUseProgram(programId);
		ARBShaderObjects.glUseProgramObjectARB(programId);
	}
	
	public void unbind()
	{
		//GL20.glUseProgram(0);
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	public void destroy()
	{
		unbind();
		GL20.glDetachShader(programId, vertShaderId);
		GL20.glDetachShader(programId, fragShaderId);
		
		GL20.glDeleteShader(vertShaderId);
		GL20.glDeleteShader(fragShaderId);
		GL20.glDeleteProgram(programId);
	}
	
	public int getShaderId()
	{
		return programId;
	}
	
}
