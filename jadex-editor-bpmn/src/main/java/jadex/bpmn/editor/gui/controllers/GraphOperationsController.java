package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VSubProcess;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.TransferHandler;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellMarker;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 *  This controller handles certain operations concerning the graph such as object movement.
 *
 */
public class GraphOperationsController extends mxGraphHandler
{
	/**
	 *  Creates a new graph controller.
	 * @param graphcomponent The graph component.
	 */
	public GraphOperationsController(mxGraphComponent graphcomponent)
	{
		super(graphcomponent);
	}
	
	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()
				&& !graphComponent.isForceMarqueeEvent(e))
		{
			cell = graphComponent.getCellAt(e.getX(), e.getY(), false);
//			System.out.println("Sel CELL: "+cell);
			// Enable to make the whole pool draggable.
//			if (cell == null)
//			{
//				cell = graphComponent.getCellAt(e.getX(), e.getY(), true);
//			}
			while (cell instanceof VLane)
			{
				cell = ((VLane) cell).getParent();
			}
			
			initialCell = cell;

			if (cell != null)
			{
				if (isSelectEnabled()
						&& !graphComponent.getGraph().isCellSelected(cell))
				{
					graphComponent.selectCellForEvent(cell, e);
					cell = null;
				}

				if (isMoveEnabled() && !e.isPopupTrigger())
				{
					start(e);
					e.consume();
				}
			}
			else if (e.isPopupTrigger())
			{
				graphComponent.getGraph().clearSelection();
			}
		}
	}
	
	/**
	 *  Moves the cells.
	 */
	protected void moveCells(Object[] cells, double dx, double dy,
			Object target, MouseEvent e)
	{
		String error = SValidation.getMoveValidationError(cells, target);
		if (error == null)
		{
			super.moveCells(cells, dx, dy, target, e);
			((BpmnGraph) graphComponent.getGraph()).getModelContainer().setDirty(true);
			((BpmnGraph) graphComponent.getGraph()).getModelContainer().setEditMode(ModelContainer.EDIT_MODE_SELECTION);
		}
		else
		{
			if (error.length() > 0)
			{	
				Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, error);
			}
		}
	}
	
	/**
	 *  Fold cells.
	 */
	protected void fold(Object cell)
	{
		graphComponent.doLayout();
		super.fold(cell);
	}
	
	/**
	 *  Creates the marker.
	 */
	protected mxCellMarker createMarker()
	{
		mxCellMarker marker = new mxCellMarker(graphComponent, Color.BLUE)
		{
			/**
			 * 
			 */
			public boolean isEnabled()
			{
				return graphComponent.getGraph().isDropEnabled();
			}

			/**
			 * 
			 */
			public Object getCell(MouseEvent e)
			{
				mxIGraphModel model = graphComponent.getGraph().getModel();
				TransferHandler th = graphComponent.getTransferHandler();
				boolean isLocal = th instanceof mxGraphTransferHandler
						&& ((mxGraphTransferHandler) th).isLocalDrag();

				mxGraph graph = graphComponent.getGraph();
				Object cell = super.getCell(e);
				Object[] cells = (isLocal) ? graph.getSelectionCells()
						: dragCells;
				
				//FIXME: Bug?
				cells = cells != null? cells : graph.getSelectionCells();
				
				cell = graph.getDropTarget(cells, e.getPoint(), cell);

				// Checks if parent is dropped into child
				Object parent = cell;

				while (parent != null)
				{
					if (mxUtils.contains(cells, parent))
					{
						return null;
					}
					
					parent = model.getParent(parent);
				}

				boolean clone = graphComponent.isCloneEvent(e) && cloneEnabled;

				if (isLocal && cell != null && cells.length > 0 && !clone
						&& graph.getModel().getParent(cells[0]) == cell)
				{
					cell = null;
				}

				return cell;
			}
			
			/**
			 */
			protected boolean isValidState(mxCellState state)
			{
				return SValidation.getMoveValidationError(cells, state.getCell()) == null;
			}
		};

		marker.setSwimlaneContentEnabled(true);

		return marker;
	}
}
