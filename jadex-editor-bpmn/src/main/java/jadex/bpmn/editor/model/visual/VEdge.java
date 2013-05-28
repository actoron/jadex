package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;

import com.mxgraph.model.mxICell;
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
	
	public void setSource(mxICell source)
	{
		super.setSource(source);
		setParent(getEdgeParent());
		((BpmnGraph) graph).refreshCellView(this);
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
			ret = getSource().getParent();
		}
		return ret;
	}
}
