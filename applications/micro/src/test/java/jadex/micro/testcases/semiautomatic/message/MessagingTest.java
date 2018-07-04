package jadex.micro.testcases.semiautomatic.message;

import java.util.Collections;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

/**
 *  Test remote message sending.
 */
public class MessagingTest
{
	protected static boolean	remote	= false;
	protected static Class<?>	sender	= FipaSenderAgent.class;
	protected static Class<?>	receiver	= FipaReceiverAgent.class;
	
	/**
	 *  Start two agents on separate platforms and exchange a request/reply.
	 */
	public static void main(String[] args) throws Exception
	{
		// Shared platform config.
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
//		config.setLogging(true);
//		config.setDefaultTimeout(-1);
		config.getExtendedPlatformConfiguration().setSecurity(true);
		config.getExtendedPlatformConfiguration().setAwaMechanisms(IPlatformConfiguration.AWAMECHANISM_LOCAL);
		config.setAwareness(true);
		config.getExtendedPlatformConfiguration().setTcpTransport(true);
//		config.setNetworkName("remotemessagetest");
//		config.setNetworkPass(key);

		
		// Start receiver.
		IExternalAccess	pf_receiver	= Starter.createPlatform(config).get();		
		IComponentManagementService	cms	= SServiceProvider.searchService(pf_receiver, new ServiceQuery<>( IComponentManagementService.class)).get();
		IComponentIdentifier	cid_receiver	= cms.createComponent(receiver.getName()+".class", null).getFirstResult();
				
		// Start sender with receiver CID.
		IExternalAccess	pf_sender;
		if(!remote)
		{
			pf_sender	= pf_receiver;
		}
		else
		{
			// Start second platform
			pf_sender	= Starter.createPlatform(config).get();
		}
		cms	= SServiceProvider.searchService(pf_sender, new ServiceQuery<>( IComponentManagementService.class)).get();
		cms.createComponent(sender.getName()+".class",
			new CreationInfo(Collections.singletonMap("receiver", (Object)cid_receiver))).get();
	}
}
