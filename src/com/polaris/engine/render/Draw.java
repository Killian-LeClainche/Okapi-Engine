package com.polaris.engine.render;

import static com.polaris.engine.render.OpenGL.getAlpha;
import static com.polaris.engine.render.OpenGL.getBlue;
import static com.polaris.engine.render.OpenGL.getColor;
import static com.polaris.engine.render.OpenGL.getGreen;
import static com.polaris.engine.render.OpenGL.getRed;
import static com.polaris.engine.render.OpenGL.glColor;
import static com.polaris.engine.render.OpenGL.glVertex;
import static com.polaris.engine.render.Texture.getTexture;
import static com.polaris.engine.util.Helper.TWOPI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.polaris.engine.util.Color4d;

public class Draw
{

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param thickness
	 * @param innerColor
	 */
	public static void rect(double x, double y, double x1, double y1, double z, double thickness, Color4d innerColor)
	{
		Color4d outerColor = new Color4d(getColor());

		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
		glVertex(x + thickness, y + thickness, z, innerColor);
		glVertex3d(x1 - thickness, y + thickness, z);

		glVertex3d(x1 - thickness, y + thickness, z);
		glVertex3d(x1 - thickness, y1 - thickness, z);
		glVertex(x1, y1, z, outerColor);
		glVertex3d(x1, y, z);

		glVertex3d(x, y1, z);
		glVertex3d(x1, y1, z);
		glVertex(x1 - thickness, y1 - thickness, z, innerColor);
		glVertex3d(x + thickness, y1 - thickness, z);

		glVertex3d(x + thickness, y1 - thickness, z);
		glVertex3d(x + thickness, y + thickness, z);
		glVertex(x, y, z, outerColor);
		glVertex3d(x, y1, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param color0
	 * @param color1
	 * @param color2
	 * @param color3
	 */
	public static void rect(double x, double y, double x1, double y1, double z, Color4d color0, Color4d color1, Color4d color2, Color4d color3)
	{
		glVertex(x, y1, z, color0);
		glVertex(x1, y1, z, color1);
		glVertex(x1, y, z, color2);
		glVertex(x, y, z, color3);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 */
	public static void rectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1)
	{
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1);
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 */
	public static void rectUV(double x, double y, double x1, double y1, double z)
	{
		Texture texture = getTexture();
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}
	
	public static void rectUV90(double x, double y, double x1, double y1, double z)
	{
		Texture texture = getTexture();
		glVertex(x, y, z, texture.getMinU(), texture.getMaxV());
		glVertex(x, y1, z, texture.getMaxU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMinV());
		glVertex(x1, y, z, texture.getMinU(), texture.getMinV());
	}
	
	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 */
	public static void rectUV(double x, double y, double x1, double y1, double z, int animationID)
	{
		Texture texture = getTexture();
		glVertex(x, y, z, texture.getMinU(animationID), texture.getMinV(animationID));
		glVertex(x, y1, z, texture.getMinU(animationID), texture.getMaxV(animationID));
		glVertex(x1, y1, z, texture.getMaxU(animationID), texture.getMaxV(animationID));
		glVertex(x1, y, z, texture.getMaxU(animationID), texture.getMinV(animationID));
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 */
	public static void rectUV(double x, double y, double x1, double y1, double z, Texture texture)
	{
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 */
	public static void rectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1);
		glVertex(x1, y, z, u1, v);
	}
	
	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void colorHRect(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		glVertex3d(x, y, z);
		glVertex3d(x, y1, z);
		glVertex(x1, y1, z, shiftToColor);
		glVertex3d(x1, y, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRect(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		glVertex3d(x, y, z);
		glVertex3d(x, y1, z);
		glVertex(x1, y1, z, r, g, b, a);
		glVertex3d(x1, y, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void colorVRect(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
		glVertex(x, y1, z, shiftToColor);
		glVertex3d(x1, y1, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRect(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
		glVertex(x, y1, z, r, g, b, a);
		glVertex3d(x1, y1, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param shiftToColor
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, shiftToColor);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param shiftToColor
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, int animationID, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, shiftToColor);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param shiftToColor
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param shiftToColor
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, Color4d shiftToColor)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, shiftToColor);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, r, g, b, a);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, int animationID, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, r, g, b, a);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV());
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, double r, double g, double b, double a)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1);
		glVertex(x1, y1, z, u1, v1, r, g, b, a);
		glVertex(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param shiftToColor
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, shiftToColor);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param shiftToColor
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, int animationID, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, shiftToColor);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param shiftToColor
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param shiftToColor
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, Color4d shiftToColor)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, shiftToColor);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, r, g, b, a);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, int animationID, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, r, g, b, a);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		glVertex(x1, y, z, texture.getMaxU(), texture.getMinV());
		glVertex(x, y, z, texture.getMinU(), texture.getMinV());
		glVertex(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		glVertex(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, double r, double g, double b, double a)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		glVertex(x1, y, z, u1, v);
		glVertex(x, y, z, u, v);
		glVertex(x, y1, z, u, v1, r, g, b, a);
		glVertex(x1, y1, z, u1, v1);
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param thickness
	 */
	public static void arc(double circleX, double circleY, double z, double radius, int lineCount, double thickness)
	{
		arc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness);
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param startAngle
	 * @param endAngle
	 * @param lineCount
	 * @param thickness
	 */
	public static void arc(double circleX, double circleY, double z, double radius, double startAngle, double endAngle, int lineCount, double thickness)
	{
		double theta = (endAngle - startAngle) / (lineCount);
		double x = radius * cos(startAngle);
		double y = radius * sin(startAngle);
		for(int i = 0; i < lineCount; i++)
		{
			glVertex3d(circleX + x, circleY + y, z);
			glVertex3d(circleX + (radius - thickness) * cos(startAngle), circleY + (radius - thickness) * sin(startAngle), z);
			startAngle += theta;
			x = radius * cos(startAngle);
			y = radius * sin(startAngle);
			glVertex3d(circleX + (radius - thickness) * cos(startAngle), circleY + (radius - thickness) * sin(startAngle), z);
			glVertex3d(circleX + x, circleY + y, z);
		}
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param thickness
	 * @param endColor
	 */
	public static void arc(double circleX, double circleY, double z, double radius, int lineCount, double thickness, Color4d endColor)
	{
		arc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness, endColor);
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param angle0
	 * @param angle
	 * @param lineCount
	 * @param thickness
	 * @param endColor
	 */
	public static void arc(double circleX, double circleY, double z, double radius, double angle0, double angle, int lineCount, double thickness, Color4d endColor)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double x = radius * cos(angle0);
		double y = radius * sin(angle0);
		double rShift = (endColor.getRed() - getRed()) / lineCount;
		double gShift = (endColor.getGreen() - getGreen()) / lineCount;
		double bShift = (endColor.getBlue() - getBlue()) / lineCount;
		double aShift = (endColor.getAlpha() - getAlpha()) / lineCount;
		for(int i = 0; i < lineCount; i++)
		{
			glVertex3d(circleX + x, circleY + y, z);
			glVertex3d(circleX + (radius - thickness) * cos(angle0), circleY + (radius - thickness) * sin(angle0), z);
			angle0 += deltaTheta;
			x = radius * cos(angle0);
			y = radius * sin(angle0);
			glColor(getRed() + rShift, getGreen() + gShift, getBlue() + bShift, getAlpha() + aShift);
			glVertex3d(circleX + (radius - thickness) * cos(angle0), circleY + (radius - thickness) * sin(angle0), z);
			glVertex3d(circleX + x, circleY + y, z);
		}
		glColor(getRed() - rShift * lineCount, getGreen() - gShift * lineCount, getBlue() - bShift * lineCount, getAlpha() - aShift * lineCount);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 */
	public static void circle(double circleX, double circleY, double z, double radius, int lineCount)
	{
		circle(circleX, circleY, z, radius, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
	public static void circle(double circleX, double circleY, double z, double radius, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		GL11.glBegin(GL_TRIANGLE_FAN);
		glVertex3d(circleX, circleY, z); 
		for(int i = 0; i <= lineCount; i++) 
		{ 
			glVertex3d(circleX + (radius * cos((angle -= deltaTheta))), circleY + (radius * sin(angle)), z);
		}
		glEnd();
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param innerColor
	 */
	public static void circle(double circleX, double circleY, double z, double radius, int lineCount, Color4d innerColor)
	{
		circle(circleX, circleY, z, radius, lineCount, 0, TWOPI, innerColor);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 * @param innerColor
	 */
	public static void circle(double circleX, double circleY, double z, double radius, int lineCount, double angle0, double angle, Color4d innerColor)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		Color4d color = new Color4d(getColor());
		GL11.glBegin(GL_TRIANGLE_FAN);
		glColor(innerColor);
		glVertex3d(circleX, circleY, z);
		glColor(color);
		for(int i = 0; i <= lineCount; i++)
		{
			glVertex3d(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z);
		}
		glEnd();
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param lineCount
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int lineCount)
	{
		circle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param animationID
	 * @param lineCount
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int animationID, int lineCount)
	{
		circle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), animationID, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param lineCount
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int lineCount)
	{
		circle(circleX, circleY, z, radius, u, v, u1, v1, texture, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param animationID
	 * @param lineCount
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int animationID, int lineCount)
	{
		circle(circleX, circleY, z, radius, u, v, u1, v1, texture, animationID, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double difU = (u1 - u) / 2;
		double difV = (v1 - v) / 2;
		double centerU = u + difU;
		double centerV = v + difV;
		GL11.glBegin(GL_TRIANGLE_FAN);
		glVertex(circleX, circleY, z, centerU, centerV);
		for(int i = 0; i <= lineCount; i++)
		{
			glVertex(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z, centerU + difU * cos(angle), centerV + difV * sin(angle), texture);
		}
		glEnd();
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param animationID
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
	public static void circle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int animationID, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double difU = (u1 - u) / 2;
		double difV = (v1 - v) / 2;
		double centerU = u + difU;
		double centerV = v + difV;
		GL11.glBegin(GL_TRIANGLE_FAN);
		glVertex(circleX, circleY, z, centerU, centerV);
		for(int i = 0; i <= lineCount; i++)
		{
			glVertex(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z, centerU + difU * cos(angle), centerV + difV * sin(angle), texture, animationID);
		}
		glEnd();
	}
	
}
