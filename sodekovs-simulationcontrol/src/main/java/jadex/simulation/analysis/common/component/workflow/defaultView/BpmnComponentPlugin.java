package jadex.simulation.analysis.common.component.workflow.defaultView;

import jadex.base.service.awareness.AwarenessAgentPanel;
import jadex.bpmn.BpmnFactory;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.service.IService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.viewer.GeneralAnalysisServiceViewerPanel;
import jadex.tools.generic.AbstractComponentPlugin;
import jadex.tools.generic.AbstractServicePlugin;
import jadex.tools.generic.AwarenessComponentPlugin;

import javax.swing.Icon;

/**
 *  Used to show a bpmn component view as JCC plugin
 */
public abstract class BpmnComponentPlugin extends AbstractComponentPlugin
{
	//-------- constants --------

	static
	{
		icons.put("bpmn_process",	SGUI.makeIcon(BpmnComponentPlugin.class, "/jadex/bpmn/images/bpmn_process.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public String getModelName()
	{
		return "jadex.simulation.analysis.common.component.workflow.BpmnComponent";
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture createComponentPanel(IExternalAccess component)
	{
		BpmnComponentViewerPanel bpmn = new BpmnComponentViewerPanel();
		bpmn.init(getJCC(), component);
		return new Future(bpmn);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return icons.getIcon("bpmn_process");
	}
}
