package jadex.simulation.analysis.application.standalone;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

@Description("Agent bietet eine IADatenobjekteParametrisierenGUIService an")
@ProvidedServices({@ProvidedService(type=IAVisualiseDataobjectService.class, implementation=@Implementation(expression="new jadex.simulation.analysis.application.standalone.AVisualiseDataobjectService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class AVisualiseDataObjectAgent extends MicroAgent
{	

}
