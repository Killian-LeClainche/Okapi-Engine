package com.polaris.engine.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.polaris.engine.render.model.Model;
import com.polaris.engine.render.model.ObjModel;

public class Helper 
{

	/**
	 * Current OS that this application is running on. Though, this will only give generic os names.
	 * @value windows, linux, osx
	 */
	public static final String osName;
	/**
	 * Pi * 2
	 */
	public static final float TWOPI = (float) (Math.PI * 2);
	/**
	 * PI / 2
	 */
	public static final float HALFPI = (float) (Math.PI / 2);
	
	public static final float QUARTERPI = (float) (Math.PI / 4);
	
	public static final ClassLoader classLoader = Helper.class.getClassLoader();
	
	public static final Map<String, Constructor<? extends Model>> modelFormats = new HashMap<String, Constructor<? extends Model>>();

	static
	{
		String s = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if(s.contains("windows"))
			osName = "windows";
		else if(s.contains("linux"))
			osName = "linux";
		else
			osName = "osx";
		/*try
		{
			modelFormats.put("obj", ObjModel.class.getConstructor(File.class));
		} 
		catch (NoSuchMethodException | SecurityException e) {}*/
	}
	
}
