package jadex.webservice.examples.rs.chart;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Imports("jadex.extension.rs.invoke.SRest")
@ProvidedServices({@ProvidedService(type = IChartService.class, implementation = @Implementation(expression = "SRest.createServiceImplementation($component, IChartService.class, IRSChartService.class)"))})
@Agent
public class ChartProviderAgent 
{
}
