package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MDataEdge;

/**
 *  Visual representation of a data edge.
 *
 */
public class VDataEdge extends VEdge
{
	/**
	 *  Creates a new visual representation of a data edge.
	 *  
	 *  @param graph The BPMN graph.
	 */
	public VDataEdge(mxGraph graph)
	{
		super(graph, VDataEdge.class.getSimpleName());
	}
	
	/**
	 *  Sets the source.
	 */
	public void setSource(mxICell source)
	{
		MDataEdge dedge = (MDataEdge) getBpmnElement();
		if (dedge != null)
		{
			if (getSource() != null)
			{
				VActivity vsrc = getSourceActivity();
				((MActivity) vsrc.getBpmnElement()).removeOutgoingDataEdge(dedge);
			}
			super.setSource(source);
			if (source != null)
			{
				VActivity vsrc = getSourceActivity();
				((MActivity) vsrc.getBpmnElement()).addOutgoingDataEdge(dedge);
				dedge.setSource(vsrc.getMActivity());
				
				if (getSource() instanceof VOutParameter)
				{
					String paramname = ((VOutParameter) getSource()).getParameter().getName();
					dedge.setSourceParameter(paramname);
				}
			}
		}
		else
		{
			super.setSource(source);
		}
	}
	
	/**
	 *  Sets the target.
	 */
	public void setTarget(mxICell target)
	{
		MDataEdge dedge = (MDataEdge) getBpmnElement();
		if (dedge != null)
		{
			if (getTarget() != null)
			{
				VActivity vtgt = getTargetActivity();
//				VActivity vtgt = (VActivity) getTarget().getParent();
				((BpmnGraph) getGraph()).delayedRefreshCellView(vtgt);
				((MActivity) vtgt.getBpmnElement()).removeIncomingDataEdge(dedge);
			}
			super.setTarget(target);
			if (target != null)
			{
				
				VActivity vtgt = getTargetActivity();
//				VActivity vtgt = (VActivity) getTarget().getParent();
				((BpmnGraph) getGraph()).delayedRefreshCellView(vtgt);
				((MActivity) vtgt.getBpmnElement()).addIncomingDataEdge(dedge);
				dedge.setTarget((MActivity) vtgt.getBpmnElement());
				
				if (getTarget() instanceof VInParameter)
				{
					String paramname = ((VInParameter) getTarget()).getParameter().getName();
					dedge.setTargetParameter(paramname);
				}
			}
		}
		else
		{
			super.setTarget(target);
		}
	}
	
	/**
	 *  Gets the authoritative edge parent. 
	 * 
	 * 	@return The parent.
	 */
	public mxICell getEdgeParent()
	{
		mxICell ret = null;
		if (getSource() != null)
		{
			if (getSource() instanceof VOutParameter)
			{
				if (getSource().getParent() != null)
				{
					ret = getSource().getParent().getParent();
				}
			}
			else
			{
				ret = getSource().getParent();
			}
		}
		return ret;
	}
	
	/**
	 *  Returns the source activity.
	 *  
	 *  @return The activity.
	 */
	protected VActivity getSourceActivity()
	{
		mxICell tmpvsrc = getSource();
		if (tmpvsrc instanceof VOutParameter)
		{
			tmpvsrc = tmpvsrc.getParent();
		}
		return (VActivity) tmpvsrc;
	}
	
	/**
	 *  Returns the target activity.
	 *  
	 *  @return The activity.
	 */
	protected VActivity getTargetActivity()
	{
		mxICell tmpvtgt = getTarget();
		if (tmpvtgt instanceof VInParameter)
		{
			tmpvtgt = tmpvtgt.getParent();
		}
		return (VActivity) tmpvtgt;
	}
}
