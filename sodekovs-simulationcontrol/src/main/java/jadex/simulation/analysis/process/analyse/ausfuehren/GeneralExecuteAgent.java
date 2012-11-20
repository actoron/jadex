package jadex.simulation.analysis.process.analyse.ausfuehren;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.types.cms.IComponentManagementService;
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
import jadex.simulation.analysis.service.continuative.computation.IAConfidenceService;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

/**
 * Agent just for testing
 */
@Description("Agent just test the IAGeneralExecuteService")
 @ProvidedServices({@ProvidedService(type=IAGeneralExecuteService.class,
 implementation=@Implementation(expression="new AGeneralExecuteService($component.getExternalAccess())"))})
 @RequiredServices({
	@RequiredService(name="experimentService", type=IAExecuteExperimentsService.class),
	@RequiredService(name="KonfidenzServices", type=IAConfidenceService.class,  binding=@Binding(create=true, componenttype="jadex/simulation/analysis/process/analyse/ausfuehren/AllgemeinAusfuehren.bpmn", scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cmsService", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class GeneralExecuteAgent extends MicroAgent
{}
