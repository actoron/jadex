package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.ModulateComposite;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.drawable.TexturedRectangle;

public class TexturedRectangleJ2DRenderer extends AbstractJ2DRenderer
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
		BufferedImage image;
		try
		{
			image = (BufferedImage) primitive.getRenderInfo(0);
		}
		catch (Exception e)
		{
			image = vp.getImage(((TexturedRectangle) primitive).getTexturePath());
			primitive.setRenderInfo(0, image);
		}
		
		Graphics2D g = vp.getContext();
		
		IVector2 size = (IVector2)dc.getBoundValue(obj, primitive.getSize(), vp);
		
		g.translate(-size.getXAsDouble() / 2.0, -size.getYAsDouble() / 2.0);
		if (!setupMatrix(dc, primitive, obj, g, vp))
			return;
		
		Color currentColor = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		if(currentColor==null)
			currentColor = Color.WHITE;
		final Color fcurrentColor = currentColor;
		
		if(!Color.WHITE.equals(currentColor))
		{
			ModulateComposite modComposite = new ModulateComposite()
			{
				protected Color getColor()
				{
					return fcurrentColor;
				}
			};
			
			Composite c = g.getComposite();
			g.setComposite(modComposite);
			g.drawImage(image, vp.getImageTransform(image.getWidth(), image.getHeight()), null);
			g.setComposite(c);
		}
		else
		{
			g.drawImage(image, vp.getImageTransform(image.getWidth(), image.getHeight()), null);
		}
	}
}
