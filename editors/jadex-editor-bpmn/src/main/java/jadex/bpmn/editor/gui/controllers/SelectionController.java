package jadex.bpmn.editor.gui.controllers;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.propertypanels.BasePropertyPanel;

/**
 *  Controller for selections.
 *
 */
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
		if (ModelContainer.EDIT_MODE_STEALTH_SELECTION.equals(modelcontainer.getEditMode()))
		{
			return;
		}
		
		//TODO: JGraphX Bug! added and removed are switched.
		String removed = "added";
		//String added = "removed";
		
		BasePropertyPanel proppanel = (BasePropertyPanel) modelcontainer.getPropertyPanel();
		if (proppanel != null)
		{
			proppanel.terminate();
		}
		
		if (evt.getProperty(removed) != null ||
			modelcontainer.getGraph().getSelectionCount() == 0 ||
			modelcontainer.getGraph().getSelectionCount() > 1)
		{
//			modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
			modelcontainer.setPropertyPanel(modelcontainer.getSettings().getPropertyPanelFactory().createPanel(modelcontainer, null));
		}
		
		if (modelcontainer.getGraph().getSelectionCount() == 1)
		{
//			modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(modelcontainer.getGraph().getSelectionCell(), modelcontainer));
			modelcontainer.setPropertyPanel(modelcontainer.getSettings().getPropertyPanelFactory().createPanel(modelcontainer, modelcontainer.getGraph().getSelectionCell()));
		}
	}
}
