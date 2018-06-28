package jadex.gpmn.editor.gui.stylesheets;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.view.mxCellState;

import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VGoal.VGoalType;

public class GoalMarkerShape extends AbstractTextMarkerShape
{
	/** Goal type to indicator mapping. */
	public static final Map<String, String> TYPE_TO_INDICATOR = new HashMap<String, String>();
	static
	{
		TYPE_TO_INDICATOR.put(ModelConstants.ACHIEVE_GOAL_TYPE, "A");
		TYPE_TO_INDICATOR.put(ModelConstants.PERFORM_GOAL_TYPE, "P");
		TYPE_TO_INDICATOR.put(ModelConstants.MAINTAIN_GOAL_TYPE, "M");
		TYPE_TO_INDICATOR.put(ModelConstants.QUERY_GOAL_TYPE, "Q");
	}
	
	/**
	 *  Generates the shape.
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;

		return new Ellipse2D.Double(x, y, w, h);
	}
	
	/**
	 *  Returns the text for the marker.
	 *  
	 *  @param state The cell state.
	 *  @return Text of the marker.
	 */
	protected String getText(mxCellState state)
	{
		return TYPE_TO_INDICATOR.get(((VGoal) (((VGoalType) state.getCell()).getParent())).getGoal().getGoalType());
	}
}
