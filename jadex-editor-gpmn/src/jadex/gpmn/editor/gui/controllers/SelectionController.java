package jadex.gpmn.editor.gui.controllers;

import jadex.gpmn.editor.gui.IModelContainer;
import jadex.gpmn.editor.gui.IViewAccess;
import jadex.gpmn.editor.gui.SPropertyPanelFactory;
import jadex.gpmn.editor.model.visual.SequentialMarker;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VNode;
import jadex.gpmn.editor.model.visual.VPlan;

import java.util.List;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

public class SelectionController implements mxIEventListener
{
	/** The model container. */
	protected IModelContainer modelcontainer;
	
	/** Access to the view. */
	protected IViewAccess viewaccess;
	
	/**
	 *  Creates the controller.
	 */
	public SelectionController(IModelContainer modelcontainer, IViewAccess viewaccess)
	{
		this.modelcontainer = modelcontainer;
		this.viewaccess = viewaccess;
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
			
			if (nodes && !IViewAccess.SELECT_MODE.equals(viewaccess.getToolGroup().getSelection().getActionCommand()))
			{
				viewaccess.getToolGroup().setSelected(viewaccess.getSelectTool().getModel(), true);
			}
		}
		else if (evt.getProperty(removed) != null && modelcontainer.getGraph().getSelectionCount() == 0)
		{
			//TODO: Add context panel.
			viewaccess.setPropertPanel(SPropertyPanelFactory.EMPTY_PANEL);
		}
		
		if (modelcontainer.getGraph().getSelectionCount() == 1)
		{
			viewaccess.setPropertPanel(SPropertyPanelFactory.createPanel(modelcontainer.getGraph()));
		}
	}
}
