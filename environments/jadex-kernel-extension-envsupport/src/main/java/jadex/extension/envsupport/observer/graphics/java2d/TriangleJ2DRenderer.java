package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

/**
 * 
 */
public class TriangleJ2DRenderer extends AbstractJ2DRenderer
{
	/** Triangle path for Java2D. */
	private static final GeneralPath J2D_TRIANGLE = new GeneralPath();
	static
	{
		J2D_TRIANGLE.moveTo(0.0f, 0.5f);
		J2D_TRIANGLE.lineTo((float)-(0.25 * Math.sqrt(3)), -0.25f);
		J2D_TRIANGLE.lineTo((float)(0.25 * Math.sqrt(3)), -0.25f);
		J2D_TRIANGLE.closePath();
	}
	
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
		AffineTransform transform = g.getTransform();
		if(!setupMatrix(dc, primitive, obj, g, vp))
			return;
		Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		g.setColor(c);
		g.fill(J2D_TRIANGLE);
		g.setTransform(transform);
	}
}
