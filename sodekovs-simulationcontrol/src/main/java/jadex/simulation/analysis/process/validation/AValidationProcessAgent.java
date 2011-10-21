package jadex.simulation.analysis.process.validation;

import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.highLevel.IAValidationProcessService;

@Description("Agent just offer the IAValidationProcessService")
 @ProvidedServices({@ProvidedService(type=IAValidationProcessService.class,
 implementation=@Implementation(expression="new AValidationProcessService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class AValidationProcessAgent extends MicroAgent
{
	@Override
	public void executeBody()
	{
		IAValidationProcessService service = (IAValidationProcessService) SServiceProvider.getService(getServiceProvider(), IAValidationProcessService.class).get(new ThreadSuspendable(this));
		service.validate(null);
	}
}
