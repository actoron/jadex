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

/**
 * 
 */
public abstract class BpmnPlanFigure extends ShadowedRoundedRectangleFigure
{

	public static final int PLAN_CORNER_ANGLE = 8;
	
	/** Flag to indicate that this plan is linked against a BPMN Plan */
	private boolean isLinked = false;

	public BpmnPlanFigure()
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
				.getTopTitleMarkerBounds(getInnerPaintBounds()), "Bpmn Plan");

		//GpmnShapePainter.paintCenteredString(graphics, getInnerPaintBounds(), "Plan");
		GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("BpmnPlanBackground.png"));
		
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
