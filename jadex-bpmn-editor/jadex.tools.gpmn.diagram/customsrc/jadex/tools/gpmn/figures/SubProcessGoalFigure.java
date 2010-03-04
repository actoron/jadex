
package jadex.tools.gpmn.figures;

import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;

/**
 * 
 */
public abstract class SubProcessGoalFigure extends ShadowedRoundedRectangleFigure {

	/**
	 * Corner angle to use for Goals with RoundedRectangle
	 */
	public static final int GOAL_CORNER_ANGLE = 60;

	/**
	 * Internal goal type field to apply goal shape marker
	 */
	private int goalType = GoalType.META_GOAL_VALUE;
 
	/** Flag to indicate that this goal is linked against a process diagram */
	private boolean isLinked = false;
	
	/**
	 * Default Constructor
	 */
	public SubProcessGoalFigure()
	{
		super(GOAL_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.SubProcessGoal_2009));
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);

		// paint static type title in figure
		GpmnShapePainter.paintCenteredString(graphics, GpmnShapePainter
				.getTopTitleMarkerBounds(getInnerPaintBounds()), "SubProcess");

		// paint background image
		GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("SimpleGoalBackground.png"));
		
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

    // ---- Abstract method declaration ----
    
    /**
     * Abstract getter to support static GoalEditPartSupport methods
     * @return {@link WrappingLabel}
     */
    public abstract WrappingLabel getFigureGoalNameFigure();
    
}
