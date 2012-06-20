package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.visual.VElement;

import com.mxgraph.view.mxGraph;

/**
 *  Graph for GPMN models.
 *
 */
public class GpmnGraph extends mxGraph
{
	/**
	 *  Creates the graph.
	 */
	public GpmnGraph()
	{
		setAllowDanglingEdges(false);
		setAllowLoops(false);
		setVertexLabelsMovable(false);
		setCellsCloneable(false);
		setAllowNegativeCoordinates(false);
	}
	
	/**
	 *  Tests if cell is foldable.
	 */
	public boolean isCellFoldable(Object cell, boolean collapse)
	{
		boolean ret = super.isCellFoldable(cell, collapse);
		if (cell instanceof VElement)
		{
			ret &= ((VElement) cell).isFoldable();
		}
		return ret;
	}
}
