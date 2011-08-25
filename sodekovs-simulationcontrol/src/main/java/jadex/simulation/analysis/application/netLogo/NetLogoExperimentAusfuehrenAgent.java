package jadex.simulation.analysis.application.netLogo;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

@Description("Agent offer IAExecuteExperimentsService")
 @ProvidedServices({@ProvidedService(type=IAExecuteExperimentsService.class,
 implementation=@Implementation(expression="new NetLogoExperimentAusfuehrenService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class NetLogoExperimentAusfuehrenAgent extends MicroAgent
{}
