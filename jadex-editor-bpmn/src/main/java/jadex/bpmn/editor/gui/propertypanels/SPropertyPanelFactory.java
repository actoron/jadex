package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;

/**
 *  Factory for generating appropriate property panels.
 *
 */
public class SPropertyPanelFactory
{
	/** An empty panel. */
	public static final BasePropertyPanel EMPTY_PANEL = new BasePropertyPanel(null, null);
	
	/**
	 *  Creates a new property panel for the selected item.
	 *  
	 *  @param container The model container.
	 *  @return Property panel.
	 */
	public static BasePropertyPanel createPanel(Object selection, ModelContainer container)
	{
		BasePropertyPanel ret = EMPTY_PANEL;
		if (selection instanceof VElement)
		{
			VElement velement = (VElement) selection;
			if (velement instanceof VActivity && MBpmnModel.TASK.equals(((MActivity) velement.getBpmnElement()).getActivityType()))
			{
				ret = new TaskPropertyPanel(container, (VActivity) velement);
			}
			else if (velement instanceof VActivity && ((MActivity) velement.getBpmnElement()).getActivityType().contains("Timer"))
			{
				ret = new TimerEventPropertyPanel(container, (VActivity) velement);
			}
			else if (velement instanceof VSequenceEdge)
			{
				ret = new SequenceEdgePropertyPanel(container, (VSequenceEdge) velement);
			}
		}
		else if (selection == null)
		{
			ret = new BpmnPropertyPanel(container);
		}
		
		return ret;
	}
}
