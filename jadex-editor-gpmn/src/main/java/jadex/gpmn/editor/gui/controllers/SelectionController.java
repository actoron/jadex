package jadex.gpmn.editor.gui.controllers;

import java.util.List;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SPropertyPanelFactory;
import jadex.gpmn.editor.model.visual.SequentialMarker;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VNode;
import jadex.gpmn.editor.model.visual.VPlan;

public class SelectionController implements mxIEventListener
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates the controller.
	 */
	public SelectionController(ModelContainer modelcontainer)
	{
		this.modelcontainer = modelcontainer;
	}
	
	/**
	 *  Handles the event.
	 */
	public void invoke(Object sender, mxEventObject evt)
	{
		//TODO: JGraphX Bug! added and removed are switched.
		String removed = "added";
		String added = "removed";
		
		if (evt.getProperty(added) != null)
		{
			boolean nodes = false;
			List addedcells = (List) evt.getProperty(added);
			for (int i = 0; i < addedcells.size(); ++i)
			{
				if (!nodes && addedcells.get(i) instanceof VNode)
				{
					nodes = true;
				}
				else if ((addedcells.get(i) instanceof VGoal.VGoalType) ||
						 (addedcells.get(i) instanceof VPlan.VPlanType) ||
						 (addedcells.get(i) instanceof SequentialMarker))
				{
					mxICell marker = (mxICell) addedcells.get(i);
					mxICell parent = marker.getParent();
					modelcontainer.getGraph().removeSelectionCell(marker);
					modelcontainer.getGraph().addSelectionCell(parent);
				}
			}
			
			if (nodes && !ModelContainer.SELECT_MODE.equals(modelcontainer.getEditMode()))
			{
				modelcontainer.setEditMode(ModelContainer.SELECT_MODE);
			}
		}
		else if (evt.getProperty(removed) != null && modelcontainer.getGraph().getSelectionCount() == 0)
		{
			//TODO: Correct?.
			modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
			//viewaccess.setPropertPanel(SPropertyPanelFactory.EMPTY_PANEL);
		}
		
		if (modelcontainer.getGraph().getSelectionCount() == 1)
		{
			modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer));
		}
	}
}
