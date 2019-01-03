package jadex.platform.service.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.JadexVersion;
import jadex.bridge.VersionInfo;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.component.impl.remotecommands.ProxyReference;
import jadex.bridge.component.streams.InputConnection;
import jadex.bridge.component.streams.OutputConnection;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.bridge.service.types.remote.ServiceInputConnectionProxy;
import jadex.bridge.service.types.remote.ServiceOutputConnectionProxy;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IRwMap;
import jadex.commons.collection.LRU;
import jadex.commons.collection.RwMapWrapper;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.IUserContextContainer;
import jadex.commons.transformation.traverser.TransformProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.platform.service.serialization.codecs.GZIPCodec;
import jadex.platform.service.serialization.codecs.LZ4Codec;
import jadex.platform.service.serialization.codecs.SnappyCodec;
import jadex.platform.service.serialization.codecs.XZCodec;
import jadex.platform.service.serialization.serializers.JadexBinarySerializer;
import jadex.platform.service.serialization.serializers.JadexFramedBinarySerializer;
import jadex.platform.service.serialization.serializers.JadexJsonSerializer;

/**
 *  Functionality for managing serialization.
 */
public class SerializationServices implements ISerializationServices
{
	//-------- attributes --------
		
	/** The remote reference module */
	protected RemoteReferenceModule rrm;
	
	/** Default serializer used for sending. */
	protected ISerializer defaultsendserializer;
	
	/** Optimized serializer used for sending to platforms with the same version. */
	protected ISerializer optimizedsendserializer;
	
	/** All available serializers */
	protected ISerializer[] serializers;
//	protected Map<Integer, ISerializer> serializers;
	
	/** Codecs used for sending. */
	protected ICodec[] sendcodecs;
	
	/** All available codecs. */
	protected ICodec[] codecs;
	
	/** Preprocessors for encoding. */
	protected ITraverseProcessor[] preprocessors;
	
	/** Postprocessors for decoding. */
	protected ITraverseProcessor[] postprocessors;
	
	/** The reference class cache (clazz->boolean (is reference)). */
	protected Map<Class<?>, boolean[]> references;
	
	/** The security service which injects itself once available. */
	protected ISecurityService secserv;
	
	/** Cache for identifying platforms with the same version. */
	protected IRwMap<IComponentIdentifier, Boolean> sameversioncache;

