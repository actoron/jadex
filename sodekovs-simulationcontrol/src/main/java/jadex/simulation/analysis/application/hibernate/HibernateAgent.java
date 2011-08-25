package jadex.simulation.analysis.application.hibernate;

import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.dataBased.persist.IASaveDataobjectService;

@Description("Agent bietet eine IASaveDataobjectService an")
@ProvidedServices({@ProvidedService(type=IASaveDataobjectService.class, implementation=@Implementation(expression="new jadex.simulation.analysis.application.hibernate.HibernateSaveDataobjectService($component.getExternalAccess())"))})
@GuiClass(ComponentServiceViewerPanel.class)
@Properties(
{
	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"")
})
public class HibernateAgent extends MicroAgent
{	

}
