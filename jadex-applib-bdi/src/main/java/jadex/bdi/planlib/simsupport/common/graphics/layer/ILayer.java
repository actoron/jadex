package jadex.bdi.planlib.simsupport.common.graphics.layer;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.awt.Graphics2D;
import java.io.Serializable;

import javax.media.opengl.GL;

/** A full-screen layer interface for viewports.
 */
public interface ILayer extends Serializable
{
	/** Draws the layer to a Java2D viewport
	 * 
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g);
	
	/** Draws the layer to an OpenGL viewport
     * 
     * @param areaSize size of the area this layer covers
     * @param vp the viewport
     * @param gl OpenGL context
     */
	public void draw(IVector2 areaSize, ViewportJOGL vp, GL gl);
	
	/** Provides a copy of the layer.
     */
	public ILayer copy();
}
