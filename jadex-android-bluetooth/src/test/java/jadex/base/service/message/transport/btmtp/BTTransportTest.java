package jadex.base.service.message.transport.btmtp;

import jadex.android.bluetooth.CustomTestRunner;
import jadex.base.fipa.SFipa;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.base.service.message.transport.codecs.JadexXMLCodec;
import jadex.base.service.remote.RemoteServiceContainer;
import jadex.base.service.remote.commands.RemoteSearchCommand;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.BasicResultSelector;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.xml.ObjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.bean.JavaWriter;
import jadex.xml.writer.WriteContext;
import jadex.xml.writer.Writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javaxx.xml.namespace.QName;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CustomTestRunner.class)
public class BTTransportTest extends TestCase {

	private BTTransport btTransport;
	private MessageEnvelope message;
	private Writer writer;


	@Before
	public void setUp() {
		IServiceProvider sp = createServiceProvider();
		btTransport = new BTTransport(sp);
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("key1", "val1");
		map.put("key2", "val2");
		
		ArrayList<IComponentIdentifier> receivers = new ArrayList<IComponentIdentifier>();
		
		message = new MessageEnvelope(map, receivers, SFipa.FIPA_MESSAGE_TYPE.getName());
		
		btTransport.codecfac = new CodecFactory();
		btTransport.classLoader = BTTransport.class.getClassLoader();
		
		
		Set typeinfoswrite = JavaWriter.getTypeInfos();
//		final RMIPreProcessor preproc = new RMIPreProcessor(rrm);
//		TypeInfo ti_proxyable = new TypeInfo(new XMLInfo(pr, null, false, preproc), 
//			new ObjectInfo(IRemotable.class));
//		typeinfoswrite.add(ti_proxyable);
		
		this.writer = new Writer(new BeanObjectWriterHandler(typeinfoswrite, true))
		{
			public void writeObject(WriteContext wc, Object object, QName tag) throws Exception 
			{
				
				if(object!=null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class))
				{
					{
						object = BasicServiceInvocationHandler.getPojoServiceProxy(object);
					}
				}
				
				super.writeObject(wc, object, tag);
			};
		};
	}

	@Test
	public void testEncodeDecode () {
		byte[] encoded = btTransport.encodeMessage(message, new byte[]{JadexXMLCodec.CODEC_ID, GZIPCodec.CODEC_ID});
		MessageEnvelope message2 = btTransport.decodeMessage(encoded);
		
		String typeName = message2.getTypeName();
		assertEquals(SFipa.FIPA_MESSAGE_TYPE.getName(), typeName);
		
		Map map = message2.getMessage();
		assertEquals("val1", map.get("key1"));
		assertEquals("val2", map.get("key2"));
				
		assertTrue(Arrays.equals(message.getReceivers(), message2.getReceivers()));
	}
	
	@Test
	public void testEncodeDecodeComplex () {
		
		ISearchManager sm = SServiceProvider.getSearchManager(false,
				RequiredServiceInfo.SCOPE_GLOBAL);
		IVisitDecider vd = SServiceProvider.getVisitDecider(false,
				RequiredServiceInfo.SCOPE_GLOBAL);
		BasicResultSelector rs = new BasicResultSelector();
		
		
		RemoteSearchCommand rsc = new RemoteSearchCommand("rms@anywhere", sm, vd, rs, "1");
		
		String cont = Writer.objectToXML(writer, rsc, getClass().getClassLoader(), new ComponentIdentifier("testcomponent"));
		
		HashMap<String, String> msg = new HashMap<String,String>();
		msg.put(SFipa.CONTENT, cont);
		
		for (int i=0; i < 10; i++) {
			msg.put(""+i, cont);
		}
		
		message.setMessage(msg);
		
		byte[] encoded = btTransport.encodeMessage(message, new byte[]{JadexXMLCodec.CODEC_ID, GZIPCodec.CODEC_ID});
		MessageEnvelope message2 = btTransport.decodeMessage(encoded);
		
		String typeName = message2.getTypeName();
		assertEquals(SFipa.FIPA_MESSAGE_TYPE.getName(), typeName);
		
		Map message3 = message2.getMessage();
		
		assertEquals(msg, message3);
		
//		Map map = message2.getMessage();
//		assertEquals("val1", map.get("key1"));
//		assertEquals("val2", map.get("key2"));
//				
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
