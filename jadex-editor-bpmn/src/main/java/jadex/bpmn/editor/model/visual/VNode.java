package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

/**
 *  This class represents a visual node.
 *
 */
public abstract class VNode extends VElement
{
	/**
	 *  Creates a new visual representation of a node.
	 *  
	 *  @param graph The BPMN graph.
	 *  @param style The style.
	 */
	public VNode(mxGraph graph, String style)
	{
		super(graph, style);
		setVertex(true);
		setVisible(true);
		setConnectable(true);
	}
}
