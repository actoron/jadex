package jadex.platform.service.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.PasswordSecret;
import jadex.platform.service.security.handshake.BasicSecurityMessage;
import jadex.platform.service.security.handshake.InitialHandshakeFinalMessage;
import jadex.platform.service.security.handshake.InitialHandshakeMessage;
import jadex.platform.service.security.handshake.InitialHandshakeReplyMessage;

/**
 *  Agent that provides the security service.
 */
@Agent
@Arguments({
	@Argument(name="cryptosuites", clazz=String[].class),
	@Argument(name="usepass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="printpass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="trustedlan", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="networkname", clazz=String.class),
	@Argument(name="networkpass", clazz=String.class),
	@Argument(name="virtualnames", clazz=String[].class),
	@Argument(name="validityduration", clazz=long.class)
})
@Service
//@ProvidedServices(@ProvidedService(type=ISecurityService.class))
@ProvidedServices(@ProvidedService(type=ISecurityService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)))
@Properties(value=@NameValue(name="system", value="true"))
public class SecurityAgent implements ISecurityService, IInternalService
{
	/** Properties id for the settings service. */
	public static final String	PROPERTIES_ID	= "securityservice";
	
	/** Header property for security messages. */
	protected static final String SECURITY_MESSAGE = "__securitymessage__";
	
	/** Timeout used for internal expirations */
	protected static final long TIMEOUT = 30000;
	
	/** Component access. */
	@Agent
	protected IInternalAccess agent;
	
	/** Local platform authentication secret. */
	protected AbstractAuthenticationSecret platformsecret;
	
	/** Remote platform authentication secrets. */
	protected Map<IComponentIdentifier, AbstractAuthenticationSecret> remoteplatformsecrets;
	
	/** Available virtual networks. */
	protected Map<String, AbstractAuthenticationSecret> networks;
	
	/** Available crypt suites. */
	protected Map<String, Class<?>> allowedcryptosuites;
	
	/** CryptoSuites currently initializing, value=Handshake state. */
	protected Map<String, HandshakeState> initializingcryptosuites;
	
	/** CryptoSuites currently in use. */
	protected Map<String, ICryptoSuite> currentcryptosuites;
	
	/** CryptoSuites that are expiring with expiration time. */
	protected Map<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites;
	
