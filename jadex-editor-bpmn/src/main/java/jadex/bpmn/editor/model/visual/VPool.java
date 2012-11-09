package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

public class VPool extends VNamedNode
{
	
	/**
	 * Creates a new pool.
	 * 
	 * @param graph The graph where this element is used.
	 */
	public VPool(mxGraph graph)
	{
		super(graph, VPool.class.getSimpleName());
		setConnectable(false);
	}
	
	/**
	 *  Tests if a pool contains lanes.
	 *  
	 *  @return True, if the pool contains lanes.
	 */
	public boolean hasLanes()
	{
		for (int i = 0; i < getChildCount(); ++i)
		{
			if (getChildAt(i) instanceof VLane)
			{
				return true;
			}
		}
		
		return false;
	}
}
