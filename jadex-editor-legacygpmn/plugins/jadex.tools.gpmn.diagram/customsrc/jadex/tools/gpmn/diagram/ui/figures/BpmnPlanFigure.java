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



import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PrecisionRectangle;

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
		//GpmnShapePainter.paintCenteredString(graphics, GpmnShapePainter
				//.getTopTitleMarkerBounds(getInnerPaintBounds()), "Bpmn Plan");
		/*{
			PrecisionRectangle bounds = GpmnShapePainter.getTopTitleMarkerBounds(getInnerPaintBounds());
			bounds.setY(bounds.preciseY - graphics.getFontMetrics().getAscent() + 1.0);
			
			String bpmnstring = "BPMN";
			
			GpmnShapePainter.paintCenteredString(graphics, bounds, bpmnstring);
			
			bounds = GpmnShapePainter.getTopTitleMarkerBounds(getInnerPaintBounds());
			bounds.setX(bounds.getCenter().x - FigureUtilities.getTextWidth(bpmnstring, graphics.getFont())/2 - 2.0);
			bounds.setWidth(FigureUtilities.getTextWidth(bpmnstring, graphics.getFont()) + 3.0);
			bounds.setHeight(graphics.getFontMetrics().getAscent() + 4.0);
			bounds.setY(bounds.preciseY - (graphics.getFontMetrics().getAscent() / 2.0) - 2.0);
			graphics.drawRectangle(bounds);
		}*/
		
		{
			String bpmnstring = "BPMN";
			
			PrecisionRectangle bounds = new PrecisionRectangle();
			bounds.setX(getBounds().getCenter().preciseX() - (FigureUtilities.getTextWidth(bpmnstring, graphics.getFont()) / 2.0) - 4.0);
			bounds.setY(getBounds().preciseY());
			bounds.setWidth(FigureUtilities.getTextWidth(bpmnstring, graphics.getFont()) + 8.0);
			bounds.setHeight(graphics.getFontMetrics().getAscent() + 4.0);
			
			GpmnShapePainter.paintCenteredString(graphics, bounds, bpmnstring);
			
			graphics.drawRectangle(bounds);
		}

		//GpmnShapePainter.paintCenteredString(graphics, getInnerPaintBounds(), "Plan");
		/*GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("BpmnPlanBackground.png"));*/
		
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
