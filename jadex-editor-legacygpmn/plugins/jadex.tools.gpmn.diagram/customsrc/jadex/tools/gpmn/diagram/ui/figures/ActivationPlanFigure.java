/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn.diagram.ui.figures;



import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 */
public abstract class ActivationPlanFigure extends ShadowedRoundedRectangleFigure
{
	public static final int PLAN_CORNER_ANGLE = 8;
	
	public static PrecisionRectangle HRECT_BOUNDS = new PrecisionRectangle();
	static
	{
		HRECT_BOUNDS.setWidth(16.0);
		HRECT_BOUNDS.setHeight(HRECT_BOUNDS.preciseWidth);
		HRECT_BOUNDS.setX(-HRECT_BOUNDS.preciseWidth);
		HRECT_BOUNDS.setY(-HRECT_BOUNDS.preciseWidth);
	}

	/** mode of the implicit plan */
    private boolean showPlanModeDecorator;
	
	public ActivationPlanFigure()
	{
		super(PLAN_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.BpmnPlan_2003));
	}
	

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);
		
		// paint static type title in figure
		GpmnShapePainter.paintCenteredString(graphics, GpmnShapePainter
				.getTopTitleMarkerBounds(getInnerPaintBounds()), "Activation Plan");

		//GpmnShapePainter.paintCenteredString(graphics, getInnerPaintBounds(), "Plan");
		/*GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("PlanBackground.png"));*/
		
		graphics.drawRoundRectangle(getHideBounds(), getCornerAngle(), getCornerAngle());// (getHideBounds());
		//graphics.drawRectangle(getHideBounds());
		GpmnShapePainter.paintMinus(graphics, getHideBounds(), 2.0f, 1.5f);
		
		// add some special markers to shape
		if (showPlanModeDecorator)
		{
			GpmnShapePainter.paintModeOrderedInsideFigure(graphics, bounds, this);
		}
		
	}

	
	
	// ---- getter / setter ----

	/** Returns the bounds of the hide rectangle
	 *  @return bounds of the hide rectangle
	 */
	public Rectangle getHideBounds()
	{
		return HRECT_BOUNDS.getCopy().translate(getOutlineBounds().getBottomRight());
	}
	
	/**
	 * Check the Mode decorator state 
	 * @return the showPlanModeDecorator flag
	 */
	public boolean isShowPlanModeDecorator()
	{
		return showPlanModeDecorator;
	}


	/**
	 * Set the Mode decorator state
	 * @param showPlanModeDecorator the showPlanModeDecorator flag to set
	 */
	public void setShowPlanModeDecorator(boolean showPlanModeDecorator)
	{
		this.showPlanModeDecorator = showPlanModeDecorator;
		revalidate();
		repaint();
	}


}
