package jadex.simulation.analysis.application.opt4j;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

@Description("Agent offer IAOptimisationService")
 @ProvidedServices({@ProvidedService(type=IAOptimisationService.class,
 implementation=@Implementation(expression="new Opt4JOptimisationService($component.getExternalAccess())"))})
//@GuiClass(ComponentServiceViewerPanel.class)
//@Properties(
//{
//	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
//})
public class OptJAgent extends MicroAgent
{}
