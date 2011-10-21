package jadex.simulation.analysis.process.analyse;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.highLevel.IAGeneralAnalysisProcessService;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;

/**
 * Agent just for testing
 */
@Description("Agent just test the IAGeneralAnalysisProcessService")
 @ProvidedServices({@ProvidedService(type=IAGeneralAnalysisProcessService.class,
 implementation=@Implementation(expression="new AGeneralAnalysisProcessService($component.getExternalAccess())"))})
 @RequiredServices({
	@RequiredService(name="AllgemeinPlanen", type=IAGeneralPlanningService.class,  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="Allgemein Ausfuehren", type=IAGeneralExecuteService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cmsService", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class GeneralAnalysisAgent extends MicroAgent
{
	@Override
	public void executeBody()
	{
		IAGeneralAnalysisProcessService service = (IAGeneralAnalysisProcessService) SServiceProvider.getService(getServiceProvider(), IAGeneralAnalysisProcessService.class).get(new ThreadSuspendable(this));
		service.analyse(null);
	}
}
