package jadex.simulation.analysis.process.analyse.planen;

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
import jadex.simulation.analysis.service.dataBased.engineering.IAEngineerDataobjectService;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;

/**
 * Agent just for testing
 */
@Description("Agent just test the IAGeneralPlanningService")
 @ProvidedServices({@ProvidedService(type=IAGeneralPlanningService.class,
 implementation=@Implementation(expression="new AGeneralPanningService($component.getExternalAccess())"))})
 @RequiredServices({
	@RequiredService(name="DatenobjektErstellenServices", type=IAEngineerDataobjectService.class,  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cmsService", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})

@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
})
public class GeneralPlanningAgent extends MicroAgent
{}
