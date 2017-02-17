package com.polaris.engine.render;

import static com.polaris.engine.render.OpenGL.getAlpha;
import static com.polaris.engine.render.OpenGL.getBlue;
import static com.polaris.engine.render.OpenGL.getGreen;
import static com.polaris.engine.render.OpenGL.getRed;
import static com.polaris.engine.render.OpenGL.glBegin;
import static java.lang.Integer.parseInt;
import static org.lwjgl.opengl.GL11.glEnd;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class FontMap implements ITexture
{

	private Map<Integer, IntObject> textureArray = new HashMap<Integer, IntObject>();
	private int textureId = 0;

	public BufferedImage genTextureMap(File baseLoc) throws IOException
	{
		BufferedImage image = ImageIO.read(new File(baseLoc, "font.png")); 
		File fontFile = new File(baseLoc, "font.txt");
		File kerningFile = new File(baseLoc, "kerning.txt");
		float largestWidth = 0;
		float largestHeight = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fontFile));
		String line = null;
		while((line = reader.readLine()) != null && line.length() > 0 && !line.startsWith("#"))
		{
			String[] content = line.split(" ");
			IntObject object = new IntObject();
			textureArray.put(parseInt(content[0]), object);
			int x = parseInt(content[1]);
			int y = parseInt(content[2]);
			object.width = parseInt(content[3]);
			object.height = parseInt(content[4]);
			object.xOffset = Float.parseFloat(content[5]);
			object.yOffset = Float.parseFloat(content[6]);
			object.xadvance = Float.parseFloat(content[7]);
			largestWidth = Math.max(largestWidth, object.width);
			largestHeight = Math.max(largestHeight, object.height);
			object.texture = new Texture(x, y, x + object.width, y + object.height);
			object.texture.reduce(image.getWidth(), image.getHeight());
			object.kerning = null;
		}
		for(Integer charID : textureArray.keySet())
		{
			IntObject object = textureArray.get(charID);
			object.width /= largestWidth;
			object.xOffset /= largestWidth;
			object.height /= largestHeight;
			object.yOffset /= largestHeight;
			object.xadvance /= largestWidth;
		}
		reader.close();
		if(kerningFile.exists() && kerningFile.isFile())
		{
			reader = new BufferedReader(new FileReader(kerningFile));
			line = null;
			while((line = reader.readLine()) != null && line.length() > 0 && !line.startsWith("#"))
			{
				String[] content = line.split(" ");
				IntObject object = textureArray.get(parseInt(content[1]));
				if(object.kerning == null)
				{
					object.kerning = new Kerning();
				}
				object.kerning.add(textureArray.get(parseInt(content[0])), Float.parseFloat(content[2]) / largestWidth);
			}
		}
		return image;
	}

	public IntObject getCharData(char letter)
	{
		if(textureArray.containsKey((int) letter))
			return textureArray.get((int) letter);
		return null;
	}

	public IntObject getCharData(int letter)
	{
		if(textureArray.containsKey(letter))
			return textureArray.get(letter);
		return null;
	}

	/**
	 * Simple form of drawing a string, will draw left->right
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 */
	public void drawString(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		drawChars(whatchars, x, y, z, pointFont);
	}
	
	public void drawUpString(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
	    drawChars(whatchars, x, y - pointFont, z, pointFont);
	}

	/**
	 * Draw an aligned string
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 */
	public void drawAlignedString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment)
	{
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont);
	}

	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawFittedString(CharSequence whatchars, double x, double y, double z, double width, double height)
	{
		drawChars(whatchars, x, y, z, Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars)));
	}

	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawAlignedFittedString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height)
	{
		double pointFont = Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont);
	}

	/**
	 * Draw a color shifted string from left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param shiftToColor : ending color on the right
	 */
	public void drawColorString(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}

	/**
	 * Draw a color shifted, aligned string with left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param shiftToColor : ending color on the right
	 */
	public void drawAlignedCString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment, Color4d shiftToColor)
	{
		switch(alignment)
		{
		case 1:
			x -= getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x -= getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}

	/**
	 * Draw a color shifted, aligned, and fitted string with left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level 
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param shiftToColor : ending color on the right
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawAlignedFCString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height, Color4d shiftToColor)
	{
		double pointFont = Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			x -= getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x -= getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}

	private void drawChars(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		IntObject letter;
		int i;
		double width;
		double xOffset;
		double yOffset;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				xOffset = letter.getXOffset() * pointFont;
				yOffset = letter.getYOffset() * pointFont;
				if(whatchars.charAt(i) != ' ')
					Draw.rectUV(x + xOffset, y + yOffset, x + width + xOffset, y + letter.getHeight() * pointFont + yOffset, z, letter.getTexture());
				x += letter.getAdvance() * pointFont;
			}
		}
		glEnd();
	}

	private void drawChars(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		double textWidth = getTextWidth(whatchars, pointFont);
		double redShift = (shiftToColor.getRed() - getRed()) / textWidth;
		double greenShift = (shiftToColor.getGreen() - getGreen()) / textWidth;
		double blueShift = (shiftToColor.getBlue() - getBlue()) / textWidth;
		double alphaShift = (shiftToColor.getAlpha() - getAlpha()) / textWidth;

		IntObject pastLetter = null;
		IntObject letter;
		int i;
		double width = 0;
		double xOffset;
		double yOffset;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				xOffset = letter.getXOffset() * pointFont;
				if(letter.getKerning() != null && pastLetter != null)
				{
					xOffset += letter.getKerning().get(pastLetter) * pointFont;
				}
				yOffset = letter.getYOffset() * pointFont;
				if(whatchars.charAt(i) != ' ')
					Draw.colorHRectUV(x + xOffset, y + yOffset, x + width + xOffset, y + letter.getHeight() * pointFont + yOffset, z, letter.getTexture(), getRed() + (redShift * width), getGreen() + (greenShift * width), getBlue() + (blueShift * width), getAlpha() + (alphaShift * width));
				x += letter.getAdvance() * pointFont;
				pastLetter = letter;
			}
		}
		glEnd();
	}

	/**
	 * Gets the width of the text (without any point font multiplication)
	 * @param whatchars : contains the content to be written
	 * @return total width of whatchars with a <b>double return type</b>
	 */
	public double getTextWidth(CharSequence whatchars)
	{
		double totalWidth = 0;
		IntObject letter = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				totalWidth += letter.getAdvance();
			}
			else
			{
				return 0;
			}
		}
		return totalWidth;
	}

	/**
	 * Gets the width of the text
	 * @param whatchars : contains the content to be written
	 * @param pointFont : size of characters
	 * @return total width of whatchars * pointFont, <b>double return type</b>
	 */
	public double getTextWidth(CharSequence whatchars, double pointFont)
	{
		return getTextWidth(whatchars) * pointFont;
	}

	public double getTextHeight(CharSequence whatchars)
	{
		double largestHeight = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				largestHeight = Math.max(intObject.getHeight() + intObject.getYOffset(), largestHeight);
			}
		}
		return largestHeight;
	}

	public double getTextHeight(CharSequence whatchars, double pointFont)
	{
		return getTextHeight(whatchars) * pointFont;
	}

	private static class IntObject
	{
		private Texture texture;
		private Kerning kerning;
		private float width;
		private float height;
		private float xOffset;
		private float yOffset;
		private float xadvance;

		public Texture getTexture()
		{
			return texture;
		}

		public float getWidth()
		{
			return width;
		}

		public float getHeight()
		{
			return height;
		}

		public float getXOffset()
		{
			return xOffset;
		}

		public float getYOffset()
		{
			return yOffset;
		}

		public float getAdvance()
		{
			return xadvance;
		}
		
		public Kerning getKerning()
		{
			return kerning;
		}
	}

	private static class Kerning
	{

		private Map<Integer, Float> kernings = new HashMap<Integer, Float>();

		public void add(IntObject otherChar, float adjustment)
		{
			kernings.put(otherChar.hashCode(), adjustment);
		}

		public float get(IntObject otherChar)
		{
			if(kernings.containsKey(otherChar.hashCode()))
				return kernings.get(otherChar.hashCode());
			return 0;
		}

	}

	@Override
	public Texture getTexture() 
	{
		return null;
	}

	@Override
	public int getTextureID() 
	{
		return textureId;
	}

	@Override
	public void setTextureID(int id)
	{
		textureId = id;
	}

}
