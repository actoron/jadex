package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;

/**
 *  Visual representation of a sequence edge.
 *
 */
public class VSequenceEdge extends VEdge
{
	/**
	 *  Creates a new visual representation of a sequence edge.
	 *  
	 *  @param graph The BPMN graph.
	 *  @param style The style.
	 */
	public VSequenceEdge(mxGraph graph)
	{
		super(graph, VSequenceEdge.class.getSimpleName());
		super.setStyle(VSequenceEdge.class.getSimpleName());
	}
	
	/**
	 *  Override set attempts.
	 */
	public void setStyle(String style)
	{
	}
	
	/**
	 *  Gets the style.
	 */
	public String getStyle()
	{
		String ret = super.getStyle();
		if(((MSequenceEdge) getBpmnElement()).isDefault())
		{
			ret += "_DEFAULT";
		}
		return ret;
	}
	
	/**
	 *  Sets the source.
	 */
	public void setSource(mxICell source)
	{
		if (getBpmnElement() != null)
		{
			MSequenceEdge medge = (MSequenceEdge) getBpmnElement();
			if (getSource() != null)
			{
				((MActivity) ((VActivity) getSource()).getBpmnElement()).removeOutgoingSequenceEdge(medge);
				medge.setSource(null);
			}
			
			if (source != null)
			{
				MActivity msource = (MActivity) ((VActivity) source).getBpmnElement();
				msource.addOutgoingSequenceEdge(medge);
			
				medge.setSource(msource);
			}
		}
		super.setSource(source);
	}
	
	/**
	 *  Sets the target.
	 */
	public void setTarget(mxICell target)
	{
		if (getBpmnElement() != null)
		{
			MSequenceEdge medge = (MSequenceEdge) getBpmnElement();
			if (getTarget() != null)
			{
				((MActivity) ((VActivity) getTarget()).getBpmnElement()).removeIncomingSequenceEdge(medge);
				medge.setTarget(null);
			}
			
			if (target != null)
			{
				MActivity mtarget = (MActivity) ((VActivity) target).getBpmnElement();
				mtarget.addIncomingSequenceEdge(medge);
				
				medge.setTarget(mtarget);
			}
		}
		super.setTarget(target);
	}
}