	/**
	 *  Initialization.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		if (!agent.getComponentIdentifier().getLocalName().equals("security"))
			agent.getLogger().warning("Security agent running as \"" + agent.getComponentIdentifier().getLocalName() +"\" instead of \"security\".");
		
		IArgumentsResultsFeature argfeat = agent.getComponentFeature(IArgumentsResultsFeature.class);
		
		jadex.commons.Properties props = getSettingsService().getProperties(PROPERTIES_ID).get();
//		props.addProperty(new Property("password", "aaaaaaaa-123"));
		
		boolean changedprops = false;
		
		if (props == null)
		{
			props = new jadex.commons.Properties();
			props.addProperty(new Property("usepass", "true"));
			props.addProperty(new Property("printpass", "true"));
			changedprops = true;
		}
		
		String secretstr = props.getStringProperty("password");
		boolean printpass = props.getBooleanProperty("printpass");
		boolean usepass = props.getBooleanProperty("usepass");
		
//		if (secretstr != null && secretstr.matches("[a-z0-9]{8,8}-[a-z0-9]{3,3}"))
//		{
//			// Die, old passwords, die, die, die!
//			secretstr = null;
//		}
		
		if (usepass && secretstr == null)
		{
			secretstr = SUtil.createRandomKey();
			props.addProperty(new Property("password", secretstr));
			changedprops = true;
			System.out.println("Generated new platform access password: "+secretstr);
			
		}
		
		if (changedprops)
			getSettingsService().setProperties(PROPERTIES_ID, props);
		
		try
		{
			platformsecret = AbstractAuthenticationSecret.fromString(secretstr);
		}
		catch (IllegalArgumentException e)
		{
			secretstr = PasswordSecret.PREFIX + ":" + secretstr;
			platformsecret = AbstractAuthenticationSecret.fromString(secretstr);
		}
		
		if (printpass && platformsecret != null)
		{
			secretstr = platformsecret.toString();
			System.out.println("Platform access secret: "+secretstr);
		}
		
		remoteplatformsecrets = new HashMap<IComponentIdentifier, AbstractAuthenticationSecret>();
		networks = new HashMap<String, AbstractAuthenticationSecret>();
		try
		{
			String nwname = (String) argfeat.getArguments().get("networkname");
			String nwpass = (String) argfeat.getArguments().get("networkpass");
			if (nwname != null)
				networks.put(nwname, AbstractAuthenticationSecret.fromString(nwpass));
		}
		catch (Exception e)
		{
		}
		
		initializingcryptosuites = new HashMap<String, HandshakeState>();
		currentcryptosuites = new HashMap<String, ICryptoSuite>();
		expiringcryptosuites = new HashMap<String, Tuple2<ICryptoSuite,Long>>();
		
		String[] cryptsuites = (String[]) argfeat.getArguments().get("cryptosuites");
		if (cryptsuites == null)
		{
			cryptsuites = new String[] { "jadex.platform.service.security.impl.Curve448ChaCha20Poly1305Suite" };
		}
		allowedcryptosuites = new HashMap<String, Class<?>>();
		for (String cryptsuite : cryptsuites)
		{
			try
			{
				Class<?> clazz = Class.forName(cryptsuite, true, agent.getClassLoader());
				allowedcryptosuites.put(cryptsuite, clazz);
			}
			catch (Exception e)
			{
				return new Future<Void>(e);
			}
		}
		agent.getComponentFeature0(IMessageFeature.class).setAllowUntrusted(true);
		agent.getComponentFeature0(IMessageFeature.class).addMessageHandler(new SecurityMessageHandler());
		return IFuture.DONE;
	}
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public IFuture<byte[]> encryptAndSign(final IMsgHeader header, final byte[] content)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<byte[]>()
		{
			public IFuture<byte[]> execute(IInternalAccess ia)
			{
				doCleanup();
				
				final Future<byte[]> ret = new Future<byte[]>();
				
				if (isSecurityMessage(header))
				{
					byte[] newcontent = new byte[content.length + 1];
					newcontent[0] = -1;
					System.arraycopy(content, 0, newcontent, 1, content.length);
					ret.setResult(newcontent);
				}
				else
				{
					String rplat = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot().toString();
					ICryptoSuite cs = currentcryptosuites.get(rplat);
					if (cs != null)
					{
						try
						{
							ret.setResult(cs.encryptAndSign(content));
						}
						catch (Exception e)
						{
							ret.setException(e);
						}
					}
					else
					{
						HandshakeState hstate = initializingcryptosuites.get(rplat);
						if (hstate == null)
						{
							String convid = SUtil.createUniqueId(agent.getComponentIdentifier().getRoot().toString());
							hstate = new HandshakeState();
							hstate.setExpirationTime(System.currentTimeMillis() + TIMEOUT);
							hstate.setConversationId(convid);
							hstate.setResultFuture(new Future<ICryptoSuite>());
							
							initializingcryptosuites.put(rplat, hstate);
							
							String[] csuites = allowedcryptosuites.keySet().toArray(new String[allowedcryptosuites.size()]);
							InitialHandshakeMessage ihm = new InitialHandshakeMessage(agent.getComponentIdentifier(), convid, csuites);
							BasicComponentIdentifier rsec = new BasicComponentIdentifier("security@" + rplat);
							sendSecurityHandshakeMessage(rsec, ihm);
						}
						
						hstate.getResultFuture().addResultListener(new ExceptionDelegationResultListener<ICryptoSuite, byte[]>(ret)
						{
							public void customResultAvailable(ICryptoSuite result) throws Exception
							{
								try
								{
									ret.setResult(result.encryptAndSign(content));
								}
								catch (Exception e)
								{
									ret.setException(e);
								}
							}
						});
					}
				}
				
				return ret;
			}
		});
	}
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public IFuture<Tuple2<IMsgSecurityInfos,byte[]>> decryptAndAuth(final IComponentIdentifier sender, final byte[] content)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Tuple2<IMsgSecurityInfos,byte[]>>()
		{
			public IFuture<Tuple2<IMsgSecurityInfos, byte[]>> execute(IInternalAccess ia)
			{
				doCleanup();
				
				final Future<Tuple2<IMsgSecurityInfos, byte[]>> ret = new Future<Tuple2<IMsgSecurityInfos,byte[]>>();
				
				if (content.length > 0 && content[0] == -1)
				{
					// Security message
					byte[] newcontent = new byte[content.length - 1];
					System.arraycopy(content, 1, newcontent, 0, newcontent.length);
					MsgSecurityInfos secinfos = new MsgSecurityInfos();
					Tuple2<IMsgSecurityInfos,byte[]> tup = new Tuple2<IMsgSecurityInfos, byte[]>(secinfos, newcontent);
					ret.setResult(tup);
				}
				else
				{
					final String splat = sender.getRoot().toString();
					ICryptoSuite cs = currentcryptosuites.get(splat);
					byte[] cleartext = null;
					
					if (cs != null)
					{
						cleartext = cs.decryptAndAuth(content);
					}
					
					if (cleartext == null)
					{
						Tuple2<ICryptoSuite, Long> tup = expiringcryptosuites.get(splat);
						if (tup != null)
						{
							cs = tup.getFirstEntity();
							cleartext = cs.decryptAndAuth(content);
						}
					}
					
					if (cleartext == null)
					{
						HandshakeState hstate = initializingcryptosuites.get(splat);
						if (hstate != null)
						{
							final byte[] fcontent = content;
							hstate.getResultFuture().addResultListener(new IResultListener<ICryptoSuite>()
							{
								public void resultAvailable(ICryptoSuite result)
								{
									byte[] cleartext = result.decryptAndAuth(fcontent);
									if (cleartext != null)
									{
										ret.setResult(new Tuple2<IMsgSecurityInfos, byte[]>(result.getSecurityInfos(), cleartext));
									}
									else
									{
										ret.setException(new SecurityException("Could not establish secure communication with: " + splat.toString()));
									}
								}
								
								public void exceptionOccurred(Exception exception)
								{
									ret.setException(exception);
								}
							});
						}
						else
						{
							ret.setException(new SecurityException("Could not establish secure communication with: " + splat.toString()));
						}
					}
					
					if (cleartext != null)
					{
						ret.setResult(new Tuple2<IMsgSecurityInfos, byte[]>(cs.getSecurityInfos(), cleartext));
					}
				}
				return ret;
			}
		});
	}
	
	//-------- Information access -------
	
	public IComponentIdentifier getComponentIdentifier()
	{
		return agent.getComponentIdentifier();
	}
	
	/**
	 *  Get access to the stored virtual network configurations.
	 * 
	 *  @return The stored virtual network configurations.
	 */
	public Map<String, AbstractAuthenticationSecret> getNetworks()
	{
		return networks;
	}
	
