package jadex.micro.testcases.semiautomatic.message;

import java.util.Collections;

import jadex.base.IRootComponentConfiguration.AWAMECHANISM;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

/**
 *  Test remote message sending.
 */
public class RemoteMessagingTest
{
	/**
	 *  Start two agents on separate platforms and exchange a request/reply.
	 */
	public static void main(String[] args) throws Exception
	{
//		String key = SUtil.createRandomKey();
		
		// Start first platform with receiver.
		PlatformConfiguration	config1	= PlatformConfiguration.getMinimal();
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.setSecurity(true);
		config1.setAwaMechanisms(AWAMECHANISM.local);
		config1.setAwareness(true);
		config1.addComponent("jadex.platform.service.transport.tcp.TcpTransportAgent.class");
//		config1.addComponent("jadex.platform.service.message.websockettransport.WebSocketTransportAgent.class");
		config1.addComponent(ReceiverAgent.class);
//		config1.setNetworkName("remotemessagetest");
//		config1.setNetworkPass(key);
		IExternalAccess	access1	= Starter.createPlatform(config1).get();		
//		TransportAddressBook	tab1	= TransportAddressBook.getAddressBook(access1.getComponentIdentifier());
//		System.out.println("TCP Addresses: " + Arrays.toString(tab1.getPlatformAddresses(access1.getComponentIdentifier(), "tcp")));
		
		// Start second platform
		PlatformConfiguration	config2	= PlatformConfiguration.getMinimal();
//		config2.setLogging(true);
//		config2.setDefaultTimeout(-1);
		config2.setSecurity(true);
		config2.setAwaMechanisms(AWAMECHANISM.local);
		config2.setAwareness(true);
		config2.addComponent("jadex.platform.service.transport.tcp.TcpTransportAgent.class");
//		config2.addComponent("jadex.platform.service.message.websockettransport.WebSocketTransportAgent.class");
//		config2.setNetworkName("remotemessagetest");
//		config2.setNetworkPass(key);
		IExternalAccess	access2	= Starter.createPlatform(config2).get();
		IComponentManagementService	cms	= SServiceProvider.getService(access2, IComponentManagementService.class).get();

		// Add addresses of first platform to second
//		TransportAddressBook	tab2	= TransportAddressBook.getAddressBook(access2.getComponentIdentifier());
//		tab2.addPlatformAddresses(access1.getComponentIdentifier(), "tcp",
//			tab1.getPlatformAddresses(access1.getComponentIdentifier(), "tcp"));
		
		// Add addresses of second platform to first
//		tab1.addPlatformAddresses(access2.getComponentIdentifier(), "tcp",
//				tab2.getPlatformAddresses(access2.getComponentIdentifier(), "tcp"));
		
		// Start sender with receiver CID on remote platform.
//		cms.createComponent(SenderAgent.class.getName()+".class",
		cms.createComponent(BenchmarkAgent.class.getName()+".class",
			new CreationInfo(Collections.singletonMap("receiver",
				(Object)new BasicComponentIdentifier("Receiver", access1.getComponentIdentifier())))).get();
	}
}
