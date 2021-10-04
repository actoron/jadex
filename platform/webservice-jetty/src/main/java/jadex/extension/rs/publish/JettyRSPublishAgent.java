package jadex.extension.rs.publish;


import java.net.URL;
import java.util.Set;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that publishes the rs publication service.
 */
@Agent(autostart=Boolean3.FALSE)
@ProvidedServices(
{
	@ProvidedService(name="publish_rs", type=IWebPublishService.class,
		scope=ServiceScope.PLATFORM,
		implementation=@Implementation(JettyRestPublishService.class))
})
@Properties(@NameValue(name="system", value="true"))
public class JettyRSPublishAgent
{
	/*@OnStart
	public void start()
	{
		try
		{
			Set<URL> cls = SUtil.collectClasspathURLs(this.getClass().getClassLoader());
			for(URL u: cls)
				System.out.println(u);
			System.out.println("jetty rs");
			System.out.println(this.getClass().getClassLoader().getParent());
			Class<?> c = Class.forName("jadex.extension.rs.publish.JettyRestPublishService", true, this.getClass().getClassLoader());
			System.out.println("found: "+c);
			System.out.println(JettyRestPublishService.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
}
