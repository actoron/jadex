package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MMessagingEdge;

public class VMessagingEdge extends VEdge
{
	public VMessagingEdge(mxGraph graph)
	{
		super(graph, VMessagingEdge.class.getSimpleName());
	}
	
	/**
	 *  Sets the source.
	 */
	public void setSource(mxICell source)
	{
		MMessagingEdge medge = (MMessagingEdge) getBpmnElement();
		if (medge != null)
		{
			if (getSource() != null)
			{
				VActivity vsrc = (VActivity) getSource();
				((MActivity) vsrc.getBpmnElement()).removeOutgoingMessagingEdge(medge);
			}
			this.source = source;
			if (source != null)
			{
				VActivity vsrc = (VActivity) getSource();
				((MActivity) vsrc.getBpmnElement()).addOutgoingMessagingEdge(medge);
				medge.setSource((MActivity) vsrc.getBpmnElement());
			}
		}
		else
		{
			this.source = source;
		}
		((BpmnGraph) graph).refreshCellView(this);
	}
	
	/**
	 *  Sets the target.
	 */
	public void setTarget(mxICell target)
	{
		MMessagingEdge medge = (MMessagingEdge) getBpmnElement();
		if (medge != null)
		{
			if (getTarget() != null)
			{
				VActivity vtgt = (VActivity) getTarget();
				((MActivity) vtgt.getBpmnElement()).removeIncomingMessagingEdge(medge);
			}
			super.setTarget(target);
			if (target != null)
			{
				VActivity vtgt = (VActivity) getTarget();
				((MActivity) vtgt.getBpmnElement()).addIncomingMessagingEdge(medge);
				medge.setTarget((MActivity) vtgt.getBpmnElement());
			}
		}
		else
		{
			super.setTarget(target);
		}
	}
	
}
