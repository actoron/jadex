/**
 * 
 */
package jadex.tools.gpmn.figures;


import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @generated NOT
 */
public abstract class ContextFigure extends ShadowedRoundedRectangleFigure {

	public static final int CONTEXT_CORNER_ANGLE = 30;
	public static final String CONTEXT_LABEL_TEXT = "Context";

	public ContextFigure()
	{
		super(CONTEXT_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.Context_2011));
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);
		Rectangle innerBounds = getInnerPaintBounds();
		
		// add some special markers to shape
		Point pp1 = new Point(innerBounds.x, innerBounds.getBottom().y - innerBounds.height * 0.3 );
		Point pp2 = new Point(innerBounds.x + innerBounds.width, innerBounds.getBottom().y - innerBounds.height * 0.3 );
		int lineWidth = graphics.getLineWidth();
		graphics.setLineWidth(lineWidth *2);
		graphics.drawLine(pp1, pp2);
		graphics.setLineWidth(lineWidth);
		
		innerBounds.height = innerBounds.height/2;
		GpmnShapePainter.paintCenteredString(graphics, innerBounds, CONTEXT_LABEL_TEXT);

	}


}
