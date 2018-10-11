package jadex.micro.testcases.semiautomatic.message;

import java.util.Collections;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

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
		IComponentIdentifier cid_receiver = pf_receiver.createComponent(new CreationInfo().setFilename(receiver.getName()+".class")).get().getId();
				
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
		pf_sender.createComponent(
			new CreationInfo(Collections.singletonMap("receiver", (Object)cid_receiver)).setFilename(sender.getName()+".class")).get();
	}
}