	/** Creates the management. */
	public SerializationServices(IComponentIdentifier comp)
	{
		sameversioncache = new RwMapWrapper<>(new LRU<>(100));
		rrm	= new RemoteReferenceModule(comp);
//		serializers = new HashMap<Integer, ISerializer>();
		serializers = new ISerializer[3];
		ISerializer serial = new JadexBinarySerializer();
//		serializers.put(serial.getSerializerId(), serial);
		serializers[serial.getSerializerId()] = serial;
		serial = new JadexJsonSerializer();
//		serializers.put(serial.getSerializerId(), serial);
		serializers[serial.getSerializerId()] = serial;
		serial = new JadexFramedBinarySerializer();
//		serializers.put(serial.getSerializerId(), serial);
		serializers[serial.getSerializerId()] = serial;
		defaultsendserializer = serializers[2];
		optimizedsendserializer = serializers[0];
		codecs = new ICodec[4];
		ICodec codec = new SnappyCodec();
		codecs[codec.getCodecId()] = codec;
		codec = new GZIPCodec();
		codecs[codec.getCodecId()] = codec;
		codec = new LZ4Codec();
		codecs[codec.getCodecId()] = codec;
		codec = new XZCodec();
		codecs[codec.getCodecId()] = codec;
		sendcodecs = new ICodec[] { codecs[3] };
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
	public byte[] encode(IMsgHeader header, IInternalAccess component, Object obj)
	{
		IComponentIdentifier receiver = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
		ISerializer serial = getSendSerializer(receiver.getRoot());
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("header", header);
		ctx.put("component", component);
		byte[] enc = serial.encode(obj, component.getClassLoader(), getPreprocessors(), ctx);
		
		ICodec[] codecs = getSendCodecs(receiver);
		if (header == obj)
			codecs = null;
		
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
	 *  @param header The header object if available, null otherwise.
	 *  @return Object to be encoded.
	 *  
	 */
	public Object decode(IMsgHeader header, IInternalAccess component, byte[] enc)
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
						for(int i = codecsize - 1; i >= 0; --i)
						{
							raw = getCodecs()[SUtil.bytesToInt(enc, (i << 4) + 8)].decode(raw, offset, raw.length - offset);
							offset = 0;
						}
					}
					else
					{
						raw = new byte[enc.length - prefixsize];
						System.arraycopy(enc, prefixsize, raw, 0, raw.length);
					}
					
					int serialid = SUtil.bytesToInt(enc, 2);
					ISerializer serial = getSerializers()[serialid];
					Map<String, Object> context = new HashMap<String, Object>();
					context.put("header", header);
					context.put("component", component);
					ret = serial.decode(raw, component.getClassLoader(), getPostprocessors(), null, context);
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				ret = null;
			}
		}
		return ret;
	}
	
	/**
	 *  Returns the serializer for sending.
	 *  
	 *  @param receiver Receiving platform.
	 *  @return Serializer.
	 */
	public ISerializer getSendSerializer(IComponentIdentifier receiverplatform)
	{
//		return (ISerializer) PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SEND_SERIALIZER);
		Boolean sameversion = sameversioncache.get(receiverplatform);
		if (Boolean.TRUE.equals(sameversion))
			return optimizedsendserializer;
		
		if (sameversion == null && secserv != null)
		{
			JadexVersion remoteversion = secserv.getJadexVersion(receiverplatform.getRoot());
			if (remoteversion != null)
			{
				if (remoteversion.isUnknown())
				{
					sameversioncache.put(receiverplatform, Boolean.FALSE);
				}
				else
				{
					JadexVersion localversion = VersionInfo.getInstance().getJadexVersion();
					if (localversion.getMinorVersion() == remoteversion.getMinorVersion() &&
						localversion.getMajorVersion() == remoteversion.getMajorVersion())
					{
						sameversioncache.put(receiverplatform, Boolean.TRUE);
//						System.out.println("Switched to optimized serializer for " + receiverplatform);
						return optimizedsendserializer;
					}
					else
					{
						sameversioncache.put(receiverplatform, Boolean.FALSE);
					}
				}
			}
		}
		return defaultsendserializer;
	}
	
	/**
	 *  Returns all serializers.
	 *  
	 *  @param platform Sending platform.
	 *  @return Serializers.
	 */
	public ISerializer[] getSerializers()
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
	public ICodec[] getCodecs()
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
	 *  Create the preprocessors.
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
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					IInternalAccess ia = (IInternalAccess)ctx.get("component");
					ProvidedServiceInfo[] sers = ia.getModel().getProvidedServices();
					boolean tolerant = false; // only registry is tolerant until now
					for(ProvidedServiceInfo psi: sers)
					{
						if(psi.getType().getTypeName().indexOf(SReflect.getUnqualifiedClassName(IPeerRegistrySynchronizationService.class))!=-1
							|| psi.getType().getTypeName().indexOf(SReflect.getUnqualifiedClassName(ISuperpeerRegistrySynchronizationService.class))!=-1)
						{
							tolerant = true;
							break;
						}
					}
					Object ret= ((RemoteReferenceModule)rrm).getProxy((ProxyReference)object, targetcl, tolerant);
					return ret;
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					throw SUtil.throwUnchecked(e);
				}
			}
		};
		procs.add(rmipostproc);
		
		procs.add(new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return ServiceInputConnectionProxy.class.equals(type);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					ServiceInputConnectionProxy icp = (ServiceInputConnectionProxy)object;
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					IInputConnection icon = ((IInternalMessageFeature)((IInternalAccess)ctx.get("component")).getFeature(IMessageFeature.class)).getParticipantInputConnection(icp.getConnectionId(), 
						icp.getInitiator(), icp.getParticipant(), icp.getNonFunctionalProperties());
					return icon;
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
			}
		});
		
		procs.add(new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return ServiceOutputConnectionProxy.class.equals(type);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					ServiceOutputConnectionProxy ocp = (ServiceOutputConnectionProxy)object;
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					IOutputConnection ocon = ((IInternalMessageFeature)((IInternalAccess)ctx.get("component")).getFeature(IMessageFeature.class)).getParticipantOutputConnection(ocp.getConnectionId(), 
						ocp.getInitiator(), ocp.getParticipant(), ocp.getNonFunctionalProperties());
					return ocon;
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
			}
		});
		
		return procs;
	}
	
	/**
	 * 
	 */
	public List<ITraverseProcessor> createPreprocessors()
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		
		// Preprocessor to copy the networknames cache object (used by security service and all service ids)
		procs.add(new TransformProcessor());
		
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
				return object!=null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class);
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
//				if(object!=null && object.toString().indexOf("Security")!=-1 && !(object instanceof String))
//					System.out.println("hererer");
				
				return rrm.isRemoteReference(object);
			}
			
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
//					System.out.println("remoteob: "+object);
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					MsgHeader header = (MsgHeader)ctx.get("header");
					IComponentIdentifier receiver = (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
					Object ret = rrm.getProxyReference(object, receiver, targetcl);
					return ret;
//					return rrm.getProxyReference(object, receiver, ((IEncodingContext)context).getClassLoader());
				}
				catch(Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
		};
		procs.add(bpreproc);
		
		// output connection as result of call
		procs.add(new ITraverseProcessor()
		{
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					MsgHeader header = (MsgHeader)ctx.get("header");
//					AbstractRemoteCommand com = getRCFromContext(context);
					ServiceInputConnectionProxy con = (ServiceInputConnectionProxy)object;
					OutputConnection ocon = ((IInternalMessageFeature)((IInternalAccess)ctx.get("component")).getFeature(IMessageFeature.class)).internalCreateOutputConnection(
						(IComponentIdentifier)header.getProperty(IMsgHeader.SENDER), (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER), null); // todo: nonfunc
					con.setOutputConnection(ocon);
					con.setConnectionId(ocon.getConnectionId());
					return con;
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
			}
			
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return object instanceof ServiceInputConnectionProxy;
			}
		});
		
		// input connection proxy as result of call
		procs.add(new ITraverseProcessor()
		{
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				try
				{
//					AbstractRemoteCommand com = (AbstractRemoteCommand)((IRootObjectContext)context).getRootObject();
					Map<String, Object> ctx = (Map<String, Object>)((IUserContextContainer)context).getUserContext();
					MsgHeader header = (MsgHeader)ctx.get("header");
					ServiceOutputConnectionProxy con = (ServiceOutputConnectionProxy)object;
					InputConnection icon = ((IInternalMessageFeature)((IInternalAccess)ctx.get("component")).getFeature(IMessageFeature.class)).internalCreateInputConnection(
						(IComponentIdentifier)header.getProperty(IMsgHeader.SENDER), (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER), null);//com.getNonFunctionalProperties());
					con.setConnectionId(icon.getConnectionId());
					con.setInputConnection(icon);
					return con;
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
			}
			
			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return object instanceof ServiceOutputConnectionProxy;
			}
		});
		
		return procs;
	}
	
	protected static final int getPrefixSize(int codeccount)
	{
		// prefixsize[2] | serializerid[4] | codeccount[2] | codecid[4]...
		return 8 + (codeccount << 4);
	}
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 *  - has been explicitly set to be reference
	 */
	public boolean isLocalReference(Object object)
	{
		return rrm.isLocalReference(object);
	}
	
	/**
	 *  Test if an object is a remote object.
	 */
	public boolean isRemoteObject(Object target)
	{
		return rrm.isRemoteObject(target);
	}
	
	/**
	 *  Get the clone processors.
	 *  @return The clone processors.
	 */
	public List<ITraverseProcessor> getCloneProcessors()
	{
		return rrm.getCloneProcessors();
	}
	
	/**
	 *  Injects the security service.
	 *  
	 *  @param secserv The security service.
	 */
	public void setSecurityService(ISecurityService secserv)
	{
		this.secserv = secserv;
	}
	
	/**
	 *  Gets the serialization services.
	 * 
	 *  @param platform The platform ID.
	 *  @return The serialization services.
	 */
	public static final ISerializationServices getSerializationServices(IComponentIdentifier platform)
	{
		return (ISerializationServices)Starter.getPlatformValue(platform, Starter.DATA_SERIALIZATIONSERVICES);
	}
}
