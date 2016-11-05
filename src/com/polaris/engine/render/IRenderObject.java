/**
 * 
 */
package com.polaris.engine.render;


/**
 * @author lec50
 *
 */
public interface IRenderObject
{
	
	public void bind();
	public void enable();
	public void setupDraw();
	public void setupDrawEnable();
	public void draw();
	public void disable();
	public void destroy();
	
}
