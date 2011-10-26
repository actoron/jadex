package jadex.base.service.message.transport.btmtp;

import jadex.android.bluetooth.CustomTestRunner;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CustomTestRunner.class)
public class BTTransportTest extends TestCase {

	private BTTransport btTransport;
	private MessageEnvelope message;


	@Before
	public void setUp() {
		IServiceProvider sp = createServiceProvider();
		btTransport = new BTTransport(sp);
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("key1", "val1");
		map.put("key2", "val2");
		
		ArrayList<IComponentIdentifier> receivers = new ArrayList<IComponentIdentifier>();
		
		message = new MessageEnvelope(map, receivers, "testformat");
		
		btTransport.codecfac = new CodecFactory();
		btTransport.classLoader = BTTransport.class.getClassLoader();
	}

	@Test
	public void testEncodeDecode () {
		byte[] encoded = btTransport.encodeMessage(message, new byte[0]);
		MessageEnvelope message2 = btTransport.decodeMessage(encoded);
		
		String typeName = message2.getTypeName();
		assertEquals("testformat", typeName);
		
		Map map = message2.getMessage();
		assertEquals("val1", map.get("key1"));
		assertEquals("val2", map.get("key2"));
				
		assertTrue(Arrays.equals(message.getReceivers(), message2.getReceivers()));
	}
	
	
	private IServiceProvider createServiceProvider() {
		return new IServiceProvider() {
			
			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IIntermediateFuture<IService> getServices(ISearchManager manager,
					IVisitDecider decider, IResultSelector selector) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IFuture<IServiceProvider> getParent() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IFuture<Collection<IServiceProvider>> getChildren() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
