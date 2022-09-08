package jadex.web.examples.mandelbrot;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.mandelbrot_new.DisplayService;
import jadex.micro.examples.mandelbrot_new.IDisplayService;
import jadex.micro.examples.mandelbrot_new.IGenerateService;

/**
 *  Agent offering a display service.
 */
@Description("Agent offering a web display service.")
@ProvidedServices({
	@ProvidedService(type=IDisplayService.class, implementation=@Implementation(DisplayService.class))
})
@RequiredServices({
	@RequiredService(name="generateservice", type=IGenerateService.class),
})
@Agent
public class DisplayAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
		
	@AgentArgument
	protected int port;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Wait for the IWebPublishService and then publish the resources.
	 *  @param pubser The publish service.
	 */
	//@AgentServiceQuery
	@OnService(requiredservice = @RequiredService(min = 1, max = 1))
	protected void publish(IWebPublishService wps)
	{
		if(port==0)
			port = 8080;
		
		//getPlatforms().get();
		
		System.out.println("display publish started: "+wps);
		IServiceIdentifier sid = ((IService)agent.getProvidedService(IDisplayService.class)).getServiceId();
		
		//wps.setLoginSecurity(false);
		
		wps.publishService(sid, new PublishInfo("[http://localhost:"+port+"/]mandelbrot", IPublishService.PUBLISH_RS, null)).get();
		
		wps.publishResources("[http://localhost:"+port+"/]", "resources/mandelbrot").get();
	}
}
