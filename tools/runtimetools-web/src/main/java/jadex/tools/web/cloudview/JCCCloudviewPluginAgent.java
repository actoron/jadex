package jadex.tools.web.cloudview;

import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

@ProvidedServices({@ProvidedService(name="cloudviewweb", type=IJCCCloudviewService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCCloudviewPluginAgent extends JCCPluginAgent implements IJCCCloudviewService
{

	public IFuture<String> getPluginName()
	{
		return new Future<>("cloudview");
	}
	
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(80);
	}

	public String getPluginUIPath()
	{
		return "jadex/tools/web/cloudview/cloudview.tag";
	}

}
