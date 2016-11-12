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
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;
import static com.polaris.engine.util.Helper.*;

import org.lwjgl.BufferUtils;

public class ResourceHelper
{
	
	public static byte[] convertToBytes(List<Byte> list)
	{
		byte[] array = new byte[list.size()];
		for(int i = 0; i < array.length; i++)
		{
			array[i] = list.get(i);
		}
		return array;
	}

	public static String[] readCompressedFile(File file) throws IOException
	{
		List<String> stringList = new ArrayList<String>();
		InflaterInputStream stream = new InflaterInputStream(new FileInputStream(file), new Inflater(), 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				stringList.add(line);
			}
		}
		catch(Exception e) {}
		reader.close();
		return stringList.toArray(new String[stringList.size()]);
	}
	
	public static URL getResource(String s)
	{
		return classLoader.getResource(s);
	}

	public static InputStream getResourceStream(String s)
	{
		return classLoader.getResourceAsStream(s);
	}

	public static void createFileSafely(File file) throws IOException
	{
		File parentFile = new File(file.getParent());
		if(!parentFile.exists())
		{
			if(!parentFile.mkdirs())
			{
				throw new IOException("Unable to create parent file: "+file.getParent());
			}
		}
		if(file.exists())
		{
			if(!file.delete())
			{
				throw new IOException("Couldn't delete '".concat(file.getAbsolutePath()).concat("'"));
			}
		}
		if(!file.createNewFile())
		{
			throw new IOException("Couldn't create '".concat(file.getAbsolutePath()).concat("'"));
		}
	}

	public static void downloadFile(String url, File dest) throws IOException
	{
		String md5 = null;
		if (dest.exists())
		{
			md5 = getMD5(dest);
		}

		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		if (md5 != null)
		{
			connection.setRequestProperty("If-None-Match", md5);
		}
		connection.connect();

		if(connection.getResponseCode() == 304)
		{
			return;
		}

		createFileSafely(dest);

		BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), 16384);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest), 16384);

		int readBytes = 0;
		byte[] block = new byte[16384];

		while ((readBytes = in.read(block)) > 0)
		{
			out.write(block, 0, readBytes);
		}
		out.flush();
		out.close();
		in.close();
		connection.disconnect();
	}

	public static BufferedImage downloadImage(String url)
	{
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.connect();
			BufferedImage image = ImageIO.read(connection.getInputStream());
			connection.disconnect();
			return image;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			if(connection != null)
			{
				connection.disconnect();
			}
			return null;
		}
	}

	public static byte[] readStreamFully(InputStream stream) throws IOException
	{
		byte[] data = new byte[4096];
		ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
		int len;
		do
		{
			len = stream.read(data);
			if (len > 0)
			{
				entryBuffer.write(data, 0, len);
			}
		} while (len != -1);

		return entryBuffer.toByteArray();
	}
	
	public static ByteBuffer ioResourceToByteBuffer(File file) throws IOException
	{
		FileInputStream fileStream;
		FileChannel channel;
		MappedByteBuffer buffer;
		
		fileStream = new FileInputStream(file);
		channel = fileStream.getChannel();
		buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		
		fileStream.close();
		
		return buffer;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		File file;
		if (url != null)
		    file = new File(url.getFile());
		else
		    file = new File(resource);
		if ( file.isFile() ) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);

			while ( fc.read(buffer) != -1 ) ;

			fc.close();
			fis.close();
		} else {
			buffer = BufferUtils.createByteBuffer(bufferSize);

			InputStream source = url.openStream();
			if ( source == null )
				throw new FileNotFoundException(resource);

			try {
				ReadableByteChannel rbc = Channels.newChannel(source);
				try {
					while ( true ) {
						int bytes = rbc.read(buffer);
						if ( bytes == -1 )
							break;
						if ( buffer.remaining() == 0 )
							buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				} finally {
					rbc.close();
				}
			} finally {
				source.close();
			}
		}

		buffer.flip();
		return buffer;
	}

	public static void copyFile(File from, File to) throws IOException 
	{
		createFileSafely(to);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(from));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(to));
		byte[] block;
		while (bis.available() > 0) 
		{
			block = new byte[16384];
			final int readNow = bis.read(block);
			bos.write(block, 0, readNow);
		}
		bos.flush();
		bos.close();
		bis.close();
	}

	public static String getMD5(File file) throws IOException
	{
		DigestInputStream stream = null;
		try
		{
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] buffer = new byte[65536];

			int read = stream.read(buffer);
			while (read >= 1)
				read = stream.read(buffer);
		} 
		catch (Exception ignored)
		{
			return null;
		} 
		finally
		{
			stream.close();
		}

		return String.format("%1$032x", new Object[]{new BigInteger(1, stream.getMessageDigest().digest())});
	}
	
	public static BufferedReader newReader(File file) throws FileNotFoundException 
	{
		return new BufferedReader(new FileReader(file));
	}

	public static BufferedWriter newWriter(File file) throws IOException
	{
		return new BufferedWriter(new FileWriter(file));
	}

	public static boolean fileStartsWith(File file, String ... strings)
	{
		boolean flag = false;
		for(int i = 0; i < strings.length && !flag; i++)
		{
			flag = file.getName().startsWith(strings[i]);
		}
		return flag;
	}
	
	public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
	{
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

}
