package jadex.bpmn.editor.model.visual;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

public class VPool extends VNamedNode
{
	/** Previous geometry since the last set. */
	protected mxGeometry previousgeometry;
	
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
	 *  Sets a new geometry, preserving the previous.
	 */
	public void setGeometry(mxGeometry geometry)
	{
		previousgeometry = getGeometry();
		super.setGeometry(geometry);
//		(new RuntimeException()).printStackTrace();
	}
	
	/**
	 * Returns the previous geometry.
	 * 
	 * @return The previous geometry.
	 */
	public mxGeometry getPreviousGeometry()
	{
		return previousgeometry;
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
