package jadex.micro.examples.ws.banking;

import jadex.bridge.service.types.publish.IPublishService;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

import javax.xml.ws.Endpoint;

/**
 * 
 */
@Agent
@Imports({"jadex.base.service.ws.*", "jadex.micro.examples.ws.offerquote.gen.*"})
@ProvidedServices(@ProvidedService(type=IBankingService.class, implementation=@Implementation(BankingService.class),
	publish=@Publish(type=IPublishService.PUBLISH_WS, publishid="http://localhost:8080/quote", servicetype=IWSBankingService.class)))
public class BankingAgent
{
//	@Agent
//	protected MicroAgent agent;
//	
//	/**
//	 * 
//	 */
//	@AgentCreated
//	public void init()
//	{
//		// Publish service as web service
//		IBankingService qs = (IBankingService)agent.getServiceContainer().getProvidedServices(IBankingService.class)[0];
//		WSBankingService qws = new WSBankingService(qs);
//		Endpoint endpoint = Endpoint.publish("http://localhost:8080/quote", qws);
//		System.out.println("Server startet: "+endpoint);
//	}
}