package jadex.simulation.analysis.application.standalone;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.dataBased.engineering.IAEngineerDataobjectService;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;
import jadex.simulation.analysis.service.simulation.allocation.IAAllocateExperimentsService;

@Description("Agent bietet eine IAAllocateExperimentsService an")
@ProvidedServices({@ProvidedService(type=IAAllocateExperimentsService.class, implementation=@Implementation(expression="new jadex.simulation.analysis.application.standalone.AAllocateExperimentsService($component.getExternalAccess())")),
	@ProvidedService(type=IAVisualiseDataobjectService.class, implementation=@Implementation(expression="new jadex.simulation.analysis.application.standalone.AVisualiseDataobjectService($component.getExternalAccess())")),
	@ProvidedService(type=IAEngineerDataobjectService.class, implementation=@Implementation(expression="new jadex.simulation.analysis.application.standalone.AEngineerDataobjectService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class AStandaloneAgent extends MicroAgent
{	

}
