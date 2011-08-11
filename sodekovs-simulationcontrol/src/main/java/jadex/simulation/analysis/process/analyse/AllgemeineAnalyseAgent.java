package jadex.simulation.analysis.process.analyse;

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
import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.service.dataBased.engineering.IADatenobjekteErstellenService;
import jadex.simulation.analysis.service.highLevel.IAAllgemeinPlanenService;
import jadex.simulation.analysis.service.highLevel.IAAllgemeinAusfuehrenService;
import jadex.simulation.analysis.service.highLevel.IAAllgemeineAnalyseService;

/**
 * Agent just for testing
 */
@Description("Agent just test the IAAllgemeineAnalyseService")
 @ProvidedServices({@ProvidedService(type=IAAllgemeineAnalyseService.class,
 implementation=@Implementation(expression="new AOptimierungsprozessService($component.getExternalAccess())"))})
 @RequiredServices({
	@RequiredService(name="AllgemeinPlanen", type=IAAllgemeinPlanenService.class,  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="Allgemein Ausfuehren", type=IAAllgemeinAusfuehrenService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cmsService", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class AllgemeineAnalyseAgent extends MicroAgent
{}
