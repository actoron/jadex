package jadex.platform.service.security;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.bouncycastle.crypto.digests.Blake2bDigest;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
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
import jadex.commons.security.SSecurity;
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
import jadex.platform.service.security.auth.AbstractX509PemSecret;
import jadex.platform.service.security.auth.KeySecret;
import jadex.platform.service.security.auth.PasswordSecret;
import jadex.platform.service.security.handshake.BasicSecurityMessage;
import jadex.platform.service.security.handshake.InitialHandshakeFinalMessage;
import jadex.platform.service.security.handshake.InitialHandshakeMessage;
import jadex.platform.service.security.handshake.InitialHandshakeReplyMessage;
import jadex.platform.service.security.impl.NHCurve448ChaCha20Poly1305Suite;

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
	protected static final long TIMEOUT = 60000;
	
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
	
	/** Task for cleanup duties. */
	protected volatile IFuture<Void> cleanuptask;
	
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
		String secretstr = props.getStringProperty("platformsecret");
		if (secretstr == null)
		{
			secretstr = props.getStringProperty("password");
			if (secretstr != null)
			{
				props.removeSubproperties("password");
				props.addProperty(new Property("platformsecret", secretstr));
				changedprops = true;
			}
		}
		boolean printpass = props.getBooleanProperty("printpass");
		boolean usepass = props.getBooleanProperty("usepass");
		
		if (usepass && secretstr == null)
		{
			secretstr = SUtil.createRandomKey();
			props.addProperty(new Property("platformsecret", secretstr));
			changedprops = true;
			System.out.println("Generated new platform access key: "+secretstr.substring(4));
			
		}
		secretstr = "pw:AAAACHBsYXRmb3JtAAAAQKH1B5mo3k/x5Ms4I1l3D7A6gS174vZnpol/TSEpWk0LZp1Ixsaiz80qYRFOftoK1J9wUwexE/Osbphf6DN1HW8:fmkds09djf0s9d0sf9kd";
		
		if (changedprops)
		{
			getSettingsService().setProperties(PROPERTIES_ID, props);
			getSettingsService().saveProperties().get();
		}
		
		try
		{
			platformsecret = AbstractAuthenticationSecret.fromString("platform", secretstr);
		}
		catch (IllegalArgumentException e)
		{
			secretstr = PasswordSecret.PREFIX + ":" + secretstr;
			platformsecret = AbstractAuthenticationSecret.fromString("platform", secretstr);
		}
		System.out.println(platformsecret.getEncoded());
		
		if (printpass && platformsecret != null)
		{
			secretstr = platformsecret.toString();
			
			if (platformsecret instanceof PasswordSecret)
				System.out.println("Platform access password: "+secretstr);
			else if (platformsecret instanceof KeySecret)
				System.out.println("Platform access key: "+secretstr);
			else if (platformsecret instanceof AbstractX509PemSecret)
				System.out.println("Platform access certificates: "+secretstr);
			else
				System.out.println("Platform access secret: "+secretstr);
		}
		
		remoteplatformsecrets = new HashMap<IComponentIdentifier, AbstractAuthenticationSecret>();
		networks = new HashMap<String, AbstractAuthenticationSecret>();
		try
		{
			String nwname = (String) argfeat.getArguments().get("networkname");
			String nwpass = (String) argfeat.getArguments().get("networkpass");
			if (nwname != null)
				networks.put(nwname, AbstractAuthenticationSecret.fromString(nwname, nwpass));
		}
		catch (Exception e)
		{
		}
		initializingcryptosuites = new HashMap<String, HandshakeState>();
		currentcryptosuites = Collections.synchronizedMap(new HashMap<String, ICryptoSuite>());
		expiringcryptosuites = new HashMap<String, Tuple2<ICryptoSuite,Long>>();
		
		String[] cryptsuites = (String[]) argfeat.getArguments().get("cryptosuites");
		if (cryptsuites == null)
		{
			cryptsuites = new String[] { NHCurve448ChaCha20Poly1305Suite.class.getCanonicalName() };
//			cryptsuites = new String[] { NHCurve448ChaCha20Poly1305Suite.class.getCanonicalName(),
//										 Curve448ChaCha20Poly1305Suite.class.getCanonicalName(),
//										 NHChaCha20Poly1305Suite.class.getCanonicalName() };
		}
		allowedcryptosuites = new LinkedHashMap<String, Class<?>>();
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
		agent.getComponentFeature(IMessageFeature.class).setAllowUntrusted(true);
		agent.getComponentFeature(IMessageFeature.class).addMessageHandler(new SecurityMessageHandler());
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
		checkCleanup();
		
		String rplat = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot().toString();
		final ICryptoSuite cs = currentcryptosuites.get(rplat);
		if (cs != null && !isSecurityMessage(header) && !cs.isExpiring())
			return new Future<byte[]>(cs.encryptAndSign(content));
		
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
					
					if (cs != null && cs.isExpiring())
					{
						expiringcryptosuites.put(rplat, new Tuple2<ICryptoSuite, Long>(cs, System.currentTimeMillis() + TIMEOUT));
						currentcryptosuites.remove(rplat);
						cs = null;
					}
					
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
							initializeHandshake(rplat);
							hstate = initializingcryptosuites.get(rplat);
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
		checkCleanup();
		
		String splat = sender.getRoot().toString();
		ICryptoSuite cs = currentcryptosuites.get(splat);
		if (cs != null && content.length > 0 && content[0] != -1)
		{
			byte[] cleartext = cs.decryptAndAuth(content);
			if (cleartext != null)
				return new Future<Tuple2<IMsgSecurityInfos,byte[]>>(new Tuple2<IMsgSecurityInfos, byte[]>(cs.getSecurityInfos(), cleartext));
		}
		
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
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<String> getEncodedPlatformSecret(IComponentIdentifier cid)
	{
		AbstractAuthenticationSecret secret = getPlatformSecret(cid);
		return new Future<String>(secret != null ? secret.toString() : null);
	}
	
	/**
	 *  Sets the secret of a platform.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<Void> setEncodedPlatformSecret(final IComponentIdentifier cid, final String secret)
	{
		return agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// TODO: Refresh?
				if (cid == null)
				{
					remoteplatformsecrets.remove(cid);
				}
				else
				{
					AbstractAuthenticationSecret authsec = AbstractAuthenticationSecret.fromString(cid.toString(), secret);
					
					if (agent.getComponentIdentifier().getRoot().equals(cid))
						platformsecret = authsec;
					else
						remoteplatformsecrets.put(cid, authsec);
				}
				return IFuture.DONE;
			}
		});
		
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
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Secret or null.
	 */
	public AbstractAuthenticationSecret getPlatformSecret(IComponentIdentifier cid)
	{
		cid = cid.getRoot();
		if (cid.equals(agent.getComponentIdentifier().getRoot()))
			return getPlatformSecret();
		return remoteplatformsecrets.get(cid.getRoot());
	}
	
	// -------- Cleanup
	
	protected void checkCleanup()
	{
		if (cleanuptask == null)
		{
			synchronized (this)
			{
				if (cleanuptask == null)
				{
					cleanuptask = agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							return ia.getComponentFeature(IExecutionFeature.class).waitForDelay(TIMEOUT << 1, new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									doCleanup();
									cleanuptask = null;
									return IFuture.DONE;
								}
							});
						}
					});
				}
			}
		}
	}
	
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
	
	protected void initializeHandshake(String cid)
	{
		String convid = SUtil.createUniqueId(agent.getComponentIdentifier().getRoot().toString());
		HandshakeState hstate = new HandshakeState();
		hstate.setExpirationTime(System.currentTimeMillis() + TIMEOUT);
		hstate.setConversationId(convid);
		hstate.setResultFuture(new Future<ICryptoSuite>());
		
		initializingcryptosuites.put(cid.toString(), hstate);
		
		String[] csuites = allowedcryptosuites.keySet().toArray(new String[allowedcryptosuites.size()]);
		InitialHandshakeMessage ihm = new InitialHandshakeMessage(agent.getComponentIdentifier(), convid, csuites);
		BasicComponentIdentifier rsec = new BasicComponentIdentifier("security@" + cid);
		sendSecurityHandshakeMessage(rsec, ihm);
	}
	
	/**
	 *  Get the settings service.
	 */
	protected ISettingsService getSettingsService()
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
				IComponentIdentifier rplat = imsg.getSender().getRoot();
				
				final Future<ICryptoSuite> fut = new Future<ICryptoSuite>();
				
				HandshakeState state = initializingcryptosuites.remove(rplat.toString());
				
				// Check if handshake is already happening. 
				if (state != null)
				{
					if (getComponentIdentifier().getRoot().toString().compareTo(rplat.toString()) < 0)
						fut.addResultListener(new DelegationResultListener<ICryptoSuite>(state.getResultFuture()));
					else
						return;
				}
				
				if (imsg.getCryptoSuites() == null || imsg.getCryptoSuites().length < 1)
					return;
				
				String[] offeredsuites = imsg.getCryptoSuites();
				
				String chosensuite = null;
				if (offeredsuites != null)
				{
					for (String suite : offeredsuites)
					{
						if (allowedcryptosuites.containsKey(suite))
						{
							chosensuite = suite;
							break;
						}
					}
				}
				
				if (chosensuite == null)
					return;
				
				state = new HandshakeState();
				state.setResultFuture(fut);
				state.setConversationId(imsg.getConversationId());
				state.setExpirationTime(System.currentTimeMillis() + TIMEOUT);
				initializingcryptosuites.put(rplat.toString(), state);
				
				ICryptoSuite oldcs = currentcryptosuites.remove(rplat.toString());
				if (oldcs != null)
				{
					expiringcryptosuites.put(rplat.toString(), new Tuple2<ICryptoSuite, Long>(oldcs, System.currentTimeMillis() + TIMEOUT));
				}
				
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
						System.out.println("Suite: " + (suite != null?suite.getClass().toString():"null"));
						
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
	
	public static void main(String[] args)
	{
		String dn = "O=Someorg,C=US,CN=My CA";
		int str = 512;
		int days = 30;
//		String scheme = "RSAANDMGF1";
//		String scheme = "RSA";
		String schemeconf = "brainpool";
//		String schemeconf = null;
		String scheme = "ECDSA";
		String hash = "SHA256";
		
		Tuple2<String, String> tup = SSecurity.createRootCaCertificate(dn, scheme, schemeconf, hash, str, days);
		
		String dn2 = "O=Someorg,C=US,CN=My Intermediate CA";
		String dn3 = "O=Someorg,C=US,CN=My Intermediate CA2";
		String dn4 = "O=Someorg,C=US,CN=My Platform";
		
		Tuple2<String, String> tup2 = SSecurity.createIntermediateCaCertificate(tup.getFirstEntity(), tup.getSecondEntity(), dn2, 1, scheme, schemeconf, hash, str, days);
		Tuple2<String, String> tup3 = SSecurity.createIntermediateCaCertificate(tup2.getFirstEntity(), tup2.getSecondEntity(), dn3, 0, scheme, schemeconf, hash, str, days);
		Tuple2<String, String> tup4 = SSecurity.createCertificate(tup3.getFirstEntity(), tup3.getSecondEntity(), dn4, scheme, schemeconf, hash, str, days);
		
		System.out.println(tup.getFirstEntity());
		System.out.println(tup.getSecondEntity());
		System.out.println("=====================================================");
//		System.out.println(tup2.getFirstEntity());
		System.out.println(tup4.getFirstEntity());
		
		ByteArrayInputStream pemcert = new ByteArrayInputStream(tup4.getFirstEntity().getBytes(SUtil.UTF8));
		ByteArrayInputStream pemkey = new ByteArrayInputStream(tup4.getSecondEntity().getBytes(SUtil.UTF8));
		Blake2bDigest dig = new Blake2bDigest(512);
		dig.update("TestMessage".getBytes(SUtil.UTF8), 0, 11);
		byte[] msghash = new byte[64];
		dig.doFinal(msghash, 0);
		byte[] token = SSecurity.signWithPEM(msghash, pemcert, pemkey);
		ByteArrayInputStream trustedpemcert = new ByteArrayInputStream(tup.getFirstEntity().getBytes(SUtil.UTF8));
		System.out.println(SSecurity.verifyWithPEM(msghash, token, trustedpemcert));
		
//		X509CertificateHolder = new X509C
	}
}
