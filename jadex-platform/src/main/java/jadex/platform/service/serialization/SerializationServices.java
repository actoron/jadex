package jadex.platform.service.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.IStarterConfiguration;
import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.serialization.IRemoteReferenceManagement;
import jadex.bridge.service.types.serialization.IRemoteReferenceModule;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.IRootObjectContext;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.platform.service.message.transport.codecs.GZIPCodec;
import jadex.platform.service.message.transport.codecs.LZ4Codec;
import jadex.platform.service.message.transport.codecs.SnappyCodec;
import jadex.platform.service.message.transport.codecs.XZCodec;
import jadex.platform.service.message.transport.serializers.JadexBinarySerializer;
import jadex.platform.service.message.transport.serializers.JadexJsonSerializer;
import jadex.platform.service.remote.ProxyReference;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.platform.service.remote.commands.AbstractRemoteCommand;

/**
 *  Functionality for managing serialization.
 *
 */
public class SerializationServices implements ISerializationServices
{
	/** The remote reference management. */
	protected RemoteReferenceManagement rrmanagement;
	
	/** The remote reference module */
	protected IRemoteReferenceModule rrm;
	
	/** Serializer used for sending. */
	protected ISerializer sendserializer;
	
	/** All available serializers */
	protected Map<Integer, ISerializer> serializers;
	
	/** Codecs used for sending. */
	protected ICodec[] sendcodecs;
	
	/** All available codecs. */
	protected Map<Integer, ICodec> codecs;
	
	/** Preprocessors for encoding. */
	ITraverseProcessor[] preprocessors;
	
	/** Postprocessors for decoding. */
	ITraverseProcessor[] postprocessors;

	/** Creates the management. */
	public SerializationServices()
	{
		rrmanagement = new RemoteReferenceManagement();
		serializers = new HashMap<Integer, ISerializer>();
		ISerializer serial = new JadexBinarySerializer();
		serializers.put(serial.getSerializerId(), serial);
		serial = new JadexJsonSerializer();
		serializers.put(serial.getSerializerId(), serial);
		sendserializer = serializers.get(0);
		codecs = new HashMap<Integer, ICodec>();
		ICodec codec = new SnappyCodec();
		codecs.put(codec.getCodecId(), codec);
		codec = new GZIPCodec();
		codecs.put(codec.getCodecId(), codec);
		codec = new LZ4Codec();
		codecs.put(codec.getCodecId(), codec);
		codec = new XZCodec();
		codecs.put(codec.getCodecId(), codec);
		sendcodecs = new ICodec[] { codecs.get(3) };
		List<ITraverseProcessor> procs = createPreprocessors();
		preprocessors = procs.toArray(new ITraverseProcessor[procs.size()]);
		procs = createPostprocessors();
		postprocessors = procs.toArray(new ITraverseProcessor[procs.size()]);
	}
	
	/**
	 *  Encodes/serializes an object for a particular receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param cl The classloader used for encoding.
	 *  @param obj Object to be encoded.
	 *  @return Encoded object.
	 */
	public byte[] encode(IMsgHeader header, ClassLoader cl, Object obj)
	{
		IComponentIdentifier receiver = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
		ISerializer serial = getSendSerializer(receiver);
		byte[] enc = serial.encode(obj, cl, getPreprocessors());
		ICodec[] codecs = getSendCodecs(receiver);
		int codecsize = 0;
		if (codecs != null)
		{
			codecsize = codecs.length;
			for (int i = 0; i < codecsize; ++i)
				enc = codecs[i].encode(enc);
		}
		int prefixsize = getPrefixSize(codecsize);
		byte[] ret = new byte[prefixsize+enc.length];
		System.arraycopy(enc, 0, ret, prefixsize, enc.length);
		enc = null;
		SUtil.shortIntoBytes((short) prefixsize, ret, 0);
		SUtil.intIntoBytes(serial.getSerializerId(), ret, 2);
		SUtil.shortIntoBytes((short) codecsize, ret, 6);
		if (codecsize > 0)
		{
			for (int i = 0; i < codecsize; ++i)
				SUtil.intIntoBytes(codecs[i].getCodecId(), ret, (i<<2) + 8);
		}
		return ret;
	}
	
