package jadex.gpmn.editor.gui;

import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import jadex.gpmn.editor.gui.controllers.EdgeReconnectController;
import jadex.gpmn.editor.gui.controllers.SelectionController;
import jadex.gpmn.editor.gui.controllers.ValueChangeController;
import jadex.gpmn.editor.model.visual.VElement;

/**
 *  Graph for GPMN models.
 *
 */
public class GpmnGraph extends mxGraph
{
	/**
	 *  Creates the graph.
	 */
	public GpmnGraph(ModelContainer container, mxStylesheet sheet)
	{
		setAllowDanglingEdges(false);
		setAllowLoops(false);
		setVertexLabelsMovable(false);
		setCellsCloneable(false);
		setAllowNegativeCoordinates(false);
		
		getModel().addListener(mxEvent.EXECUTE, new ValueChangeController(container));
		getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(container));
		
		addListener(mxEvent.CONNECT_CELL, new EdgeReconnectController(container));
		
		addListener(mxEvent.CELLS_FOLDED, container.getFoldController());
		
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
