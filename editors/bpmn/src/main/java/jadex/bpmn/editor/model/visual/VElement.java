package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.model.MIdElement;

/**
 *  Generic Visual BPMN Element.
 */
public abstract class VElement extends mxCell
{
	/** The graph where this element is used. */
	protected mxGraph graph;
	
	/** Associated BPMN element */
	protected MIdElement bpmnelement;
	
	/**
	 * Creates a new element.
	 * 
	 * @param graph The graph where this element is used.
	 * @param geometry Initial element geometry.
	 * @param style Initial style.
	 */
	public VElement(mxGraph graph, String style)
	{
		super(null, new mxGeometry(), style);
		this.graph = graph;
		setStyle(style);
	}
	
	/**
	 *  Returns the graph.
	 *  
	 *  @return The graph.
	 */
	public mxGraph getGraph()
	{
		return graph;
	}

	/**
	 *  Gets the BPMN element.
	 *
	 *  @return The BPMN element.
	 */
	public MIdElement getBpmnElement()
	{
		return bpmnelement;
	}

	/**
	 *  Sets the BPMN element.
	 *
	 *  @param bpmnelement The BPMN element.
	 */
	public void setBpmnElement(MIdElement bpmnelement)
	{
		this.bpmnelement = bpmnelement;
	}
	
	/**
	 *  Sets the visual parent without side effects.
	 */
	public void setVisualParent(mxICell parent)
	{
		super.setParent(parent);
	}
}
