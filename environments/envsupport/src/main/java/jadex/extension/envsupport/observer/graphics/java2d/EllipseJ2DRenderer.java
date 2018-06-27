package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

public class EllipseJ2DRenderer extends AbstractJ2DRenderer
{
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp)
	{
		Ellipse2D.Double shape;
		try
		{
			shape = (Ellipse2D.Double) primitive.getRenderInfo(0);
		}
		catch (Exception e)
		{
			shape = new Ellipse2D.Double();
			shape.x = -0.5;
			shape.y = -0.5;
			shape.width = 1.0;
			shape.height = 1.0;
			
			primitive.setRenderInfo(0, shape);
		}
		
		Graphics2D g = vp.getContext();
		if(!setupMatrix(dc, primitive, obj, g, vp))
			return;
		Color c = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		if(c==null)
			c=Color.WHITE;
		g.setColor(c);
		g.fill(shape);
	}
}