	/**
	 *  Gets the local platform secret.
	 */
	public AbstractAuthenticationSecret getPlatformSecret()
	{
		return platformsecret;
	}
	
	/**
	 *  Gets the secret of a remote platform if available.
	 * 
	 *  @param remoteid ID of the remote platform.
	 *  @return Secret or null.
	 */
	public AbstractAuthenticationSecret getRemotePlatformSecret(IComponentIdentifier remoteid)
	{
		return remoteplatformsecrets.get(remoteid.getRoot());
	}
	
	// -------- Cleanup
	
	/**
	 *  Cleans expired objects.
	 */
	protected void doCleanup()
	{
		long time = System.currentTimeMillis();
		
		for (Iterator<Map.Entry<String, HandshakeState>> it = initializingcryptosuites.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, HandshakeState> entry = it.next();
			if (time > entry.getValue().getExpirationTime())
			{
				entry.getValue().getResultFuture().setException(new TimeoutException("Handshake timed out with platform: " + entry.getKey()));
				it.remove();
			}
		}
		
		for (Iterator<Map.Entry<String, Tuple2<ICryptoSuite, Long>>> it = expiringcryptosuites.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Tuple2<ICryptoSuite, Long>> entry = it.next();
			if (time > entry.getValue().getSecondEntity())
				it.remove();
		}
	}
	
	//-------- Utility functions -------
	
