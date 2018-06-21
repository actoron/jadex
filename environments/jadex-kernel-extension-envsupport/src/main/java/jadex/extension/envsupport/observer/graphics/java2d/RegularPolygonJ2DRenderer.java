package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.drawable.RegularPolygon;

public class RegularPolygonJ2DRenderer extends AbstractJ2DRenderer
{
	public RegularPolygonJ2DRenderer()
	{
		
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
		GeneralPath shape;
		try
		{
			shape = (GeneralPath) primitive.getRenderInfo(0);
		}
		catch (Exception e)
		{
			int vertices = ((RegularPolygon) primitive).getVertexCount();
			
			shape = new GeneralPath();
			shape.moveTo(0.5f, 0.0f);
			for(int i = 1; i < vertices; ++i)
			{
				double x = Math.PI * 2 / vertices * i;
				shape.lineTo((float)(Math.cos(x) / 2.0),
							 (float)(Math.sin(x) / 2.0));
			}
			shape.closePath();
			
			primitive.setRenderInfo(0, shape);
		}
		
		Graphics2D g = vp.getContext();
		if(!setupMatrix(dc, primitive, obj, g, vp))
			return;
		Color c = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		if(c==null)
			c = Color.WHITE;
		g.setColor(c);
		g.fill(shape);
	}
}
