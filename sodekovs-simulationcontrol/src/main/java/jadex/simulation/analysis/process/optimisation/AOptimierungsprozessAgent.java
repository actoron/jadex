package jadex.simulation.analysis.process.optimisation;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.highLevel.IAOptimierungsprozessService;

@Description("Agent just offer the IAOptimierungsprozessService")
 @ProvidedServices({@ProvidedService(type=IAOptimierungsprozessService.class,
 implementation=@Implementation(expression="new AOptimierungsprozessService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class AOptimierungsprozessAgent extends MicroAgent
{}
