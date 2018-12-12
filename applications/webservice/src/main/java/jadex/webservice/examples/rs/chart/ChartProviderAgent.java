package jadex.webservice.examples.rs.chart;

import jadex.extension.rs.invoke.RestServiceAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that wraps a normal rest service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IChartService.class, implementation=@Implementation(
	expression="$pojoagent.createServiceImplementation(IChartService.class, IRSChartService.class)")))
public class ChartProviderAgent extends RestServiceAgent
{
}
