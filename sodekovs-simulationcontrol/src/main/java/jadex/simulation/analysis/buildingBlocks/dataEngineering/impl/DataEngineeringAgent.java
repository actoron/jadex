package jadex.simulation.analysis.buildingBlocks.dataEngineering.impl;

import jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel;
import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.IEngineerDataObjectService;
import jadex.simulation.analysis.common.component.microAgent.defaultView.MicroComponentViewerPanel;

/**
 *  Agent just offering the dataObject engineering service..
 */
@Description("Agent just offering the dataObject engineering service.")
@ProvidedServices({
	@ProvidedService(type=IEngineerDataObjectService.class, implementation=@Implementation(EngineerDataObjectService.class))
	})
@GuiClass(DefaultComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="componentviewer.viewerclass", value="jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel.class"),
	@NameValue(name="viewerpanel.componentviewerclass", value="jadex.simulation.analysis.common.component.microAgent.defaultView.MicroComponentViewerPanel.class")
})
public class DataEngineeringAgent extends MicroAgent
{	
	@Override
	public IFuture agentCreated()
	{
		//TODO: Classloader (microAgent) do not found the classes of properties, so add viewercalss as a "real" Class
//		getModel().getProperties().put("componentviewer.viewerclass", DefaultComponentServiceViewerPanel.class);
		getModel().getProperties().put("viewerpanel.componentviewerclass", MicroComponentViewerPanel.class);
		return super.agentCreated();
	}
}
