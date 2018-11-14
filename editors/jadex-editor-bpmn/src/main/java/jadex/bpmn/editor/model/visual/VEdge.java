package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.model.MNamedIdElement;

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
		if (getBpmnElement() != null)
		{
			((BpmnGraph) graph).refreshCellView(this);
		}
	}
	
	/**
	 *  Gets the parent.
	 */
	public mxICell getParent()
	{
		if (super.getParent() == null)
			setParent(getEdgeParent());
		return super.getParent();
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
	
	/** 
	 *  Sets the value.
	 */
	public void setValue(Object value)
	{
		if (getBpmnElement() != null)
		{
			((MNamedIdElement) getBpmnElement()).setName((String) value);
		}
	}
	
	/** 
	 *  Gets the value.
	 */
	public Object getValue()
	{
		return getBpmnElement() != null? ((MNamedIdElement) getBpmnElement()).getName() :  super.getValue();
	}
}