	/**
	 *  Decodes/deserializes an object.
	 *  
	 *  @param cl The classloader used for decoding.
	 *  @param enc Encoded object.
	 *  @return Object to be encoded.
	 *  
	 */
	public Object decode(ClassLoader cl, byte[] enc)
	{
		Object ret = null;
		
		// Check if this makes any sense at all.
		if (enc != null && enc.length > 7)
		{
			int prefixsize = SUtil.bytesToShort(enc, 0) & 0xFFFF;
			try
			{
				int codecsize = (SUtil.bytesToShort(enc, 6) & 0xFFFF);
				
				if (prefixsize >= getPrefixSize(codecsize))
				{
					byte[] raw = null;
					
					if (codecsize > 0)
					{
						int offset = prefixsize;
						raw = enc;
						for (int i = codecsize - 1; i >= 0; --i)
						{
							raw = getCodecs().get(SUtil.bytesToInt(enc, (i << 4) + 8)).decode(raw, offset, raw.length - offset);
							offset = 0;
						}
					}
					else
					{
						raw = new byte[enc.length - prefixsize];
						System.arraycopy(enc, prefixsize, raw, 0, raw.length);
					}
					
					ISerializer serial = getSerializers().get(SUtil.bytesToInt(enc, 2));
					ret = serial.decode(raw, cl, getPostprocessors(), null);
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				ret = null;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Gets the remote reference management.
	 *
	 *  @return The remote reference management.
	 */
	public IRemoteReferenceManagement getRemoteReferenceManagement()
	{
		return rrmanagement;
	}

	/**
	 *  Gets the remote reference module.
	 *
	 *  @return The remote reference module.
	 */
	public IRemoteReferenceModule getRemoteReferenceModule()
	{
		return rrm;
	}

	/**
	 *  Sets the remote reference module.
	 *
	 *  @param rrm The remote reference module.
	 */
	public void setRemoteReferenceModule(IRemoteReferenceModule rrm)
	{
		this.rrm = rrm;
	}
	
	/**
	 *  Returns the serializer for sending.
	 *  
	 *  @param receiver Receiving platform.
	 *  @return Serializer.
	 */
	public ISerializer getSendSerializer(IComponentIdentifier receiver)
	{
//		return (ISerializer) PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SEND_SERIALIZER);
		return sendserializer;
	}
	
	/**
	 *  Returns all serializers.
	 *  
	 *  @param platform Sending platform.
	 *  @return Serializers.
	 */
	public Map<Integer, ISerializer> getSerializers()
	{
		return serializers;
	}
	
	/**
	 *  Returns the codecs for sending.
	 *  
	 *  @param receiver Receiving platform.
	 *  @return Codecs.
	 */
	public ICodec[] getSendCodecs(IComponentIdentifier receiver)
	{
		return sendcodecs;
	}
	
	/**
	 *  Returns all codecs.
	 *  
	 *  @return Codecs.
	 */
	public Map<Integer, ICodec> getCodecs()
	{
		return codecs;
	}
	
	/**
	 *  Gets the post-processors for decoding a received message.
	 */
	public ITraverseProcessor[] getPostprocessors()
	{
		return postprocessors;
	}
	
	/**
	 *  Gets the pre-processors for encoding a received message.
	 */
	public ITraverseProcessor[] getPreprocessors()
	{
		return preprocessors;
	}
	
	/**
	 * 
	 */
	public List<ITraverseProcessor> createPostprocessors()
	{
		// Equivalent pre- and postprocessors for binary mode.
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		
		// Proxy reference -> proxy object
		ITraverseProcessor rmipostproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return ProxyReference.class.equals(type);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					Object ret= ((RemoteReferenceModule) rrm).getProxy((ProxyReference)object, targetcl);
					return ret;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
		procs.add(rmipostproc);
		
		// TODO: Re-enable for streams!
//		procs.add(new ITraverseProcessor()
//		{
//			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
//			{
//				return ServiceInputConnectionProxy.class.equals(type);
//			}
//			
//			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
//			{
//				try
//				{
//					ServiceInputConnectionProxy icp = (ServiceInputConnectionProxy)object;
//					IInputConnection icon = ((MessageService)msgservice).getParticipantInputConnection(icp.getConnectionId(), 
//						icp.getInitiator(), icp.getParticipant(), icp.getNonFunctionalProperties());
//					return icon;
//				}
//				catch(RuntimeException e)
//				{
//					e.printStackTrace();
//					throw e;
//				}
//			}
//		});
		
		// TODO: Re-enable for streams!
//		procs.add(new ITraverseProcessor()
//		{
//			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
//			{
//				return ServiceOutputConnectionProxy.class.equals(type);
//			}
//			
//			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
//			{
//				try
//				{
//					ServiceOutputConnectionProxy ocp = (ServiceOutputConnectionProxy)object;
//					IOutputConnection ocon = ((MessageService)msgservice).getParticipantOutputConnection(ocp.getConnectionId(), 
//						ocp.getInitiator(), ocp.getParticipant(), ocp.getNonFunctionalProperties());
//					return ocon;
//				}
//				catch(RuntimeException e)
//				{
//					e.printStackTrace();
//					throw e;
//				}
//			}
//		});
		
		return procs;
	}
	
	/**
	 * 
	 */
	public List<ITraverseProcessor> createPreprocessors()
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		
		// Update component identifiers with addresses
//		ITraverseProcessor bpreproc = new ITraverseProcessor()
//		{
//			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
//			{
//				Class<?> clazz = SReflect.getClass(type);
//				return ComponentIdentifier.class.equals(clazz);
//			}
//			
//			public Object process(Object object, Type type, List<ITraverseProcessor> processors, Traverser traverser,
//				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
//			{
//				try
//				{
//					IComponentIdentifier src = (IComponentIdentifier)object;
//					BasicComponentIdentifier ret = null;
//					if(src.getPlatformName().equals(component.getComponentIdentifier().getRoot().getLocalName()))
//					{
//						String[] addresses = ((MessageService)msgservice).internalGetAddresses();
//						ret = new ComponentIdentifier(src.getName(), addresses);
//					}
//					
//					return ret==null? src: ret;
//				}
//				catch(RuntimeException e)
//				{
//					e.printStackTrace();
//					throw e;
//				}
//			}
//		};
//		procs.add(bpreproc);
		
		// Handle pojo services
		ITraverseProcessor bpreproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return object != null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					return BasicServiceInvocationHandler.getPojoServiceProxy(object);
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
			}
		};
		procs.add(bpreproc);

		// Handle remote references
		bpreproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
//				if(marshal.isRemoteReference(object))
//					System.out.println("rr: "+object);
				return rrmanagement.isRemoteReference(object);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					IComponentIdentifier receiver = getRCFromContext(context).getReceiver();
					Object ret = rrm.getProxyReference(object, receiver, targetcl);
					return ret;
//					return rrm.getProxyReference(object, receiver, ((IEncodingContext)context).getClassLoader());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
		procs.add(bpreproc);
		
		// output connection as result of call
		// TODO: Re-enable for streams!
//		procs.add(new ITraverseProcessor()
//		{
//			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
//			{
//				try
//				{
//					AbstractRemoteCommand com = getRCFromContext(context);
//					ServiceInputConnectionProxy con = (ServiceInputConnectionProxy)object;
//					OutputConnection ocon = ((MessageService)msgservice).internalCreateOutputConnection(
//						RemoteServiceManagementService.this.component.getComponentIdentifier(), com.getReceiver(), com.getNonFunctionalProperties());
//					con.setOutputConnection(ocon);
//					con.setConnectionId(ocon.getConnectionId());
//					return con;
//				}
//				catch(RuntimeException e)
//				{
//					e.printStackTrace();
//					throw e;
//				}
//			}
//			
//			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
//			{
//				return object instanceof ServiceInputConnectionProxy;
//			}
//		});
		
		// input connection proxy as result of call
		// TODO: Re-enable for streams!
//		procs.add(new ITraverseProcessor()
//		{
//			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
//			{
//				try
//				{
//					AbstractRemoteCommand com = (AbstractRemoteCommand)((IRootObjectContext)context).getRootObject();
//					ServiceOutputConnectionProxy con = (ServiceOutputConnectionProxy)object;
//					InputConnection icon = ((MessageService)msgservice).internalCreateInputConnection(
//						RemoteServiceManagementService.this.component.getComponentIdentifier(), com.getReceiver(), com.getNonFunctionalProperties());
//					con.setConnectionId(icon.getConnectionId());
//					con.setInputConnection(icon);
//					return con;
//				}
//				catch(RuntimeException e)
//				{
//					e.printStackTrace();
//					throw e;
//				}
//			}
//			
//			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
//			{
//				return object instanceof ServiceOutputConnectionProxy;
//			}
//		});
		
		return procs;
	}
	
	protected static final int getPrefixSize(int codeccount)
	{
		// prefixsize[2] | serializerid[4] | codeccount[2] | codecid[4]...
		return 8 + (codeccount << 4);
	}
	
	@SuppressWarnings("unchecked")
	protected static AbstractRemoteCommand getRCFromContext(Object ec)
	{
		return ((AbstractRemoteCommand)((Map<String, Object>)((IRootObjectContext)ec).getRootObject()).get(SFipa.CONTENT));
//		return ((AbstractRemoteCommand)((MessageEnvelope)((IEncodingContext)ec).getRootObject()).getMessage().get(SFipa.CONTENT));
	}
	
	/**
	 *  Gets the serialization services.
	 * 
	 *  @param platform The platform ID.
	 *  @return The serialization services.
	 */
	protected static final ISerializationServices getSerializationServices(IComponentIdentifier platform)
	{
		return (ISerializationServices) PlatformConfiguration.getPlatformValue(platform, IStarterConfiguration.DATA_SERIALIZATIONSERVICES);
	}
}
