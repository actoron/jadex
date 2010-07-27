package jadex.application.space.envsupport.observer.graphics.layer;

import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.observer.graphics.ViewportJ2D;
import jadex.application.space.envsupport.observer.graphics.ViewportJOGL;
import jadex.application.space.envsupport.observer.gui.SObjectInspector;
import jadex.application.space.envsupport.observer.perspective.IPerspective;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;

/**
 * Simple plain color layer.
 */
public class ColorLayer implements ILayer
{
	/** The color or color binding of the layer. */
	private Object color;
	
	/**
	 * Creates a new ColorLayer.
	 * @param color color the layer should have, or color binding
	 */
	public ColorLayer(Object color)
	{
		this.color = color;
	}
	
	/**
	 * Initializes the layer for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g)
	{
	}

	/**
	 * Initializes the layer for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
	}
	
	/**
	 * Draws the layer to a Java2D viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IPerspective persp, IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		Color c = color instanceof Color? (Color)color: (Color)SObjectInspector.getProperty(persp, (String)color, "$perspective", 
			vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		g.setColor(c);
		Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, areaSize.getXAsDouble(), areaSize.getYAsDouble());
		g.fill(r);
	}

	/**
	 * Draws the layer to an OpenGL viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(IPerspective persp, IVector2 areaSize, ViewportJOGL vp, GL gl)
	{
		Color c = color instanceof Color? (Color)color: (Color)SObjectInspector.getProperty(persp, (String)color, "$perspective", 
			vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		
//		if(c==null)
//			System.out.println("here");
		
		gl.glColor4fv(c.getComponents(null), 0);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex2f(0.0f, 0.0f);
		gl.glVertex2f(0.0f, areaSize.getYAsFloat());
		gl.glVertex2f(areaSize.getXAsFloat(), areaSize.getYAsFloat());
		gl.glVertex2f(areaSize.getXAsFloat(), 0.0f);
		gl.glEnd();
	}
}
