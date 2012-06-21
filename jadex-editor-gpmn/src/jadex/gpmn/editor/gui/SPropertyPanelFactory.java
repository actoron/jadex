package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.propertypanels.ActivationPlanPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.BpmnPlanPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.ContextPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.GoalPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.VirtualActivationEdgePropertyPanel;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.visual.VElement;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

/**
 *  Factory for generating appropriate property panels.
 *
 */
public class SPropertyPanelFactory
{
	/** An empty panel. */
	public static final BasePropertyPanel EMPTY_PANEL = new BasePropertyPanel(null);
	
	/**
	 *  Creates a new property panel for the selected item.
	 *  
	 *  @param container The model container.
	 *  @return Property panel.
	 */
	public static BasePropertyPanel createPanel(IModelContainer container)
	{
		BasePropertyPanel ret = EMPTY_PANEL;
		Object selection = container.getGraph().getSelectionCell();
		if (selection instanceof VElement)
		{
			VElement velement = (VElement) selection;
			if (velement.getElement() instanceof IGoal)
			{
				ret = new GoalPropertyPanel(container, (VGoal) velement);
			}
			else if (velement.getElement() instanceof IBpmnPlan)
			{
				ret = new BpmnPlanPropertyPanel(container, (VPlan) velement);
			}
			else if (velement.getElement() instanceof IActivationPlan)
			{
				ret = new ActivationPlanPropertyPanel(container, (VPlan) velement);
			}
			else if (velement instanceof VVirtualActivationEdge)
			{
				ret = new VirtualActivationEdgePropertyPanel(container, (VVirtualActivationEdge) velement);
			}
		}
		else if (selection == null)
		{
			ret = new ContextPropertyPanel(container);
		}
		
		return ret;
	}
}
