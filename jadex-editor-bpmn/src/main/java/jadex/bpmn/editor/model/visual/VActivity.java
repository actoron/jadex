package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

public class VActivity extends VNamedNode
{
	/**
	 * Creates a new activity.
	 * 
	 * @param graph The graph where this element is used.
	 */
	public VActivity(mxGraph graph)
	{
		super(graph, VActivity.class.getSimpleName());
		setValue("");
	}
	
	/**
	 *  Gets the style.
	 */
	public String getStyle()
	{
		String ret = VActivity.class.getSimpleName() + "_";
		if (getBpmnElement() != null)
		{
			String at = ((MActivity) getBpmnElement()).getActivityType();
			if (at.startsWith("Event"))
			{
				ret += EventShape.class.getSimpleName();
				if (at.startsWith("EventStart"))
				{
					ret += "_START";
				}
				else if (at.startsWith("EventIntermediate"))
				{
					ret += "_INTERMEDIATE";
				}
				else
				{
					ret += "_END";
				}
			}
			else
			{
				ret += at;
			}
		}
		else
		{
			ret +=  MBpmnModel.TASK;
		}
		return ret;
	}
	
	/**
	 *  Sets the parent.
	 */
	public void setParent(mxICell parent)
	{
		MActivity mactivity = (MActivity) getBpmnElement();
		if (mactivity != null)
		{
			if (getParent() != null)
			{
				VNode oldparent = (VNode) getParent();
				if (oldparent instanceof VLane)
				{
					((MLane) ((VLane) oldparent).getBpmnElement()).removeActivity(mactivity);
					mactivity.setLane(null);
					mactivity.setPool(null);
				}
				else
				{
					((MPool) ((VPool) oldparent).getBpmnElement()).removeActivity(mactivity);
					mactivity.setPool(null);
				}
			}
			if (parent != null)
			{
				if (parent instanceof VLane)
				{
					((MLane) ((VLane) parent).getBpmnElement()).addActivity(mactivity);
					mactivity.setLane((MLane) ((VLane) parent).getBpmnElement());
					mactivity.setPool((MPool) ((VLane) parent).getPool().getBpmnElement());
				}
				else
				{
					((MPool) ((VPool) parent).getBpmnElement()).addActivity(mactivity);
					mactivity.setPool((MPool) ((VPool) parent).getBpmnElement());
				}
			}
		}
		super.setParent(parent);
	}
}
