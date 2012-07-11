package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.visual.VElement;

import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 *  Graph for GPMN models.
 *
 */
public class GpmnGraph extends mxGraph
{
	/**
	 *  Creates the graph.
	 */
	public GpmnGraph(IControllerAccess access, mxStylesheet sheet)
	{
		setAllowDanglingEdges(false);
		setAllowLoops(false);
		setVertexLabelsMovable(false);
		setCellsCloneable(false);
		setAllowNegativeCoordinates(false);
		
		getModel().addListener(mxEvent.EXECUTE, access.getValueChangeController());
		getSelectionModel().addListener(mxEvent.CHANGE, access.getSelectionController());
		
		addListener(mxEvent.CONNECT_CELL, access.getEdgeReconnectController());
		
		addListener(mxEvent.CELLS_FOLDED, access.getFoldController());
		
		setStylesheet(sheet);
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
