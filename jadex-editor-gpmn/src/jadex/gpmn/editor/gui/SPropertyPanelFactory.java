package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.propertypanels.ActivationPlanPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.BpmnPlanPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.GoalPropertyPanel;
import jadex.gpmn.editor.gui.propertypanels.VirtualActivationEdgePropertyPanel;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.visual.VElement;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

import com.mxgraph.view.mxGraph;

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
	 *  @param graph The graph.
	 *  @return Property panel.
	 */
	public static BasePropertyPanel createPanel(mxGraph graph)
	{
		BasePropertyPanel ret = EMPTY_PANEL;
		Object selection = graph.getSelectionCell();
		if (selection instanceof VElement)
		{
			VElement velement = (VElement) selection;
			if (velement.getElement() instanceof IGoal)
			{
				ret = new GoalPropertyPanel(graph, (VGoal) velement);
			}
			else if (velement.getElement() instanceof IBpmnPlan)
			{
				ret = new BpmnPlanPropertyPanel(graph, (VPlan) velement);
			}
			else if (velement.getElement() instanceof IActivationPlan)
			{
				ret = new ActivationPlanPropertyPanel(graph, (VPlan) velement);
			}
			else if (velement instanceof VVirtualActivationEdge)
			{
				ret = new VirtualActivationEdgePropertyPanel(graph, (VVirtualActivationEdge) velement);
			}
		}
		
		return ret;
	}
}
