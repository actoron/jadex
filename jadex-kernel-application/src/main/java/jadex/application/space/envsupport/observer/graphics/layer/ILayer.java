package jadex.application.space.envsupport.observer.graphics.layer;

import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.observer.graphics.ViewportJ2D;
import jadex.application.space.envsupport.observer.graphics.ViewportJOGL;
import jadex.application.space.envsupport.observer.perspective.IPerspective;

import java.awt.Graphics2D;
import java.io.Serializable;

import javax.media.opengl.GL;


/**
 * A full-screen layer interface for viewports.
 */
public interface ILayer extends Serializable
{
	/**
	 * Initializes the layer for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g);

	/**
	 * Initializes the layer for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl);
	
	/**
	 * Draws the layer to a Java2D viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IPerspective persp, IVector2 areaSize, ViewportJ2D vp, Graphics2D g);

	/**
	 * Draws the layer to an OpenGL viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(IPerspective persp, IVector2 areaSize, ViewportJOGL vp, GL gl);
}
