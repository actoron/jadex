package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.layer.GridLayer;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public class GridLayerJ2DRenderer implements ILayerJ2DRenderer
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
		Color c = layer.getColor() instanceof Color? (Color)layer.getColor(): (Color)SObjectInspector.getProperty(persp, (String)layer.getColor(), "$perspective", 
				vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		
		Graphics2D g = vp.getContext();
		
		g.setColor(c);
		
		IVector2 pixSize = vp.getPixelSize();
		
		IVector2 step = areaSize.copy().subtract(pixSize).divide(areaSize.copy().divide(((GridLayer) layer).getGridSize()));
		
		for (float x = 0.0f; x < areaSize.getXAsFloat(); x = x + step.getXAsFloat())
		{
			Rectangle2D.Float r = new Rectangle2D.Float(x, 0.0f, pixSize.getXAsFloat(), areaSize.getYAsFloat());
			g.fill(r);
		}
		
		for (float y = 0.0f; y < areaSize.getYAsFloat(); y = y + step.getYAsFloat())
		{
			Rectangle2D.Float r = new Rectangle2D.Float(0.0f, y, areaSize.getXAsFloat(), pixSize.getYAsFloat());
			g.fill(r);
		}
	}
}
