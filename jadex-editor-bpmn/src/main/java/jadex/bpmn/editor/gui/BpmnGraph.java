package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.SValidation;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;

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
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates the graph.
	 */
	public BpmnGraph(ModelContainer container, mxStylesheet sheet)
	{
		this.modelcontainer = container;
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
	 *  Gets the model container.
	 *
	 *  @return The model container.
	 */
	public ModelContainer getModelContainer()
	{
		return modelcontainer;
	}

	/**
	 * Returns true if the given cell is a valid drop target for the specified
	 * cells. This returns true if the cell is a swimlane, has children and is
	 * not collapsed, or if splitEnabled is true and isSplitTarget returns
	 * true for the given arguments
	 * 
	 * @param cell Object that represents the possible drop target.
	 * @param cells Objects that are going to be dropped.
	 * @return Returns true if the cell is a valid drop target for the given
	 * cells.
	 */
	public boolean isValidDropTarget(Object cell, Object[] cells)
	{
		boolean ret = cell != null
				&& ((isSplitEnabled() && isSplitTarget(cell, cells)) || (!model
						.isEdge(cell) && (isSwimlane(cell) || cell instanceof VSubProcess || (model
						.getChildCount(cell) > 0 && !isCellCollapsed(cell)))));
		return ret;
	}

	/**
	 *  Refreshes the view for a cell.
	 *  
	 *  @param cell The cell.
	 */
	public void refreshCellView(mxICell cell)
	{
		getView().clear(cell, true, false);
		getView().invalidate(cell);
		Object[] selcells = getSelectionModel().getCells();
		getSelectionModel().removeCells(selcells);
		getView().validate();
		setSelectionCells(selcells);
	}
	
	/**
	 * Returns the validation error message to be displayed when inserting or
	 * changing an edges' connectivity. A return value of null means the edge
	 * is valid, a return value of '' means it's not valid, but do not display
	 * an error message. Any other (non-empty) string returned from this method
	 * is displayed as an error message when trying to connect an edge to a
	 * source and target. This implementation uses the multiplicities, as
	 * well as multigraph and allowDanglingEdges to generate validation
	 * errors.
	 * 
	 * @param edge Cell that represents the edge to validate.
	 * @param source Cell that represents the source terminal.
	 * @param target Cell that represents the target terminal.
	 */
	public String getEdgeValidationError(Object edge, Object source,
			Object target)
	{
		String error = super.getEdgeValidationError(edge, source, target);
		if (error == null)
		{
			if (edge instanceof VSequenceEdge)
			{
				error = SValidation.getSequenceEdgeValidationError(source, target);
			}
		}
		
		return error;
	}
}
