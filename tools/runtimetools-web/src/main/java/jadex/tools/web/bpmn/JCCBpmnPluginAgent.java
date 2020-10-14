package jadex.tools.web.bpmn;

import jadex.bridge.IInternalAccess;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Security web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="bpmnweb", type=IJCCBpmnService.class)})
@Agent(autostart=Boolean3.FALSE)
public class JCCBpmnPluginAgent extends JCCPluginAgent implements IJCCBpmnService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("BPMN");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(90);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/bpmn/bpmn.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return new Future<>((byte[])null);
	}
}
