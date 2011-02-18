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
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedOvalFigure;

import java.util.HashSet;
import java.util.Set;

import javax.swing.plaf.FontUIResource;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.swt.graphics.Image;


public abstract class GoalFigure extends ShadowedOvalFigure /*ShadowedRoundedRectangleFigure*/ {
	
	/**
	 * Corner angle to use for Goals with RoundedRectangle
	 */
	public static final int GOAL_CORNER_ANGLE = 80;

	/**
	 * Internal goal type field to apply goal shape marker
	 */
	private int goalType;
	
	/**
	 * Modes of the attached activation plans.
	 */
	private Set modeTypes;
	
	/** indicate the goal as recurring */
	private boolean isRecur;
	
	
	/**
	 * Default Constructor
	 */
	public GoalFigure()
	{
		super(/*GOAL_CORNER_ANGLE, */GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.Goal_2004));
		
		// default to AchieveGoal
		this.goalType = GoalType.ACHIEVE_GOAL_VALUE;
		
		modeTypes = new HashSet();
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);

		//GpmnShapePainter.paintTypeImageInFigure(graphics, super.getInnerPaintBounds(), this, getTypeImage());
		
		{
			String ts = null;
			switch(goalType)
			{
				case GoalType.MAINTAIN_GOAL_VALUE:
					ts = "M";
					break;
				case GoalType.PERFORM_GOAL_VALUE:
					ts = "P";
					break;
				case GoalType.QUERY_GOAL_VALUE:
					ts = "Q";
					break;
				case GoalType.ACHIEVE_GOAL_VALUE:
				default:
					ts = "A";
			}
			
			PrecisionRectangle bounds = new PrecisionRectangle();
			bounds.setX(getBounds().getCenter().preciseX() - (FigureUtilities.getTextWidth(ts, graphics.getFont()) / 2.0) - 4.0);
			bounds.setY(getBounds().preciseY());
			bounds.setWidth(FigureUtilities.getTextWidth(ts, graphics.getFont()) + 8.0);
			bounds.setHeight(graphics.getFontMetrics().getAscent() + 4.0);
			
			GpmnShapePainter.paintCenteredString(graphics, bounds, ts);
			graphics.drawRectangle(bounds);
		}
		
		if (modeTypes.size() == 1 && modeTypes.contains(ModeType.SEQUENTIAL))
			GpmnShapePainter.paintModeOrderedInsideFigure(graphics, bounds, this);
	}
	
	
	/**
	 * Create the background image for the goal type
	 * @return The type background image for the current {@link GoalType}
	 */
	private Image getTypeImage()
	{
		String imageURI = "";

		switch (goalType)
		{
			case GoalType.ACHIEVE_GOAL_VALUE:
				imageURI = "AchieveGoalBackground.png";
				break;
			case GoalType.MAINTAIN_GOAL_VALUE:
				imageURI = "MaintainGoalBackground.png";
				break;
			case GoalType.PERFORM_GOAL_VALUE:
				imageURI = "PerformGoalBackground.png";
				break;

			default:
				imageURI = "SimpleGoalBackground.png";
		}

		return GpmnShapePainter.getBackgroundImage(imageURI);
	}

	// -------- model getter / setter ---------
	
	/**
	 * Sets the goal type over the figure, so that it will be painted properly.
	 * 
	 * @param type
	 *            one of the GoalType literal values.
	 * 
	 * @generated NOT
	 */
	public void setGoalType(String type)
	{
		GoalType gt = GoalType.get(type);

		if (gt == null)
		{
			goalType = GoalType.ACHIEVE_GOAL_VALUE;
		}
		else
		{
			goalType = gt.getValue();
		}
		revalidate();
		repaint();
	}
    
	// -------- getter / setter ---------
	
	/**
	 * Get the GoalType this figure is displaying. 
	 * @return
	 */
	public int getGoalType()
	{
		return goalType;
	}
	
    /**
     * Gets the mode types.
	 * @return the modeTypes
	 */
	public Set getModeTypes()
	{
		return modeTypes;
	}

	/**
	 * Sets the mode types.
	 * @param modeTypes the modeType to set
	 */
	public void setModeTypes(Set modeTypes)
	{
		this.modeTypes = modeTypes;
	}

	/**
     * @return the isRecur
     */
    public boolean isRecur() {
        return isRecur;
    }

    /**
     * @param isRecur
     *            the isRecur to set
     */
    public void setRecur(boolean isRecur) {
        this.isRecur = isRecur;
        revalidate();
        repaint();
    }

    // ---- Abstract method declaration ----
    
    /**
     * Abstract getter to support static GoalEditPartSupport methods
     * @return {@link WrappingLabel}
     */
    public abstract WrappingLabel getFigureGoalNameFigure();
    
}
