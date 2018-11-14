package jadex.bpmn.editor.gui.controllers;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectPreview;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.contextmenus.EdgeDragContextMenu;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.model.MActivity;

/**
 *  Edge controller for creating and managing edges.
 *
 */
public class EdgeController extends mxConnectionHandler
{
	/** The model container */
	protected ModelContainer modelcontainer;
	
	/** Really commit flag. Hack? */
	protected boolean reallycommit = false;
	
	/**
	 *  Creates the edge controller.
	 *  @param graphcomponent The graph component.
	 */
	public EdgeController(mxGraphComponent graphcomponent, ModelContainer modelcontainer)
	{
		super(graphcomponent);
		this.modelcontainer = modelcontainer;
		marker.setHotspot(0.1);
	}
	
	/**
	 *  Creates the preview.
	 */
	protected mxConnectPreview createConnectPreview()
	{
		return new BpmnConnectPreview(graphComponent);
	}
	
	/**
	 *  Validates a connection.
	 */
	public String validateConnection(Object source, Object target)
	{
		String ret = super.validateConnection(source, target);
		
		String mode = modelcontainer.getEditMode();
		if (ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(mode))
		{
			ret = SValidation.getMessagingEdgeValidationError(source, target);
		}
		else
		{
			if (ret == null)
			{
				if (source instanceof VInParameter ||
					source instanceof VOutParameter ||
					target instanceof VInParameter ||
					target instanceof VOutParameter)
				{
					ret = SValidation.getDataEdgeValidationError(source, target);
				}
			}
			
			if (ret == null)
			{
				if (source instanceof VActivity && !(target instanceof VInParameter))
				{
					if (target instanceof VActivity &&
						((MActivity) ((VActivity) source).getBpmnElement()).getActivityType().endsWith("Message") &&
						((MActivity) ((VActivity) target).getBpmnElement()).getActivityType().endsWith("Message") &&
						SValidation.areMessageEventsConnectable(source, target))
					{
						ret = SValidation.getMessagingEdgeValidationError(source, target);
					}
					else
					{
						ret = SValidation.getSequenceEdgeValidationError(source, target);
					}
				}
			}
		}
		
		if (ret != null && ret.length() > 0)
		{
			Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING, ret);
			ret = "";
		}
		
		return ret;
	}
	
	/**
	 *  Called when mouse is released.
	 */
	public void mouseReleased(final MouseEvent e)
	{
		if (connectPreview != null &&
			connectPreview.getPreviewState() != null &&
			((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(false) == null &&
			((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(true) instanceof VActivity &&
			!(((MActivity)((VActivity)((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(true)).getBpmnElement()).getActivityType() != null && ((MActivity)((VActivity)((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(true)).getBpmnElement()).getActivityType().startsWith("EventEnd")) &&
			!ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(modelcontainer.getEditMode()))
		{
			graphComponent.getGraph().getModel().beginUpdate();
			final EdgeDragContextMenu[] edcmc = new EdgeDragContextMenu[1];
			ActionListener actionlistener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e1)
				{
					if (e1 != null)
					{
						EdgeDragContextMenu edcm = edcmc[0];
						((mxCell) connectPreview.getPreviewState().getCell()).setTarget(edcm.getTarget());
					}
					reallycommit = true;
					EdgeController.super.mouseReleased(e);
					graphComponent.getGraph().getModel().endUpdate();
					reallycommit = false;
					
					
				}
			};
			EdgeDragContextMenu edcm = new EdgeDragContextMenu(((BpmnGraph) graphComponent.getGraph()).getModelContainer(),
					((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(true),
					graphComponent.getPointForEvent(e).getPoint(),
					actionlistener);
			edcmc[0] = edcm;
			int x = e.getX() - graphComponent.getHorizontalScrollBar().getModel().getValue();
			int y = e.getY() - graphComponent.getVerticalScrollBar().getModel().getValue();
			edcm.show(graphComponent, x, y);			
		}
		else
		{
			super.mouseReleased(e);
		}
	}
	
	/**
	 *  Edge creation preview.
	 *
	 */
	protected class BpmnConnectPreview extends mxConnectPreview
	{
		/** Time stamp of drag start. */
		protected long timestamp;
		
		/**
		 *  Creates a new preview.
		 *  @param graphcomponent The graph component.
		 */
		public BpmnConnectPreview(mxGraphComponent graphcomponent)
		{
			super(graphcomponent);
		}
		
		/**
		 *  Updates mouse position.
		 */
		public void update(MouseEvent e, mxCellState targetState, double x,
				double y)
		{
			super.update(e, targetState, x, y);
			timestamp = System.currentTimeMillis();
		}
		
		/**
		 *  Stops operation.
		 */
		public Object stop(boolean commit, MouseEvent e)
		{
			Object result = (sourceState != null) ? sourceState.getCell() : null;

			if (previewState != null)
			{
				mxGraph graph = graphComponent.getGraph();

				graph.getModel().beginUpdate();
				try
				{
					mxICell cell = (mxICell) previewState.getCell();
					Object src = cell.getTerminal(true);
					Object trg = cell.getTerminal(false);

					if (src != null)
					{
						((mxICell) src).removeEdge(cell, true);
					}

					if (trg != null)
					{
						((mxICell) trg).removeEdge(cell, false);
					}
					
					String mode = modelcontainer.getEditMode();
					cell = SCreationController.createConnection((BpmnGraph) graph, mode, src, trg, ((BpmnConnectPreview) connectPreview).timestamp);
					List<mxPoint> points = null;
					if (cell != null && cell.getGeometry() != null)
					{
						points = cell.getGeometry().getPoints();
					}
					
					if (commit || reallycommit)
					{
						if (cell instanceof VEdge)
						{
							if (cell instanceof VMessagingEdge)
							{
								result = graph.addCell(cell, null, null, ((VEdge) cell).getSource(), ((VEdge) cell).getTarget());
//								result = graph.addCell(cell, graph.getCurrentRoot(), null, src, trg);
//								result = graph.addCell(cell, ((mxICell) graph.getModel().getRoot()).getChildAt(0), null, src, trg);
							}
							else
							{
								result = graph.addCell(cell, ((VEdge) cell).getEdgeParent(), null, src, trg);
							}
						}
						else
						{
							result = graph.addCell(cell, ((mxICell) src).getParent(), null, src, trg);
						}
						if (cell instanceof VDataEdge)
						{
							((BpmnGraph) graph).refreshCellView(((VDataEdge) cell).getTarget().getParent());
						}
					}
					
					if (points != null)
					{
						cell.getGeometry().setPoints(points);
					}
					fireEvent(new mxEventObject(mxEvent.STOP, "event", e, "commit",
							commit, "cell", (commit) ? result : null));
					
					// Clears the state before the model commits
					if (previewState != null)
					{
						Rectangle dirty = getDirtyRect();
						graph.getView().clear(cell, false, true);
						previewState = null;

						if (!commit && dirty != null)
						{
							graphComponent.getGraphControl().repaint(dirty);
						}
					}
				}
				finally
				{
					graph.getModel().endUpdate();
				}
			}

			sourceState = null;
			startPoint = null;

			return result;
		}
	}
}
