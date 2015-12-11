package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

public class RectangleJ2DRenderer extends AbstractJ2DRenderer
{
	/** Rectangle2D for Java2D. */
	private static final Rectangle2D.Double	J2D_RECTANGLE	= new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
	
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		if(!setupMatrix(dc, primitive, obj, g, vp))
			return;
		Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		if(c==null)
			c = Color.WHITE;
		g.setColor(c);
		g.fill(J2D_RECTANGLE);
	}
}
