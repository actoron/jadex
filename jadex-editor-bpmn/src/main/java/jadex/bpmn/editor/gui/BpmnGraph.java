package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.SEdgeValidation;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxLayoutManager;
import com.mxgraph.view.mxStylesheet;

/**
 *  Graph for BPMN models.
 *
 */
public class BpmnGraph extends mxGraph
{
	/**
	 *  Creates the graph.
	 */
	public BpmnGraph(mxStylesheet sheet)
	{
		setAllowDanglingEdges(false);
		setAllowLoops(true);
		setVertexLabelsMovable(false);
		setCellsCloneable(false);
		setAllowNegativeCoordinates(false);
		setGridEnabled(true);
		setGridSize(10);
		
		/*getModel().addListener(mxEvent.EXECUTE, access.getValueChangeController());
		getSelectionModel().addListener(mxEvent.CHANGE, access.getSelectionController());
		
		addListener(mxEvent.CONNECT_CELL, access.getEdgeReconnectController());
		
		addListener(mxEvent.CELLS_FOLDED, access.getFoldController());*/
		
		setStylesheet(sheet);
		
		new mxLayoutManager(this)
		{
			protected mxStackLayout lanelayout = new LaneLayout(graph);
			
			public mxIGraphLayout getLayout(Object parent)
			{
				if (parent instanceof VPool &&
					graph.getModel().getChildCount(parent) > 0 &&
					graph.getModel().getChildAt(parent, 0) instanceof VLane)
				{
					return lanelayout;
				}
				
				return null;
			}
		};
	}
	
	/*protected mxGraphView createGraphView()
	{
		return new BpmnGraphView(this);
	}*/
	
	/**
	 *  Refreshes the view for a cell.
	 *  
	 *  @param cell The cell.
	 */
	public void refreshCellView(mxICell cell)
	{
		getView().clear(cell, true, false);
		getView().invalidate(cell);
		//Object[] selcells = getSelectionModel().getCells();
		//getSelectionModel().removeCells(selcells);
		getView().validate();
		//setSelectionCells(selcells);
	}
	
	public Object addCell(Object cell, Object parent)
	{
		return super.addCell(cell, parent);
	}
	
	public boolean isCellConnectable(Object cell)
	{
		return super.isCellConnectable(cell);
	}
	
	public String getEdgeValidationError(Object edge, Object source,
			Object target)
	{
		String error = super.getEdgeValidationError(edge, source, target);
		if (error == null)
		{
			error = SEdgeValidation.getEdgeValidationError(edge, source, target);
		}
		
		return error;
	}
}
