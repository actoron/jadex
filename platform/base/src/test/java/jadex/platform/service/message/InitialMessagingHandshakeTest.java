package jadex.platform.service.message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.future.IFuture;
import jadex.platform.service.awareness.CatalogAwarenessAgent;

/**
 *  Try to test robustness of the initial messaging handshake when a new platform is not yet inited. 
 */
@Ignore
public class InitialMessagingHandshakeTest
{
	@Test
	public void	testInitialMessagingHandshake()
	{
		long	send_timeout	= 3000;
		
		// Sender platform without awareness
    	IPlatformConfiguration senderconf = STest.getDefaultTestConfig(getClass());
    	senderconf.setValue("intravmawareness", false);
    	IFuture<IExternalAccess>	sender	= Starter.createPlatform(senderconf);
		
		// Receiver platform without awareness and (initially) without transport
    	IPlatformConfiguration receiverconf = STest.getDefaultTestConfig(getClass());
    	receiverconf.setValue("intravmawareness", false);
    	receiverconf.setValue("intravm", false);
//    	receiverconf.setLogging(true);
    	IFuture<IExternalAccess>	receiver	= Starter.createPlatform(receiverconf);
    	
    	// No transport on receiver -> should fail immediately due to "No transport addresses"
    	try
    	{
    		sendMessage(sender.get(), receiver.get()).get(send_timeout);
    		fail("Sending unexpectedly succeeded.");
    	}
    	catch(Exception e)
    	{
    		assertFalse("Exception shouldn't be CTE: "+SUtil.getExceptionStacktrace(e), e instanceof ComponentTerminatedException);
    		assertFalse("Exception shouldn't be timeout: "+SUtil.getExceptionStacktrace(e), e instanceof TimeoutException);
    	}
    	
    	// Start transport on receiver and retry
    	// No receiver addresses known by sender -> should still fail immediately due to "No transport addresses"
    	IntravmTestTransportAgent	intravm	= new IntravmTestTransportAgent();
    	receiver.get().addComponent(intravm);
    	intravm.initing().get();
    	try
    	{
    		sendMessage(sender.get(), receiver.get()).get(send_timeout);
    		fail("Sending unexpectedly succeeded.");
    	}
    	catch(Exception e)
    	{
    		Assert.assertFalse("Exception shouldn't be CTE: "+SUtil.getExceptionStacktrace(e), e instanceof ComponentTerminatedException);
    		Assert.assertFalse("Exception shouldn't be timeout: "+SUtil.getExceptionStacktrace(e), e instanceof TimeoutException);
    	}
    	
    	// Start catalog awareness on sender and retry -> should still fail immediately due to "No transport addresses"
    	IExternalAccess	catalog	= sender.get().createComponent(new CreationInfo()
    		.setFilenameClass(CatalogAwarenessAgent.class)
    		.addArgument("platformurls", "")).get();
    	try
    	{
    		sendMessage(sender.get(), receiver.get()).get(send_timeout);
    		fail("Sending unexpectedly succeeded.");
    	}
    	catch(Exception e)
    	{
    		Assert.assertFalse("Exception shouldn't be CTE: "+SUtil.getExceptionStacktrace(e), e instanceof ComponentTerminatedException);
    		Assert.assertFalse("Exception shouldn't be timeout: "+SUtil.getExceptionStacktrace(e), e instanceof TimeoutException);
    	}
    	
    	// Add receiver to catalog and retry -> should now work.
    	intravm.kickoff().get();
    	intravm.doReturn();
    	
    	List<TransportAddress>	addresses	= receiver.get().searchService(new ServiceQuery<>(ITransportAddressService.class)).get().getAddresses(receiver.get().getId()).get();
    	String	address	= "intravm://"+receiver.get().getId()+"@"+addresses.get(0).getAddress();
    	System.out.println("address: "+address);
    	catalog.scheduleStep(ia -> ((CatalogAwarenessAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent()).addPlatform(address));
    	try
    	{
    		sendMessage(sender.get(), receiver.get()).get(send_timeout);
    	}
    	catch(Exception e)
    	{
    		Assert.fail("Unexpected exception: "+SUtil.getExceptionStacktrace(e));
    	}

	}
	
	/**
	 *  Try sending a message from one platform to another.
	 */
	protected IFuture<Void>	sendMessage(IExternalAccess sender, IExternalAccess receiver)
	{
		return sender.scheduleStep(ia -> ia.getFeature(IMessageFeature.class).sendMessage("Hi!", receiver.getId()));
	}
}
