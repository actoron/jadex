package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public class ColorLayerJ2DRenderer implements ILayerJ2DRenderer
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
		
		Color c = layer.getColor() instanceof Color? (Color)layer.getColor(): (Color)SObjectInspector.getProperty(persp, (String)layer.getColor(), "$perspective", 
				vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		g.setColor(c);
		if(c==null)
			c=Color.WHITE;
		Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, areaSize.getXAsDouble(), areaSize.getYAsDouble());
		g.fill(r);
	}
}
