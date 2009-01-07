package jadex.bdi.planlib.simsupport.common.graphics.layer;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.awt.Graphics2D;
import java.io.Serializable;

import javax.media.opengl.GL;

/** A layer for displaying a grid.
 */
public class GridLayer implements ILayer
{
	private IVector2 gridSize_;
	
	public GridLayer(IVector2 gridSize)
	{
		gridSize_ = gridSize.copy();
	}
	
	/** Draws the layer to a Java2D viewport
	 * 
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		
	}
	
	/** Draws the layer to an OpenGL viewport
     * 
     * @param areaSize size of the area this layer covers
     * @param vp the viewport
     * @param gl OpenGL context
     */
	public void draw(IVector2 areaSize, ViewportJOGL vp, GL gl)
	{
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		gl.glBegin(gl.GL_LINES);
		for (float x = 0.0f; x <= areaSize.getXAsFloat(); x = x + gridSize_.getXAsFloat())
		{
			gl.glVertex2f(x, 0.0f);
			gl.glVertex2f(x, areaSize.getYAsFloat());
		}
		
		for (float y = 0.0f; y <= areaSize.getYAsFloat(); y = y + gridSize_.getYAsFloat())
		{
			gl.glVertex2f(0.0f, y);
			gl.glVertex2f(areaSize.getXAsFloat(), y);
		}
		gl.glEnd();
	}
	
	/** Provides a copy of the layer.
     */
	public ILayer copy()
	{
		return new GridLayer(gridSize_);
	}
}
