package jadex.webservice.examples.rs.banking;

import jadex.bridge.service.types.publish.IPublishService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;

/**
 *  Banking agent that offers a banking service.
 * 
 *  This example shows how a Jadex service can be automatically published
 *  as web service with a synchronous interfaces.
 *  
 *  The @Publish annotation requires the publishing type (e.g. web service),
 *  the id (e.g. a web service url) and the service type, i.e. interface to
 *  be specified.
 */
@Agent
//@Imports({"jadex.base.service.ws.*", "jadex.micro.examples.ws.offerquote.gen.*"})
@ProvidedServices(@ProvidedService(type=IBankingService.class, implementation=@Implementation(BankingService.class),
//	publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="http://localhost:8080/banking")))
	publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="http://localhost:8080/", servicetype=RSBankingService.class)))
public class BankingAgent
{
}