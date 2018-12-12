package jadex.webservice.examples.ws.quote;

import jadex.extension.ws.invoke.WebServiceAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@Imports({"jadex.extension.ws.invoke.*", "jadex.webservice.examples.ws.quote.gen.*"})
@ProvidedServices(@ProvidedService(type=IQuoteService.class, implementation=@Implementation(
	expression="$pojoagent.createServiceImplementation(IQuoteService.class, new WebServiceMappingInfo(StockQuote.class, \"getStockQuoteSoap\"))")))
public class QuoteWebServiceAgent extends WebServiceAgent
{
}
