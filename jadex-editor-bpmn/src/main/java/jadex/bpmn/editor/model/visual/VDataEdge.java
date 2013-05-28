package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MDataEdge;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

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
				VActivity vsrc = (VActivity) getSource().getParent();
				((MActivity) vsrc.getBpmnElement()).removeOutgoingDataEdge(dedge);
			}
			super.setSource(source);
			if (source != null)
			{
				VActivity vsrc = (VActivity) getSource().getParent();
				((MActivity) vsrc.getBpmnElement()).addOutgoingDataEdge(dedge);
				dedge.setSource((MActivity) vsrc.getBpmnElement());
				String paramname = ((VOutParameter) getSource()).getParameter().getName();
				dedge.setSourceParameter(paramname);
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
				VActivity vtgt = (VActivity) getTarget().getParent();
				((BpmnGraph) getGraph()).delayedRefreshCellView(vtgt);
				((MActivity) vtgt.getBpmnElement()).removeIncomingDataEdge(dedge);
			}
			super.setTarget(target);
			if (target != null)
			{
				VActivity vtgt = (VActivity) getTarget().getParent();
				((BpmnGraph) getGraph()).delayedRefreshCellView(vtgt);
				((MActivity) vtgt.getBpmnElement()).addIncomingDataEdge(dedge);
				dedge.setTarget((MActivity) vtgt.getBpmnElement());
				String paramname = ((VInParameter) getTarget()).getParameter().getName();
				dedge.setTargetParameter(paramname);
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
			if (getSource().getParent() != null)
			{
				ret = getSource().getParent().getParent();
			}
		}
		return ret;
	}
}
