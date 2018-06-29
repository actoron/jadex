package jadex.gpmn.editor.gui.controllers;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VNode;

public class DeletionController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** Creates a new edge reconnect controller. */
	public DeletionController(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	public void invoke(Object sender, mxEventObject evt)
	{
		Object[] cells = (Object[]) evt.getProperty("cells");
		for (int i = 0; i < cells.length; ++i)
		{
			if (cells[i] instanceof VNode)
			{
				//System.out.println("Deleting Node: " + cells[i]);
				INode node = ((VNode) cells[i]).getNode();
				modelcontainer.getGpmnModel().removeNode(node);
				modelcontainer.setDirty(true);
			}
			else if(cells[i] instanceof VEdge)
			{
				//System.out.println("Deleting Edge: " + cells[i]);
				IEdge edge = ((VEdge) cells[i]).getEdge();
				modelcontainer.getGpmnModel().removeEdge(edge);
				modelcontainer.setDirty(true);
				
				if (edge instanceof IPlanEdge)
				{
					SGuiHelper.refreshCellView(modelcontainer.getGraph(), ((VGoal) ((VEdge) cells[i]).getSource()));
				}
			}
		}
	}
}
