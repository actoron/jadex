package jadex.simulation.analysis.process.analyse.ausfuehren;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClass;
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
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.computation.IAKonfidenzService;
import jadex.simulation.analysis.service.highLevel.IAAllgemeinAusfuehrenService;
import jadex.simulation.analysis.service.simulation.execution.IAExperimentAusfuehrenService;

/**
 * Agent just for testing
 */
@Description("Agent just test the IAAllgemeinAusfuehrenService")
 @ProvidedServices({@ProvidedService(type=IAAllgemeinAusfuehrenService.class,
 implementation=@Implementation(expression="new AAllgemeinAusfuehrenService($component.getExternalAccess())"))})
 @RequiredServices({
	@RequiredService(name="experimentService", type=IAExperimentAusfuehrenService.class),
	@RequiredService(name="KonfidenzServices", type=IAKonfidenzService.class,  binding=@Binding(create=true, componentfilename="jadex/simulation/analysis/process/analyse/ausfuehren/AllgemeinAusfuehren.bpmn", scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cmsService", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class AllgemeinAusfuehrenAgent extends MicroAgent
{}
