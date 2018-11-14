package jadex.bpmn.editor.gui.controllers;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;

public class DeletionController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new deletion controller. */
	public DeletionController(ModelContainer container)
	{
		this.modelcontainer = container;
//		modelcontainer.getGraph().getModel().addListener(mxEvent.CHANGE, this);
		modelcontainer.getGraph().addListener(mxEvent.REMOVE_CELLS, this);
//		modelcontainer.getGraph().addListener(mxEvent.CELLS_REMOVED, this);
//		modelcontainer.getGraph().getModel().addListener(mxEvent.REMOVE_CELLS, this);
//		modelcontainer.getGraph().getSelectionModel().addListener(mxEvent.REMOVE_CELLS, this);
	}
	
	/**
	 *  Called on deletion.
	 */
	public void invoke(Object sender, mxEventObject evt)
	{
		Object[] cells = (Object[]) evt.getProperty("cells");
		for (int i = 0; i < cells.length; ++i)
		{
			if (cells[i] instanceof VPool)
			{
				modelcontainer.getBpmnModel().removePool(((MPool)((VPool) cells[i]).getBpmnElement()));
				modelcontainer.setDirty(true);
			}
			else if (cells[i] instanceof VLane)
			{
				MLane mlane = (MLane) ((VLane) cells[i]).getBpmnElement();
				List<MPool> pools = modelcontainer.getBpmnModel().getPools();
				modelcontainer.setDirty(true);
				
				// Workaround, since the lane has already lost its parent at this point. 
				for (MPool mpool : pools)
				{
					if (mpool.getLanes().contains(mlane))
					{
						mpool.removeLane(mlane);
						break;
					}
				}
				
				modelcontainer.setDirty(true);
			}
			else if (cells[i] instanceof VSequenceEdge)
			{
				VSequenceEdge vedge = (VSequenceEdge) cells[i];
				MSequenceEdge medge = (MSequenceEdge) vedge.getBpmnElement();
				//TODO: No longer necessary, cleanup?
//				List pools = modelcontainer.getBpmnModel().getPools();
//				
//				for (Object obj : pools)
//				{
//					MPool mpool = (MPool) obj;
//					if (mpool.getSequenceEdges() != null)
//					{
//						if (mpool.getSequenceEdges().contains(medge))
//						{
//							mpool.removeSequenceEdge(medge);
//							break;
//						}
//					}
//				}
				medge.getSource().removeOutgoingSequenceEdge(medge);
				medge.getTarget().removeIncomingSequenceEdge(medge);
				modelcontainer.setDirty(true);
			}
			else if (cells[i] instanceof VDataEdge)
			{
				VDataEdge vedge = (VDataEdge) cells[i];
				MDataEdge medge = (MDataEdge) vedge.getBpmnElement();
				
				medge.getSource().removeOutgoingDataEdge(medge);
				medge.getTarget().removeIncomingDataEdge(medge);
				modelcontainer.setDirty(true);
			}
			else if (cells[i] instanceof VMessagingEdge)
			{
				VMessagingEdge vedge = (VMessagingEdge) cells[i];
				MMessagingEdge medge = (MMessagingEdge) vedge.getBpmnElement();
				
				medge.getSource().removeOutgoingMessagingEdge(medge);
				medge.getTarget().removeIncomingMessagingEdge(medge);
				modelcontainer.setDirty(true);
			}
			else if (cells[i] instanceof VActivity)
			{
				VActivity vact = (VActivity) cells[i];
//				MActivity mact = (MActivity) vact.getBpmnElement();
				
				final List<VEdge> deledges = new ArrayList<VEdge>();
				for (int j = 0; j < vact.getEdgeCount(); ++j)
				{
					VEdge edge = (VEdge) vact.getEdgeAt(j);
					if (edge instanceof VMessagingEdge)
					{
						deledges.add(edge);
					}
				}
				
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().removeCells(deledges.toArray());
				modelcontainer.getGraph().getModel().endUpdate();
			}
		}
	}
}
