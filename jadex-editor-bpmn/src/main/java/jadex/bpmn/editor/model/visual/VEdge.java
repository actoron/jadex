package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

/**
 *  Visual representation of a BPMN edge.
 *
 */
public class VEdge extends VElement
{
	/**
	 *  Creates a new visual representation of an edge.
	 *  
	 *  @param graph The BPMN graph.
	 *  @param style The style.
	 */
	public VEdge(mxGraph graph, String style)
	{
		super(graph, style);
		setVertex(false);
		setEdge(true);
		setVisible(true);
		setConnectable(false);
	}
}
