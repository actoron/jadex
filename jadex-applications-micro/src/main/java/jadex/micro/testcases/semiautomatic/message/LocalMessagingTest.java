package jadex.micro.testcases.semiautomatic.message;

import java.util.Collections;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

/**
 *  Test local message sending.
 */
public class LocalMessagingTest
{
	/**
	 *  Start two agents and exchange a request/reply.
	 */
	public static void main(String[] args)
	{
		PlatformConfiguration	config	= PlatformConfiguration.getMinimal();
//		config.setLogging(true);
		config.addComponent(ReceiverAgent.class);
//		config.addComponent(SenderAgent.class, Collections.singletonMap(...));
//		Starter.createPlatform(config).get();
		
		IExternalAccess	access	= Starter.createPlatform(config).get();
		IComponentManagementService	cms	= SServiceProvider.getService(access, IComponentManagementService.class).get();
		cms.createComponent(SenderAgent.class.getName()+".class",
			new CreationInfo(Collections.singletonMap("receiver",
				(Object)new BasicComponentIdentifier("Receiver", access.getComponentIdentifier())))).get();
	}
}
