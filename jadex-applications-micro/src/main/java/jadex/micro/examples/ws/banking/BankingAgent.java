package jadex.micro.examples.ws.banking;

import jadex.bridge.service.types.publish.IPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

/**
 * 
 */
@Agent
@Imports({"jadex.base.service.ws.*", "jadex.micro.examples.ws.offerquote.gen.*"})
@ProvidedServices(@ProvidedService(type=IBankingService.class, implementation=@Implementation(BankingService.class),
	publish=@Publish(type=IPublishService.PUBLISH_WS, publishid="http://localhost:8080/banking", servicetype=IWSBankingService.class)))
public class BankingAgent
{
}