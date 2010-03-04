
package jadex.tools.gpmn.figures;

import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedOvalFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public abstract class GoalFigure extends ShadowedOvalFigure /*ShadowedRoundedRectangleFigure*/ {

	/**
	 * Corner angle to use for Goals with RoundedRectangle
	 */
	public static final int GOAL_CORNER_ANGLE = 80;

	/**
	 * Internal goal type field to apply goal shape marker
	 */
	private int goalType = GoalType.META_GOAL_VALUE;
	
	/** indicate the goal as sequential */
    private boolean isGoalTypeSequential;
    
    /** indicate the goal as parallel */
	private boolean isGoalTypeParallel;
	
	/** indicate the goal as recurring */
	private boolean isRecur;
	
	/** indicate the goal subgoal order important */
	private boolean isSequentialOrderMode;
 
	/**
	 * Default Constructor
	 */
	public GoalFigure()
	{
		super(/*GOAL_CORNER_ANGLE, */GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.AchieveGoal_2002));
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);

		GpmnShapePainter.paintTypeImageInFigure(graphics, super.getInnerPaintBounds(), this, getTypeImage());
		
		// add some special goal markers to shape
		if (isSequentialOrderMode)
		{
			// FIXME: implement ordered marker
			GpmnShapePainter.paintModeOrderedInsideFigure(graphics, bounds, this);
		}
		
		// paint sequential or parallel marker
		if (isGoalTypeSequential)
		{
			GpmnShapePainter.paintLoopInsideFigure(graphics, bounds, this);
		}
		else if (isGoalTypeParallel)
		{
			GpmnShapePainter.paintParallelInsideFigure(graphics, bounds, this);
		}
		
//		switch (goalType)
//		{
//			case GoalType.SEQUENTIAL_GOAL_VALUE:
//				
//				break;
//			case GoalType.PARALLEL_GOAL_VALUE:
//				
//				break;
//			default:
//
//		}
		
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
		case GoalType.QUERY_GOAL_VALUE:
			imageURI = "QueryGoalBackground.png";
			break;
		case GoalType.META_GOAL_VALUE:
			imageURI = "MetaGoalBackground.png";
			break;
		case GoalType.PARALLEL_GOAL_VALUE:
		case GoalType.SEQUENTIAL_GOAL_VALUE:
		case GoalType.MESSAGE_GOAL_VALUE:
		case GoalType.SUB_PROCESS_GOAL_VALUE:
			imageURI = "SimpleGoalBackground.png";
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
			goalType = GoalType.META_GOAL_VALUE;
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
     * @return the isSequentialOrderMode
     */
    public boolean isSequentialOrderMode() {
        return isSequentialOrderMode;
    }

    /**
     * @param sequentialOrderMode
     *            the sequentialOrderMode to set
     */
    public void setSequentialOrderMode(boolean sequentialOrderMode) {
        this.isSequentialOrderMode = sequentialOrderMode;
        revalidate();
        repaint();
    }
    
     /**
     * @return the isGoalTypeSequential
     */
    public boolean isGoalTypeSequential() {
        return isGoalTypeSequential;
    }

    /**
     * @param isSequential
     *            the isSequential to set
     */
    public void setGoalTypeSequential(boolean isSequential) {
        this.isGoalTypeSequential = isSequential;
        revalidate();
        repaint();
    }
    
    /**
     * @return the isGoalTypeParallel
     */
    public boolean istGoalTypeParallel() {
        return isGoalTypeParallel;
    }

    /**
     * @param isGoalTypeParallel
     *            the isGoalTypeParallel to set
     */
    public void setGoalTypeParallel(boolean isParallel) {
        this.isGoalTypeParallel = isParallel;
        revalidate();
        repaint();
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
