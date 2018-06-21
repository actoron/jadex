package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.ModulateComposite;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.graphics.layer.TiledLayer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public class TiledLayerJ2DRenderer implements ILayerJ2DRenderer
{
	/**
	 * Draws the layer.
	 * @param persp the Perspective
	 * @param layer the layer being drawn
	 * @param areaSize the area size
	 * @param vp the viewport
	 */
	public void draw(IPerspective persp, Layer layer, IVector2 areaSize, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		TiledLayer tl = (TiledLayer) layer;
		BufferedImage image;
		try
		{
			image = (BufferedImage) layer.getRenderInfo(0);
		}
		catch (Exception e)
		{
			image = vp.getImage(tl.getTexturePath());
			tl.setRenderInfo(0, image);
			//imageToUser_ = new AffineTransform();
			//imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_.getHeight());
		}
		
		Composite c = g.getComposite();
		if (!Color.WHITE.equals(layer.getColor()))
		{
			final Color color = layer.getColor() instanceof Color? (Color)layer.getColor(): (Color)SObjectInspector.getProperty(persp, (String)layer.getColor(), "$perspective", 
				vp.getPerspective().getObserverCenter().getSpace().getFetcher());
			g.setComposite(new ModulateComposite()
			{
				protected Color getColor()
				{
					return color;
//					return (Color) SObjectInspector.getPropertyAsClass(propObject, modColor_, Color.class);
				}
			});
		}
		
		for(double x = 0.0; x < areaSize.getXAsDouble(); x = x
				+ tl.getTileSize().getXAsDouble())
		{
			for(double y = 0.0; y < areaSize.getYAsDouble(); y = y
					+ tl.getTileSize().getYAsDouble())
			{
				AffineTransform transform = g.getTransform();
				g.translate(x, y);
				g.scale(tl.getTileSize().getXAsDouble(), tl.getTileSize().getYAsDouble());
				g.drawImage(image, vp.getImageTransform(image.getWidth(), image.getHeight()), null);
				g.setTransform(transform);
			}
		}
		
		g.setComposite(c);
	}
}
