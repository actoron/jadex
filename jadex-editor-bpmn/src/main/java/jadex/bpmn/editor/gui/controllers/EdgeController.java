package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.BpmnGraphComponent;
import jadex.bpmn.editor.gui.EdgeDragContextMenu;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectPreview;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;

public class EdgeController extends mxConnectionHandler
{
	/** Really commit flag. */
	protected boolean reallycommit = false;
	
	public EdgeController(BpmnGraphComponent graphcomponent)
	{
		super(graphcomponent);
	}
	
	protected mxConnectPreview createConnectPreview()
	{
		return new BpmnConnectPreview(graphComponent);
	}
	
	public String validateConnection(Object source, Object target)
	{
		String ret = super.validateConnection(source, target);
		
		if (ret == null)
		{
			if ((source instanceof VActivity && target instanceof VActivity))
			{
				ret = SValidation.getSequenceEdgeValidationError(source, target);
			}
					
		}
		
		return ret;
	}
	
	public mxICell createConnection(Object src, Object tgt)
	{
		ModelContainer modelcontainer = ((BpmnGraph) graphComponent.getGraph()).getModelContainer();
		mxICell ret = null;
		mxICell source = (mxICell) src;
		mxICell target = (mxICell) tgt;
		if (source instanceof VActivity && target instanceof VActivity)
		{
			MSequenceEdge medge = new MSequenceEdge();
			medge.setId(modelcontainer.getIdGenerator().generateId());
			
			if (((VActivity) source).getParent() instanceof VSubProcess)
			{
				((MSubProcess) ((VSubProcess) ((VActivity) source).getParent()).getBpmnElement()).addSequenceEdge(medge);
			}
			else
			{
				MActivity msrc = (MActivity) ((VActivity) source).getBpmnElement();
				msrc.getPool().addSequenceEdge(medge);
			}
			
			VSequenceEdge vedge = new VSequenceEdge(modelcontainer.getGraph(), VSequenceEdge.class.getSimpleName());
			vedge.setBpmnElement(medge);
			vedge.setSource(source);
			vedge.setTarget(target);
			modelcontainer.setDirty(true);
			ret = vedge;
		}
		
		return ret;
	}
	
	public void mouseReleased(final MouseEvent e)
	{
		if (connectPreview != null && connectPreview.getPreviewState() != null && ((mxICell) connectPreview.getPreviewState().getCell()).getTerminal(false) == null)
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
					e.getPoint(),
					actionlistener);
			edcmc[0] = edcm;
			edcm.show(graphComponent, (int) e.getX(), (int) e.getY());			
		}
		else
		{
			super.mouseReleased(e);
		}
	}
	
	protected class BpmnConnectPreview extends mxConnectPreview
	{
		public BpmnConnectPreview(mxGraphComponent graphcomponent)
		{
			super(graphcomponent);
		}
		
		
		
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
					
					cell = createConnection(src, trg);
					
					if (commit || reallycommit)
					{
						result = graph.addCell(cell, ((mxICell) src).getParent(), null, src, trg);
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
