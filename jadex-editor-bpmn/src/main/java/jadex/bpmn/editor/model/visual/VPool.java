package jadex.bpmn.editor.model.visual;

import java.util.Collections;
import java.util.Comparator;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

public class VPool extends VNamedNode
{
	/**
	 *  Comparator ensuring correct y-ordering of lanes.
	 */
	protected static final Comparator<Object> LANE_COMPARATOR = new Comparator<Object>()
	{
		public int compare(Object o1, Object o2)
		{
			if (o1 instanceof VLane || o2 instanceof VLane)
			{
				if (!(o1 instanceof VLane))
				{
					return 1;
				}
				if (!(o2 instanceof VLane))
				{
					return -1;
				}
				VLane v1 = (VLane) o1;
				VLane v2 = (VLane) o2;
				if (v1.getGeometry().getY() > v2.getGeometry().getY())
				{
					return -1;
				}
				return 1;
			}
			return 0;
		}
	};
	
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
	
	/**
	 *  Override to ensure correct y-ordering of lanes.
	 */
	public mxICell insert(mxICell child)
	{
		mxICell ret = super.insert(child);
		Collections.sort(children, LANE_COMPARATOR);
		return ret;
	}
	
	/**
	 *  Override to ensure correct y-ordering of lanes.
	 */
	public mxICell insert(mxICell child, int index)
	{
		mxICell ret = super.insert(child, index);
		Collections.sort(children, LANE_COMPARATOR);
		return ret;
	}
}
