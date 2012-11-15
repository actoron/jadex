package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;

import java.util.List;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

public class DeletionController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new deletion controller. */
	public DeletionController(ModelContainer container)
	{
		this.modelcontainer = container;
		modelcontainer.getGraph().addListener(mxEvent.REMOVE_CELLS, this);
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
				List pools = modelcontainer.getBpmnModel().getPools();
				
				// Workaround, since the lane has already lost its parent at this point. 
				for (Object obj : pools)
				{
					MPool mpool = (MPool) obj;
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
				List pools = modelcontainer.getBpmnModel().getPools();
				
				for (Object obj : pools)
				{
					MPool mpool = (MPool) obj;
					if (mpool.getSequenceEdges().contains(medge))
					{
						mpool.removeSequenceEdge(medge);
						break;
					}
				}
				medge.getSource().removeOutgoingSequenceEdge(medge);
				medge.getTarget().removeIncomingSequenceEdge(medge);
			}
//			else if (cells[i] instanceof VActivity)
//			{
//				VActivity vact = (VActivity) cells[i];
//				MActivity mact = (MActivity) vact.getBpmnElement();
//				
//				for (int j = 0; j < vact.getEdgeCount(); ++j)
//				{
//					VEdge edge = (VEdge) vact.getEdgeAt(j);
//					if (edge instanceof VSequenceEdge)
//					{
//						
//					}
//				}
//			}
		}
	}
}
