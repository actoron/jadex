package jadex.platform.service.message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
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
import jadex.platform.service.transport.intravm.IntravmTransportAgent;

/**
 *  Try to test robustness of the initial messaging handshake when a new platform is not yet inited. 
 */
@Ignore
public class InitialMessagingHandshakeTest
{
	static final long	SEND_TIMEOUT	= 3000;

	@Test
	public void	testRecoveryFromMissingReceiverTransport()
	{
		// Sender platform without awareness
    	IPlatformConfiguration senderconf = STest.getRealtimeTestConfig(getClass());
    	senderconf.setValue("intravmawareness", false);
    	IFuture<IExternalAccess>	fsender	= Starter.createPlatform(senderconf);
		
		// Receiver platform without awareness and (initially) without transport
    	IPlatformConfiguration receiverconf = STest.getRealtimeTestConfig(getClass());
    	receiverconf.setValue("intravmawareness", false);
    	receiverconf.setValue("intravm", false);
    	IFuture<IExternalAccess>	freceiver	= Starter.createPlatform(receiverconf);
    	
    	IExternalAccess	sender	= fsender.get();
    	IExternalAccess	receiver	= freceiver.get();
    	
    	// No transport on receiver -> should fail immediately due to "No transport addresses"
		sendForFailure("Send without receiver transport", sender, receiver.getId());
    	
    	// Start transport on receiver and retry
    	// No receiver addresses known by sender -> should still fail immediately due to "No transport addresses"
    	IntravmTransportAgent	intravm	= new IntravmTransportAgent();
    	receiver.addComponent(intravm).get();
		sendForFailure("Send without receiver address", sender, receiver.getId());

    	// Start catalog awareness on sender and retry -> should still fail immediately due to "No transport addresses"
    	IExternalAccess	catalog	= sender.createComponent(new CreationInfo()
    		.setFilenameClass(CatalogAwarenessAgent.class)
    		.addArgument("platformurls", "")).get();
		sendForFailure("Send with empty catalog", sender, receiver.getId());
    	
		// Add address to catalog and retry -> should eventually succeed.
    	List<TransportAddress>	addresses	= receiver.searchService(new ServiceQuery<>(ITransportAddressService.class)).get().getAddresses(receiver.getId()).get();
    	String	address	= "intravm://"+receiver.getId()+"@"+addresses.get(0).getAddress();
    	System.out.println("address: "+address);
    	catalog.scheduleStep(ia -> ((CatalogAwarenessAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent()).addPlatform(address));
    	sendForRecovery(sender, receiver.getId());
	}

	/**
	 *  Check that message sending fails immediately.
	 *  @param msg	Test case explanation for printout.
	 *  @param sender	The sender platform access.
	 *  @param receiver	The receiver id.
	 */
	protected void sendForFailure(String msg, IExternalAccess sender, IComponentIdentifier receiver)
	{
		long	start	= System.nanoTime();
    	try
    	{
    		sender.scheduleStep(ia -> ia.getFeature(IMessageFeature.class).sendMessage("Hi!", receiver))
    			.get(SEND_TIMEOUT);
    		fail("Sending unexpectedly succeeded.");
    	}
    	catch(Exception e)
    	{
    		System.out.println("Expecting failure: "+msg+" took "+((System.nanoTime()-start)/1000000)/1000.0+" seconds: "+e);
    		assertFalse("Exception shouldn't be CTE: "+SUtil.getExceptionStacktrace(e), e instanceof ComponentTerminatedException);
    		assertFalse("Exception shouldn't be timeout: "+SUtil.getExceptionStacktrace(e), e instanceof TimeoutException);
    	}
	}

	/**
	 *  Check that message sending works eventually when retrying.
	 *  @param sender	The sender platform access.
	 *  @param receiver	The receiver id.
	 */
	protected void sendForRecovery(IExternalAccess sender, IComponentIdentifier receiver)
	{
		long	start	= System.nanoTime();
		while((System.nanoTime()-start)/1000000.0<Starter.getScaledDefaultTimeout(sender.getId(), 2))
		{
	    	try
	    	{
	    		sender.scheduleStep(ia -> ia.getFeature(IMessageFeature.class).sendMessage("Hi!", receiver))
	    			.get(SEND_TIMEOUT);
	    		System.out.println("Sending succeeded after "+((System.nanoTime()-start)/1000000)/1000.0+" seconds.");
	    		return;
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println("Temporary send failure after "+((System.nanoTime()-start)/1000000)/1000.0+" seconds: "+e);
	    		assertFalse("Exception shouldn't be CTE: "+SUtil.getExceptionStacktrace(e), e instanceof ComponentTerminatedException);
	    		assertFalse("Exception shouldn't be timeout: "+SUtil.getExceptionStacktrace(e), e instanceof TimeoutException);
	    	}
	    	
	    	sender.waitForDelay(SEND_TIMEOUT).get();
		}
		
		fail("Sending did not recover after "+((System.nanoTime()-start)/1000000)/1000.0+" seconds");
	}
}
