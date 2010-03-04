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
public abstract class PlanFigure extends ShadowedRoundedRectangleFigure
{

	public static final int PLAN_CORNER_ANGLE = 8;
	
	/** Flag to indicate that this plan is linked against a BPMN Plan */
	private boolean isLinked = false;

	public PlanFigure()
	{
		super(PLAN_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.Plan_2010));
	}

	

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);
		
		// paint static type title in figure
		GpmnShapePainter.paintCenteredString(graphics, GpmnShapePainter
				.getTopTitleMarkerBounds(getInnerPaintBounds()), "Plan");

		//GpmnShapePainter.paintCenteredString(graphics, getInnerPaintBounds(), "Plan");
		GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("PlanBackground.png"));
		
		// add some special plan markers to shape
		if (isLinked)
		{
			GpmnShapePainter.paintSubProcessMarkerInsideFigure(graphics,
					bounds, this);
		}
		else
		{
			GpmnShapePainter.paintUnsetSubProcessMarkerInsideFigure(graphics,
					bounds, this);
		}

	}

	// ----- getter / setter -----
	
	/**
	 * @return the isLinked
	 */
	public boolean isLinked()
	{
		return isLinked;
	}

	/**
	 * @param isLinked
	 *            the isLinked to set
	 */
	public void setLinked(boolean isLinked)
	{
		this.isLinked = isLinked;
		revalidate();
		repaint();
	}
	
	

}
