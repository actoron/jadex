package jadex.simulation.analysis.application.commonsMath;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.computation.IAConfidenceService;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.continuative.validation.IAValidationService;

@Description("Agent offer IAOptimisationService, IAValidationService and IAConfidenceService")
 @ProvidedServices({@ProvidedService(type=IAOptimisationService.class, implementation=@Implementation(expression="new CommonsMathOptimisationService($component.getExternalAccess())")),
 		@ProvidedService(type=IAValidationService.class, implementation=@Implementation(expression="new CommonsMathValidationService($component.getExternalAccess())")),
 		@ProvidedService(type=IAConfidenceService.class, implementation=@Implementation(expression="new CommonsMathConfidenceService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class CommonsMathAgent extends MicroAgent
{}
