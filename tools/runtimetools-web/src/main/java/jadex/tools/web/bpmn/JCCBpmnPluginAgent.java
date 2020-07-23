package jadex.tools.web.bpmn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Security web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="bpmnweb", type=IJCCBpmnService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCBpmnPluginAgent extends JCCPluginAgent implements IJCCBpmnService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The plugin component string. */
	protected String component;
	
	
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
	
	
}
