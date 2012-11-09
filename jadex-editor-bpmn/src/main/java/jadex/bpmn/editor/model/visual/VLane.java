package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

public class VLane extends VNamedNode
{
	/** Width of the text field of the associated pool. */
	protected double pooltextfieldwidth;
	
	/** Height of the lane. */
	protected int laneheight;
	
	/**
	 * Creates a new lane.
	 * 
	 * @param graph The graph where this element is used.
	 */
	public VLane(mxGraph graph)
	{
		super(graph, VLane.class.getSimpleName());
		setValue("Lane");
		setConnectable(false);
	}
	
	/**
	 *  Returns the pool of the lane.
	 *  
	 *  @return The pool.
	 */
	public VPool getPool()
	{
		VElement pool = this;
		while (!(pool instanceof VPool))
		{
			pool = (VElement) pool.getParent();
		}
		
		return (VPool) pool;
	}
}
