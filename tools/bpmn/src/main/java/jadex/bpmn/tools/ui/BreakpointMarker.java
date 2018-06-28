package jadex.bpmn.tools.ui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

/**
 * 
 */
public class BreakpointMarker extends mxCell
{
	/** The graph where this element is used. */
	protected mxGraph graph;
	
	/**
	 * Creates a new element.
	 * 
	 * @param graph The graph where this element is used.
	 * @param geometry Initial element geometry.
	 * @param style Initial style.
	 */
	public BreakpointMarker(mxGraph graph)
	{
		super(null, new mxGeometry(), BreakpointMarker.class.getSimpleName());
		this.graph = graph;
		
		setVertex(true);
		setVisible(true);
	}
	
	/**
	 *  Returns the graph.
	 *  @return The graph.
	 */
	public mxGraph getGraph()
	{
		return graph;
	}
}
