/*
 * 
 */
package jadex.tools.gpmn.figures;



import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.Graphics;

/**
 * 
 */
public abstract class GenericFigure extends ShadowedRoundedRectangleFigure
{

	public static final int GENERIC_CORNER_ANGLE = 1;
	

	public GenericFigure()
	{
		super(GENERIC_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.GenericGpmnElement_2014));
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);
		
		//GpmnShapePainter.paintCenteredString(graphics, getInnerPaintBounds(), "Plan");
		GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("GenericBackground.png"));

	}

}
