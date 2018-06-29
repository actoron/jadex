package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MNamedIdElement;

/**
 *  This class represents a named visual node.
 *
 */
public class VNamedNode extends VNode
{
	
	/**
	 *  Creates a new visual representation of a named node.
	 *  
	 *  @param graph The BPMN graph.
	 *  @param style The style.
	 */
	public VNamedNode(mxGraph graph, String style)
	{
		super(graph, style);
	}
	
	/**
	 *  Gets the value.
	 *  
	 *  @return The value.
	 */
	public Object getValue()
	{
		if (getBpmnElement() != null)
		{
			return ((MNamedIdElement) getBpmnElement()).getName();
		}
		
		return super.getValue();
	}
	
	/**
	 *  Sets the value.
	 *  
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		if (getBpmnElement() != null)
		{
			((MNamedIdElement) getBpmnElement()).setName((String) value);
		}
	}
	
	/**
	 *  Sets the BPMN element.
	 *  
	 *  @param bpmnelement The BPMN element.
	 */
	public void setBpmnElement(MIdElement bpmnelement)
	{
		super.setBpmnElement(bpmnelement);
		((MNamedIdElement) bpmnelement).setName((String) getValue());
	}
}