	/**
	 *  Creates a crypto suite of a particular name.
	 * 
	 *  @param name Name of the suite.
	 *  @return The suite, null if not found.
	 */
	protected ICryptoSuite createCryptoSuite(String name)
	{
		ICryptoSuite ret = null;
		try
		{
			Class<?> clazz = allowedcryptosuites.get(name);
			if (clazz != null)
			{
				ret = (ICryptoSuite) clazz.newInstance();
			}
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Sends a security handshake message.
	 * 
	 *  @param receiver Receiver of the message.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	public void sendSecurityHandshakeMessage(final IComponentIdentifier receiver, Object message)
	{
		sendSecurityMessage(receiver, message).addResultListener(new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				HandshakeState state = initializingcryptosuites.remove(receiver.getRoot());
				if (state != null)
				{
					state.getResultFuture().setException(new SecurityException("Could not reach " + receiver + " for handshake."));
				}
			}
			
			public void resultAvailable(Void result)
			{	
			}
		});
	}
	
	/**
	 *  Get the settings service.
	 */
	public ISettingsService getSettingsService()
	{
		ISettingsService ret = null;
		try
		{
			ret = SServiceProvider.getLocalService(agent, ISettingsService.class, Binding.SCOPE_PLATFORM);
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Sends a security message.
	 * 
	 *  @param receiver Receiver of the message.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	protected IFuture<Void> sendSecurityMessage(IComponentIdentifier receiver, Object message)
	{
		Map<String, Object> addheader = new HashMap<String, Object>();
		addheader.put(SECURITY_MESSAGE, Boolean.TRUE);
		
		return agent.getComponentFeature(IMessageFeature.class).sendMessage(receiver, message, addheader);
	}
	
	/**
	 *  Checks if a message is a security message.
	 *  
	 *  @param header The message header.
	 *  @return True, if security message.
	 */
	protected static final boolean isSecurityMessage(IMsgHeader header)
	{
		return Boolean.TRUE.equals(header.getProperty(SECURITY_MESSAGE));
	}
	
	//-------- Message Handler -------
	
	/**
	 *  Security service message handler.
	 *
	 */
	protected class SecurityMessageHandler implements IMessageHandler
	{
		/**
		 *  Test if handler should handle a message.
		 *  @return True if it should handle the message. 
		 */
		public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			return isSecurityMessage(header);
		}
		
		/**
		 *  Test if handler should be removed.
		 *  @return True if it should be removed. 
		 */
		public boolean isRemove()
		{
			return false;
		}
		
		/**
		 *  Handle the message.
		 *  @param header The header.
		 *  @param msg The message.
		 */
		public void handleMessage(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
		{
			if (msg instanceof InitialHandshakeMessage)
			{
				final InitialHandshakeMessage imsg = (InitialHandshakeMessage) msg;
				System.out.println("Got initial handshake: " + imsg.getConversationId());
				
				final Future<ICryptoSuite> fut = new Future<ICryptoSuite>();
				
				HandshakeState state = initializingcryptosuites.remove(imsg.getSender().getRoot().toString());
				
				// Check if handshake is already happening. 
				if (state != null)
				{
					if (getComponentIdentifier().getRoot().toString().compareTo(imsg.getSender().getRoot().toString()) < 0)
						fut.addResultListener(new DelegationResultListener<ICryptoSuite>(state.getResultFuture()));
					else
						return;
				}
				
				if (imsg.getCryptoSuites() == null || imsg.getCryptoSuites().length < 1)
					return;
				
				Set<String> offeredsuites = new HashSet<String>(Arrays.asList(imsg.getCryptoSuites()));
				
				String chosensuite = null;
				for (String suite : allowedcryptosuites.keySet())
				{
					if (offeredsuites.contains(suite))
					{
						chosensuite = suite;
						break;
					}
				}
				
				if (chosensuite == null)
					return;
				
				state = new HandshakeState();
				state.setResultFuture(fut);
				state.setConversationId(imsg.getConversationId());
				state.setExpirationTime(System.currentTimeMillis() + TIMEOUT);
				initializingcryptosuites.put(imsg.getSender().getRoot().toString(), state);
				
				
				InitialHandshakeReplyMessage reply = new InitialHandshakeReplyMessage(getComponentIdentifier(), state.getConversationId(), chosensuite);
				
				sendSecurityHandshakeMessage(imsg.getSender(), reply);
			}
			else if (msg instanceof InitialHandshakeReplyMessage)
			{
				InitialHandshakeReplyMessage rm = (InitialHandshakeReplyMessage) msg;
				HandshakeState state = initializingcryptosuites.get(rm.getSender().getRoot().toString());
				
				if (state != null)
				{
					String convid = state.getConversationId();
					if (convid != null && convid.equals(rm.getConversationId()))
					{
						ICryptoSuite suite = createCryptoSuite(rm.getChosenCryptoSuite());
						
						if (suite == null)
						{
							initializingcryptosuites.remove(rm.getSender().getRoot().toString());
							state.getResultFuture().setException(new SecurityException("Handshake with remote platform " + rm.getSender().getRoot().toString() + " failed."));
						}
						else
						{
							state.setCryptoSuite(suite);
							InitialHandshakeFinalMessage fm = new InitialHandshakeFinalMessage(agent.getComponentIdentifier(), rm.getConversationId(), rm.getChosenCryptoSuite());
							sendSecurityHandshakeMessage(rm.getSender(), fm);
						}
					}
				}
				
			}
			else if (msg instanceof InitialHandshakeFinalMessage)
			{
				InitialHandshakeFinalMessage fm = (InitialHandshakeFinalMessage) msg;
				HandshakeState state = initializingcryptosuites.get(fm.getSender().getRoot().toString());
				if (state != null)
				{
					String convid = state.getConversationId();
					if (convid != null && convid.equals(fm.getConversationId()))
					{
						ICryptoSuite suite = createCryptoSuite(fm.getChosenCryptoSuite());
						
						if (suite == null)
						{
							initializingcryptosuites.remove(fm.getSender().getRoot().toString());
							state.getResultFuture().setException(new SecurityException("Handshake with remote platform " + fm.getSender().getRoot().toString() + " failed."));
						}
						else
						{
							state.setCryptoSuite(suite);
							if (!suite.handleHandshake(SecurityAgent.this, fm))
							{
								System.out.println("Finished handshake: " + fm.getSender());
								currentcryptosuites.put(fm.getSender().getRoot().toString(), state.getCryptoSuite());
								initializingcryptosuites.remove(fm.getSender().getRoot().toString());
								state.getResultFuture().setResult(state.getCryptoSuite());
								
							}
						}
					}
				}
			}
			else if (msg instanceof BasicSecurityMessage)
			{
				BasicSecurityMessage secmsg = (BasicSecurityMessage) msg;
				HandshakeState state = initializingcryptosuites.get(secmsg.getSender().getRoot().toString());
				if (state != null && state.getConversationId().equals(secmsg.getConversationId()) && state.getCryptoSuite() != null)
				{
					try
					{
						if (!state.getCryptoSuite().handleHandshake(SecurityAgent.this, secmsg))
						{
							System.out.println("Finished handshake: " + secmsg.getSender());
							currentcryptosuites.put(secmsg.getSender().getRoot().toString(), state.getCryptoSuite());
							initializingcryptosuites.remove(secmsg.getSender().getRoot().toString());
							state.getResultFuture().setResult(state.getCryptoSuite());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						state.getResultFuture().setException(e);
						initializingcryptosuites.remove(secmsg.getSender().getRoot());
					}
				}
			}
		}
	}
	
	//---- IInternalService bullshit
	
	private IServiceIdentifier sid;
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public IFuture<Boolean> isValid()
	{
		return new Future<Boolean>(true);
	}
		
	/**
	 *  Get the map of properties (considered as constant).
	 *  @return The service property map (if any).
	 */
	public Map<String, Object> getPropertyMap()
	{
		return new HashMap<String, Object>();
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	startService() {return IFuture.DONE;}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	shutdownService() {return IFuture.DONE;}
	
	/**
	 *  Sets the access for the component.
	 *  @param access Component access.
	 */
	public IFuture<Void> setComponentAccess(@Reference IInternalAccess access) {return IFuture.DONE;}
	
	/**
	 *  Set the service identifier.
	 */
	public void createServiceIdentifier(String name, Class<?> implclazz, IResourceIdentifier rid, Class<?> type, String scope)
	{
		this.sid = BasicService.createServiceIdentifier(agent.getComponentIdentifier(), name, type, implclazz, rid, scope);
	}
}
