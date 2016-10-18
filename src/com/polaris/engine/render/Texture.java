package com.polaris.engine.render;

import static com.polaris.engine.options.Settings.getModelDirectory;
import static com.polaris.engine.options.Settings.getTextureDirectory;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteriv;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.GL11;

import com.polaris.engine.util.Helper;
import com.polaris.engine.util.ResourceHelper;

public class Texture
{

	private static Map<String, ITexture> textures = new HashMap<String, ITexture>();
	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	private static ITexture currentTexture = null;

	/**
	 * Called once by Application.java to create the texture, model, and font handling. NEVER CALL!
	 * @param location
	 * @throws IOException
	 */
	public static void loadTextures() throws IOException
	{
		new File(getTextureDirectory(), "stitched").mkdir();
		for(File file : getTextureDirectory().listFiles())
		{
			if(file.isDirectory() && !ResourceHelper.fileStartsWith(file, "stitched", "models"))
			{
				loadTextures(file, !file.getName().startsWith("$"));
			}
		}
	}

	public static Map<String, ByteBuffer> getTextureData()
	{
		Map<String, ByteBuffer> textureData = new HashMap<String, ByteBuffer>();
		for(String texture : textures.keySet())
		{
			ByteBuffer data;
			IntBuffer width = BufferUtils.createIntBuffer(1);
			IntBuffer height = BufferUtils.createIntBuffer(1);
			ITexture tex = textures.get(texture);
			glBindTexture(tex.getTextureID());
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);
			data = BufferUtils.createByteBuffer(width.get() * height.get() * 4);
			glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			textureData.put(texture, data);
			glBindTexture(0);
			glDeleteTextures(tex.getTextureID());
		}
		return textureData;
	}

	public static void loadTextureData(Map<String, ByteBuffer> textureData)
	{
		for(String texture : textures.keySet())
		{
		    IntBuffer width = BufferUtils.createIntBuffer(1);
			IntBuffer height = BufferUtils.createIntBuffer(1);
			ITexture tex = textures.get(texture);
			glBindTexture(tex.getTextureID());
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);

			glBindTexture(tex.getTextureID());
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData.get(texture));
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width.get(), height.get(), GL_RGBA, GL_UNSIGNED_BYTE, textureData.get(texture));
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
	}

	private static void loadTextures(File stitchDirectory, boolean load) throws IOException
	{
		for(File subDirectory : stitchDirectory.listFiles())
		{
			if(subDirectory.isDirectory())
			{
				loadTextures(subDirectory, !subDirectory.getName().startsWith("$"));
			}
		}
		List<File> stitchTextures = new ArrayList<File>();
		for(File stitchTexture : stitchDirectory.listFiles())
		{
			if(stitchTexture.isFile() && stitchTexture.getName().endsWith(".png"))
			{
				stitchTextures.add(stitchTexture);
			}
		}

		if(stitchTextures.size() == 0)
			return;

		ITexture texture = null;
		String title = stitchDirectory.getPath().replace("$", "").substring(getTextureDirectory().getPath().length() + 1).replace("\\", ":");
		if(title.startsWith("font:"))
		{
			if(load)
			{
				texture = new FontMap();
				textures.put(title, texture);
				((FontMap) texture).setTextureID(createTextureId(((FontMap) texture).genTextureMap(stitchDirectory), false));
			}
		}
		else
		{
			texture = new StitchedMap();
			BufferedImage savedImage = ((StitchedMap)texture).genTextureMap(stitchTextures, new File(stitchDirectory, "animation.ani"));

			if(load)
			{
				texture.setTextureID(createTextureId(savedImage, false));
				textures.put(title, texture);
			}
			new File(getTextureDirectory(), "stitched/" + title + ".png").createNewFile();
			new File(getTextureDirectory(), "stitched/" + title + ".info").createNewFile();
			ImageIO.write(savedImage, "PNG", new File(getTextureDirectory(), "stitched/" + title + ".png"));
			((StitchedMap)texture).genInfo(new File(getTextureDirectory(), "stitched/" + title + ".info"));
		}
	}

	/**
	 * Clears all stitched maps from memory
	 */
	public static void clearTextureMap()
	{
		ITexture texture;
		for(String textureTitle : textures.keySet())
		{
			texture = textures.get(textureTitle);
			if(texture instanceof StitchedMap)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(textureTitle);
			}
		}
	}

	/**
	 * Clears all fonts from memory
	 */
	public static void clearFontMap()
	{
		ITexture texture;
		for(String fontTitle : textures.keySet())
		{
			texture = textures.get(fontTitle);
			if(texture instanceof FontMap)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(fontTitle);
			}
		}
	}

	/**
	 * clears all virtual textures from memory
	 */
	public static void clearVirtualMap()
	{
		ITexture texture;
		for(String virtualTitle : textures.keySet())
		{
			texture = textures.get(virtualTitle);
			if(texture instanceof VirtualTexture)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(virtualTitle);
			}
		}
	}

	/**
	 * Loads a stitched texture map into memory
	 * @param texture
	 * @throws IOException
	 */
	public static void loadTextureMap(String texture) throws IOException
	{
		File textureMapFile = new File(getTextureDirectory(), "stitched/" + texture + ".png");
		File textureMapInfo = new File(getTextureDirectory(), "stitched/" + texture + ".info");
		if(textureMapFile.isFile() && textureMapInfo.isFile())
		{
			if(!textures.containsKey(texture))
			{
				StitchedMap stitchedTexture = new StitchedMap();
				stitchedTexture.loadInfo(textureMapInfo);
				stitchedTexture.setTextureID(createTextureId(ImageIO.read(textureMapFile), false));
				textures.put(texture, stitchedTexture);
			}
		}
	}

	public static void loadFontMap(String font) throws IOException
	{
		File fontLoc = new File(getTextureDirectory(), font.replace(":", "/"));
		File fontPNG = new File(fontLoc, "font.png");
		if(fontLoc.exists() && fontLoc.isDirectory() && fontPNG.exists() && new File(fontLoc, "font.txt").exists())
		{
			FontMap fontMap = new FontMap();
			fontMap.genTextureMap(fontLoc);
			fontMap.setTextureID(createTextureId(ImageIO.read(fontPNG), false));
			textures.put(font, fontMap);
		}
	}

	/**
	 * Loads a model into memory
	 * @param model
	 * @throws IOException
	 */
	public static void loadModel(String model) throws IOException
	{
		String convertedModel = model.replace(":", "/");
		File textureFile = new File(getTextureDirectory(), "models/" + convertedModel.substring(0, model.lastIndexOf('.')) + ".png");
		File modelFile = new File(getModelDirectory(), convertedModel);
		if(textureFile.isFile() && modelFile.isFile())
		{
			if(!modelMap.containsKey(model))
			{
				Model loadedModel = loadModel(model.substring(model.lastIndexOf('.')), modelFile);
				loadedModel.setTextureID(createTextureId(ImageIO.read(textureFile), false));
				modelMap.put(model, loadedModel);
			}
		}
	}

	private static Model loadModel(String endingTitle, File location)
	{
		Model model = null;
		if(Helper.modelFormats.containsKey(endingTitle))
		{
			try 
			{
				model = Helper.modelFormats.get(endingTitle).newInstance(location);
			} 
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		return model;
	}

	/**
	 * Binds a texture to the system for rendering, make sure to enable GL_TEXTURE_2D
	 * @param textureId
	 */
	public static void glBindTexture(int textureId)
	{
		currentTexture = null;
		GL11.glBindTexture(GL_TEXTURE_2D, textureId);
	}

	/**
	 * Binds a texture to the system for rendering, make sure to enable GL_TEXTURE_2D
	 * @param texture
	 * @return
	 */
	public static boolean glBindTexture(String texture) 
	{
		ITexture textureId = getTextureId(texture);
		if(textureId != null && textureId != currentTexture)
		{
			glBindTexture(textureId.getTextureID());
			currentTexture = textureId;
			return true;
		}
		return false;
	}

	public static boolean glBindTexture(String texture, String subtexture) 
	{
		ITexture textureId = getTextureId(texture);
		if(textureId != null && textureId != currentTexture)
		{
			glBindTexture(textureId.getTextureID());
			currentTexture = textureId;
			return ((StitchedMap) currentTexture).bindTexture(subtexture);
		}
		return false;
	}

	public static boolean glBindSubTexture(String texture)
	{
		return ((StitchedMap) currentTexture).bindTexture(texture);
	}
	
	/**
	 * Removes a texture from memory
	 * @param texture
	 * @return
	 */
	public static boolean glClearTexture(String texture)
	{
		ITexture tex = textures.get(texture);
		if(tex != null)
		{
			textures.remove(texture);
			if(tex == currentTexture)
				currentTexture = null;
			glDeleteTextures(tex.getTextureID());
			return true;
		}
		return false;
	}

	/**
	 * returns the current texture bounded
	 * @return
	 */
	public static ITexture getTextureId()
	{
		return currentTexture;
	}

	public static FontMap getFontMap(String fontTitle)
	{
		fontTitle = "font:" + fontTitle;
		if(textures.containsKey(fontTitle))
			return (FontMap) textures.get(fontTitle);
		return null;
	}

	/**
	 * returns a texture
	 * @param texture
	 * @return
	 */
	public static ITexture getTextureId(String texture)
	{
		return textures.get(texture);
	}

	/**
	 * only use for texture maps, gets the current texture within the texture map
	 * @return
	 */
	public static Texture getTexture()
	{
		return currentTexture.getTexture();
	}

	/**
	 * only use for texture maps, gets a specfic texture within the texture map
	 * @param textureName
	 * @return
	 */
	public static Texture getTexture(String textureName)
	{
		if(currentTexture instanceof StitchedMap)
			return ((StitchedMap) currentTexture).getTexture(textureName);
		return null;
	}

	/**
	 * Generates a texture id int and loads the buffered image into memory
	 * @param image
	 * @param mipmap
	 * @return
	 */
	public static int createTextureId(BufferedImage image, boolean mipmap)
	{
		if(image != null)
		{
			int texture = glGenTextures();
			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for(int y = 0; y < image.getHeight(); y++)
			{
				for(int x = 0; x < image.getWidth(); x++)
				{
					int pixel = image.getRGB(x, y);
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) (pixel & 0xFF));
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}

			buffer.flip();
			glBindTexture(texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			if(mipmap)
			{
				ARBTextureStorage.glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, image.getWidth(), image.getHeight());
				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				ARBFramebufferObject.glGenerateMipmap(GL_TEXTURE_2D);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			}
			else
			{
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}
			return texture;
		}
		return -100;
	}

	private float minU, minV, maxU, maxV;

	public Texture(float u, float v, float u1, float v1)
	{
		minU = u;
		minV = v;
		maxU = u1;
		maxV = v1;
	}

	public void reduce(int width, int height)
	{
		minU /= (float) width;
		maxU /= (float) width;
		minV /= (float) height;
		maxV /= (float) height;
	}

	public void setMinU(float u)
	{
		minU = u;
	}

	public void setMinV(float v)
	{
		minV = v;
	}

	public void setMaxU(float u)
	{
		maxU = u;
	}

	public void setMaxV(float v)
	{
		maxV = v;
	}

	public float getMinU()
	{
		return minU;
	}

	public float getMinV()
	{
		return minV;
	}

	public float getMaxU()
	{
		return maxU;
	}

	public float getMaxV()
	{
		return maxV;
	}

	public float getMinU(int animationID)
	{
		return getMinU();
	}

	public float getMinV(int animationID)
	{
		return getMinV();
	}

	public float getMaxU(int animationID)
	{
		return getMaxU();
	}

	public float getMaxV(int animationID)
	{
		return getMaxV();
	}

}
