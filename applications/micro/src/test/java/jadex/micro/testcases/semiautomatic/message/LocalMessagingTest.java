package jadex.micro.testcases.semiautomatic.message;

import java.util.HashMap;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

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
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
//		config.setLogging(true);
		config.addComponent(ReceiverAgent.class);
//		config.addComponent(SenderAgent.class, Collections.singletonMap(...));
//		Starter.createPlatform(config).get();
		
		final IExternalAccess	access	= Starter.createPlatform(config).get();
		//cms.createComponent(SenderAgent.class.getName()+".class",
		access.createComponent(null,
			new CreationInfo(new HashMap<String, Object>(){{
				put("receiver", (Object)new BasicComponentIdentifier("Receiver", access.getId()));
				put("count", 100000);
			}}).setFilename(BenchmarkAgent.class.getName()+".class")).get();
	}
}
