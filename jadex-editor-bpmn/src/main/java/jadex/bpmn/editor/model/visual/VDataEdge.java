package jadex.bpmn.editor.model.visual;

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
}
