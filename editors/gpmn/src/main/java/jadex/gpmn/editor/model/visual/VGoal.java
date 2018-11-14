package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 *  Visual representation of a goal.
 *
 */
public class VGoal extends VNode implements IPlanModeProvider
{
	/**
	 *  Creates a new visual goal element and goal element in the model.
	 */
	public VGoal(IGoal goal, mxPoint position)
	{
		super(goal, new mxGeometry(position.getX(), position.getY(),
							 GuiConstants.DEFAULT_GOAL_WIDTH, 
							 GuiConstants.DEFAULT_GOAL_HEIGHT),
							 ModelConstants.ACHIEVE_GOAL_TYPE);
		insert(new VGoalType());
		insert(new SequentialMarker(this));
	}
	
	/**
	 *  Gets the underlying goal.
	 *  @return The goal.
	 */
	public IGoal getGoal()
	{
		return (IGoal) super.getValue();
	}
	
	/**
	 *  Sets the goal type.
	 */
	public void setGoalType(String type)
	{
		getGoal().setGoalType(type);
	}
	
	@Override
	public String getStyle()
	{
		return getGoal().getGoalType();
	}
	
	/**
	 *  Sets the value.
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		if (value instanceof String)
			getGoal().setName((String) value);
		else
			super.setValue(value);
	}
	
	/**
	 *  Gets the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return getGoal().getName();
	}
	
	/**
	 *  Override to match type marker.
	 */
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		VGoalType gt = ((VGoalType) getChildAt(0));
		if (gt != null)
			gt.setGeometry();
	}
	
	/**
	 *  Returns the business model node.
	 *  @return The node.
	 */
	public INode getNode()
	{
		return getGoal();
	}
	
	/**
	 *  Provides the mode of the associated plan.
	 *  @return Sequential, Parallel or null if undetermined.
	 */
	public String getPlanMode()
	{
		String mode = null;
		for (IEdge edge : getGoal().getSourceEdges())
		{
			if (edge.getTarget() instanceof IActivationPlan)
			{
				IActivationPlan aplan = (IActivationPlan) edge.getTarget();
				if (mode != null)
				{
					if (!aplan.getMode().equals(mode))
					{
						mode = null;
						break;
					}
				}
				else
				{
					mode = aplan.getMode();
				}
				
			}
		}
		
		return mode;
	}
	
	/** 
	 * Goal Type Marker
	 */
	public class VGoalType extends mxCell
	{
		public VGoalType()
		{
			super(null, new mxGeometry(), GuiConstants.GOAL_TYPE_STYLE);
			
			setGeometry();
			setConnectable(false);
			setVisible(true);
			setVertex(true);
			
			setId(VGoal.this.getId() + "_Type");
		}
		
		public void setGeometry()
		{
			mxGeometry geo = new mxGeometry(0, 0, GuiConstants.GOAL_MARKER_WIDTH,
												  GuiConstants.GOAL_MARKER_HEIGHT);
			geo.setRelative(true);
			double gw = VGoal.this.getGeometry().getWidth();
			geo.setOffset(new mxPoint((gw - GuiConstants.GOAL_MARKER_WIDTH) / 2.0, 0));
			setGeometry(geo);
		}
	}
}
