package jadex.platform.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import jadex.base.PlatformConfiguration;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IUntrustedMessageHandler;
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
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.SCloner;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
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
@Service
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
	
	/** Flag whether to use the platform secret for authentication. */
	protected boolean usesecret;
	
	/** Flag whether the platform secret should be printed during start. */
	protected boolean printsecret;
	
	/** Local platform authentication secret. */
	protected AbstractAuthenticationSecret platformsecret;
	
	/** Remote platform authentication secrets. */
	protected Map<IComponentIdentifier, AbstractAuthenticationSecret> remoteplatformsecrets;
	
	/** Flag whether to allow platforms to be associated with roles (clashes, spoofing problem?). */
	protected boolean allowplatformroles = false;
	
	/** Available virtual networks. */
	protected Map<String, AbstractAuthenticationSecret> networks;
	
	/** Available crypt suites. */
	protected Map<String, Class<?>> allowedcryptosuites;
	
	/** CryptoSuites currently initializing, value=Handshake state. */
	protected Map<String, HandshakeState> initializingcryptosuites;
	
	/** CryptoSuites currently in use. */
	protected Map<String, ICryptoSuite> currentcryptosuites;
	
	/** CryptoSuites that are expiring with expiration time. */
	protected MultiCollection<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites;
//	protected Map<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites;
	
	/** Map of entities and associated roles. */
	protected Map<String, Set<String>> roles;
	
	/** Crypto-Suite reset in progress. */
	protected IFuture<Void> cryptoreset; 
	
	/** Task for cleanup duties. */
	protected volatile IFuture<Void> cleanuptask;
	
	/** The list of network names (used by all service identifiers). */
	protected Set<String> networknames;
	
	/**
	 *  Initialization.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		if(!agent.getComponentIdentifier().getLocalName().equals("security"))
			agent.getLogger().warning("Security agent running as \"" + agent.getComponentIdentifier().getLocalName() +"\" instead of \"security\".");
		
		Map<String, String> activeprops = new HashMap<String, String>();
		Map<String, String> networkprops = new HashMap<String, String>();
		Map<String, String> remotepfprops = new HashMap<String, String>();
		roles = new HashMap<String, Set<String>>();
		
		jadex.commons.Properties settings = getSettingsService().getProperties(PROPERTIES_ID).get();
		if (settings != null)
		{
			Property[] proparr = settings.getProperties();
			for (Property prop : proparr)
			{
				if (prop.getName() == null)
				{
					activeprops.put(prop.getType(), prop.getValue());
				}
				else
				{
					if ("networks".equals(prop.getType()))
					{
						networkprops.put(prop.getName(), prop.getValue());
					}
					else if ("remoteplatformsecrets".equals(prop.getType()))
					{
						remotepfprops.put(prop.getName(), prop.getValue());
					}
					else if ("roles".equals(prop.getType()))
					{
						Set<String> eroles = roles.get(prop.getName());
						if (eroles == null)
						{
							eroles = new HashSet<String>();
							roles.put(prop.getName(), eroles);
						}
						
						eroles.add(prop.getValue());
					}
				}
			}
		}
		
		IArgumentsResultsFeature argfeat = agent.getComponentFeature(IArgumentsResultsFeature.class);
		Map<String, Object> args = argfeat.getArguments();
		if (args != null)
		{
			Set<String> argexcluded = new HashSet<String>();
			argexcluded.add(PROPERTY_NETWORK);
			argexcluded.add(PROPERTY_NETWORKSECRET);
			argexcluded.add(PROPERTY_REMOTEPLATFORM);
			argexcluded.add(PROPERTY_REMOTEPLATFORMSECRET);
			
			for (Map.Entry<String, Object> arg : args.entrySet())
			{
				if (!argexcluded.contains(arg.getKey()) && arg.getValue() instanceof String)
					activeprops.put(arg.getKey(), (String) arg.getValue());
			}
			
			Object name = args.get(PROPERTY_NETWORK);
			Object secret = args.get(PROPERTY_NETWORKSECRET);
			if (name instanceof String && secret instanceof String)
			{
				networkprops.put((String) name, (String) secret);
			}
			else if (name instanceof String[] && secret instanceof String[])
			{
				String[] aname = (String[]) name;
				String[] asecret = (String[]) secret;
				if (aname.length == asecret.length)
				{
					for (int i = 0; i < aname.length; ++i)
					{
						networkprops.put(aname[i], asecret[i]);
					}
				}
			}
			
			name = args.get(PROPERTY_REMOTEPLATFORM);
			secret = args.get(PROPERTY_REMOTEPLATFORMSECRET);
			if (name instanceof String && secret instanceof String)
			{
				remotepfprops.put((String) name, (String) secret);
			}
			else if (name instanceof String[] && secret instanceof String[])
			{
				String[] aname = (String[]) name;
				String[] asecret = (String[]) secret;
				if (aname.length == asecret.length)
				{
					for (int i = 0; i < aname.length; ++i)
					{
						remotepfprops.put(aname[i], asecret[i]);
					}
				}
			}
		}
		
		if (!activeprops.containsKey(ISecurityService.PROPERTY_USESECRET))
			activeprops.put(ISecurityService.PROPERTY_USESECRET, "true");
		
		if (!activeprops.containsKey(ISecurityService.PROPERTY_PRINTSECRET))
			activeprops.put(ISecurityService.PROPERTY_PRINTSECRET, "true");
		
		String secretstr = activeprops.get(ISecurityService.PROPERTY_PLATFORMSECRET);
		printsecret = "true".equals(activeprops.get(ISecurityService.PROPERTY_PRINTSECRET).toLowerCase());
		usesecret = "true".equals(activeprops.get(ISecurityService.PROPERTY_USESECRET).toLowerCase());
		
		if (usesecret && secretstr == null)
		{
			secretstr = KeySecret.createRandomAsString();
			activeprops.put(ISecurityService.PROPERTY_PLATFORMSECRET, secretstr);
			System.out.println("Generated new platform access key: "+secretstr.substring(KeySecret.PREFIX.length() + 1));
		}
		
		try
		{
			platformsecret = AbstractAuthenticationSecret.fromString(secretstr);
		}
		catch (IllegalArgumentException e)
		{
			secretstr = PasswordSecret.PREFIX + ":" + secretstr;
			platformsecret = AbstractAuthenticationSecret.fromString(secretstr);
			activeprops.put(ISecurityService.PROPERTY_PLATFORMSECRET, secretstr);
		}
		
		networks = new HashMap<String, AbstractAuthenticationSecret>();
		networknames = (Set<String>)PlatformConfiguration.getPlatformValue(agent.getComponentIdentifier(), PlatformConfiguration.DATA_NETWORKNAMESCACHE);
		for(Map.Entry<String, String> entry : networkprops.entrySet())
		{
			networks.put(entry.getKey(), AbstractAuthenticationSecret.fromString(entry.getValue()));
			networknames.add(entry.getKey());
		}
		
		remoteplatformsecrets = new HashMap<IComponentIdentifier, AbstractAuthenticationSecret>();
		for (Map.Entry<String, String> entry : remotepfprops.entrySet())
			remoteplatformsecrets.put(new ComponentIdentifier(entry.getKey()), AbstractAuthenticationSecret.fromString(entry.getValue()));
		
		saveSettings();
		
		if (printsecret && platformsecret != null)
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
		
		initializingcryptosuites = new HashMap<String, HandshakeState>();
		currentcryptosuites = Collections.synchronizedMap(new HashMap<String, ICryptoSuite>());
//		expiringcryptosuites = new HashMap<String, Tuple2<ICryptoSuite,Long>>();
		expiringcryptosuites = new MultiCollection<String, Tuple2<ICryptoSuite,Long>>();
		
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
	
	//---- ISecurityService methods. ----
	
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
						expiringcryptosuites.add(rplat, new Tuple2<ICryptoSuite, Long>(cs, System.currentTimeMillis() + TIMEOUT));
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
						Collection<Tuple2<ICryptoSuite, Long>> tupcoll = expiringcryptosuites.get(splat);
						if (tupcoll != null)
						{
							for (Tuple2<ICryptoSuite, Long> tup : tupcoll)
							{
								cs = tup.getFirstEntity();
								cleartext = cs.decryptAndAuth(content);
								if (cleartext != null)
									break;
							}
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
	
	/**
	 *  Checks if platform secret is used.
	 *  
	 *  @return True, if so.
	 */
	public IFuture<Boolean> isUsePlatformSecret()
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Boolean>()
		{
			public IFuture<Boolean> execute(IInternalAccess ia)
			{
				return new Future<Boolean>(usesecret);
			}
		});
	}
	
	/**
	 *  Sets whether the platform secret should be used.
	 *  
	 *  @param useplatformsecret The flag.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setUsePlatformSecret(final boolean useplatformsecret)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				usesecret = useplatformsecret;
				saveSettings();
				resetCryptoSuites();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Checks if platform secret is printed.
	 *  
	 *  @return True, if so.
	 */
	public IFuture<Boolean> isPrintPlatformSecret()
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Boolean>()
		{
			public IFuture<Boolean> execute(IInternalAccess ia)
			{
				return new Future<Boolean>(printsecret);
			}
		});
	}
	
	/**
	 *  Sets whether the platform secret should be printed.
	 *  
	 *  @param printplatformsecret The flag.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setPrintPlatformSecret(final boolean printplatformsecret)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				printsecret = printplatformsecret;
				saveSettings();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Sets a new network.
	 * 
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setNetwork(final String networkname, final String secret)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(secret == null)
				{
					networks.remove(networkname);
					networknames.remove(networkname);
				}
				else
				{
					AbstractAuthenticationSecret asecret = AbstractAuthenticationSecret.fromString(secret);
					networks.put(networkname, asecret);
					networknames.add(networkname);
				}
				
				saveSettings();
				
				resetCryptoSuites();
				
				//TODO: RESET keys / sessions?
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Gets the current networks and secrets. 
	 *  
	 *  @return The current networks and secrets.
	 */
	public IFuture<Map<String, String>> getNetworks()
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Map<String, String>>()
		{
			public IFuture<Map<String, String>> execute(IInternalAccess ia)
			{
				Map<String, String> ret = new HashMap<String, String>();
				
				for(Map.Entry<String, AbstractAuthenticationSecret> entry : networks.entrySet())
				{
					ret.put(entry.getKey(), entry.getValue().toString());
				}
				
				return new Future<Map<String,String>>(ret);
			}
		});
	}
	
//	/**
//	 *  Gets the current network names. 
//	 *  @return The current networks names.
//	 */
//	public IFuture<String[]> getNetworkNames()
//	{
//		return agent.getExternalAccess().scheduleStep(new IComponentStep<String[]>()
//		{
//			public IFuture<String[]> execute(IInternalAccess ia)
//			{
//				String[] ret = new String[networks.size()];
//				
//				int i=0;
//				for(Map.Entry<String, AbstractAuthenticationSecret> entry : networks.entrySet())
//				{
//					ret[i++] = entry.getKey(); 
//				}
//				
//				return new Future<String[]>(ret);
//			}
//		});
//	}
	
	/**
	 *  Gets the current network names. 
	 *  @return The current networks names.
	 */
	public IFuture<Set<String>> getNetworkNames()
	{
		return new Future<Set<String>>(networknames);
	}
	
	/**
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<String> getPlatformSecret(final IComponentIdentifier cid)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<String>()
		{
			public IFuture<String> execute(IInternalAccess ia)
			{
				AbstractAuthenticationSecret secret = null;
				if (cid == null)
					secret = getInternalPlatformSecret();
				else
					getInternalPlatformSecret(cid);
				return new Future<String>(secret != null ? secret.toString() : null);
			}
		});
	}
	
	/**
	 *  Sets the secret of a platform.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<Void> setPlatformSecret(final IComponentIdentifier cid, final String secret)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// TODO: Refresh?
				if (secret == null)
				{
					if (cid == null || agent.getComponentIdentifier().getRoot().equals(cid))
						platformsecret = null;
					else
						remoteplatformsecrets.remove(cid);
				}
				else
				{
					AbstractAuthenticationSecret authsec = AbstractAuthenticationSecret.fromString(secret);
					
					if (cid == null || agent.getComponentIdentifier().getRoot().equals(cid))
						platformsecret = authsec;
					else
						remoteplatformsecrets.put(cid, authsec);
				}
				
				saveSettings();
				
				if (usesecret)
					resetCryptoSuites();
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Adds a role for an entity (platform or network name).
	 *  
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addRole(final String entity, final String role)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Set<String> eroles = roles.get(entity);
				if (eroles == null)
				{
					eroles = new HashSet<String>();
					roles.put(entity, eroles);
				}
				
				eroles.add(role);
				
				saveSettings();
				
				resetCryptoSuites();
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Adds a role of an entity (platform or network name).
	 *  
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeRole(final String entity, final String role)
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Set<String> eroles = roles.get(entity);
				if (eroles != null)
				{
					eroles.remove(role);
					if (eroles.isEmpty())
						roles.remove(entity);
				}
				
				saveSettings();
				
				resetCryptoSuites();
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Gets a copy of the current role map.
	 *  
	 *  @return Copy of the role map.
	 */
	public IFuture<Map<String, Set<String>>> getRoleMap()
	{
		return agent.getExternalAccess().scheduleStep(new IComponentStep<Map<String, Set<String>>>()
		{
			@SuppressWarnings("unchecked")
			public IFuture<Map<String, Set<String>>> execute(IInternalAccess ia)
			{
				return new Future<Map<String,Set<String>>>((Map<String, Set<String>>) SCloner.clone(roles));
			}
		});
	}
	
	//---- Internal direct access methods. ----
	
	/**
	 *  Get access to the stored virtual network configurations.
	 * 
	 *  @return The stored virtual network configurations.
	 */
	public Map<String, AbstractAuthenticationSecret> getInternalNetworks()
	{
		return networks;
	}
	
	/**
	 *  Gets the local platform secret.
	 */
	public AbstractAuthenticationSecret getInternalPlatformSecret()
	{
		return platformsecret;
	}
	
	/**
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Secret or null.
	 */
	public AbstractAuthenticationSecret getInternalPlatformSecret(IComponentIdentifier cid)
	{
		cid = cid.getRoot();
		if (cid.equals(agent.getComponentIdentifier().getRoot()))
			return getInternalPlatformSecret();
		return remoteplatformsecrets.get(cid.getRoot());
	}
	
	/**
	 *  Gets the role map.
	 * 
	 *  @return The role map.
	 */
	public Map<String, Set<String>> getInternalRoles()
	{
		return roles;
	}
	
	/**
	 *  Checks whether to use platform secret.
	 *  
	 *  @return True, if used.
	 */
	public boolean getInternalUsePlatformSecret()
	{
		return usesecret;
	}
	
	/**
	 *  Checks whether to allow platform roles.
	 *  @return True, if used.
	 */
	public boolean getInternalAllowPlatformRoles()
	{
		return allowplatformroles;
	}
	
	/**
	 *  Get component ID.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return agent.getComponentIdentifier();
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
		
		for (String pf : expiringcryptosuites.keySet())
		{
			Collection<Tuple2<ICryptoSuite, Long>> coll = expiringcryptosuites.get(pf);
			for (Tuple2<ICryptoSuite, Long> tup : coll)
			{
				if (time > tup.getSecondEntity())
					expiringcryptosuites.removeObject(pf, tup);
			}
		}
//		for (Iterator<Map.Entry<String, Tuple2<ICryptoSuite, Long>>> it = expiringcryptosuites.entrySet().iterator(); it.hasNext(); )
//		{
//			Map.Entry<String, Tuple2<ICryptoSuite, Long>> entry = it.next();
//			if (time > entry.getValue().getSecondEntity())
//				it.remove();
//		}
	}
	
	//-------- Utility functions -------
	
	/**
	 *  Resets the crypto suite in case of security state change (network secret changes etc.).
	 */
	protected void resetCryptoSuites()
	{
		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if (cryptoreset != null)
				{
					long resetdelay = TIMEOUT >>> 3;
					cryptoreset = ia.getComponentFeature(IExecutionFeature.class).waitForDelay(resetdelay, new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							Map<String, ICryptoSuite> expire = new HashMap<String, ICryptoSuite>(currentcryptosuites);
							
							synchronized (currentcryptosuites)
							{
								long exptime = System.currentTimeMillis() + TIMEOUT;
								for (Map.Entry<String, ICryptoSuite> suite : expire.entrySet())
								{
									expiringcryptosuites.add(suite.getKey(), new Tuple2<ICryptoSuite, Long>(suite.getValue(), exptime));
									
									// Reinitialize handshakes.
									String rplat = suite.getKey();
									initializeHandshake(rplat);
								}
								currentcryptosuites.clear();
							}
							
							cryptoreset = null;
							
							return IFuture.DONE;
						}
					});
				}
				return IFuture.DONE;
			}
		});
	}
	
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
				HandshakeState state = initializingcryptosuites.remove(receiver.getRoot().toString());
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
	 *  Saves the current settings.
	 */
	protected void saveSettings()
	{
		jadex.commons.Properties settings = new jadex.commons.Properties();
		
		settings.addProperty(new Property(ISecurityService.PROPERTY_USESECRET, String.valueOf(usesecret)));
		settings.addProperty(new Property(ISecurityService.PROPERTY_PRINTSECRET, String.valueOf(printsecret)));
		
		if (platformsecret != null)
			settings.addProperty(new Property(ISecurityService.PROPERTY_PLATFORMSECRET, platformsecret.toString()));
		
		if(networks != null && networks.size() > 0)
		{
			for (Map.Entry<String, AbstractAuthenticationSecret> entry : networks.entrySet())
				settings.addProperty(new Property(entry.getKey(), "networks", entry.getValue().toString()));
		}
		
		if (remoteplatformsecrets != null && remoteplatformsecrets.size() > 0)
		{
			for (Map.Entry<IComponentIdentifier, AbstractAuthenticationSecret> entry : remoteplatformsecrets.entrySet())
				settings.addProperty(new Property(entry.getKey().toString(), "remoteplatformsecrets", entry.getValue().toString()));
		}
		
		if (roles != null && roles.size() > 0)
		{
			List<Tuple2<String, String>> flatroles = flattenRoleMap(roles);
			for (Tuple2<String, String> tup : flatroles)
				settings.addProperty(new Property(tup.getFirstEntity(), "roles", tup.getSecondEntity()));
		}
		
		getSettingsService().setProperties(PROPERTIES_ID, settings);
		getSettingsService().saveProperties().get();
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
	protected class SecurityMessageHandler implements IUntrustedMessageHandler
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
					expiringcryptosuites.add(rplat.toString(), new Tuple2<ICryptoSuite, Long>(oldcs, System.currentTimeMillis() + TIMEOUT));
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
						initializingcryptosuites.remove(secmsg.getSender().getRoot().toString());
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
	
	/**
	 *   Helper for flattening the role map.
	 */
	public static final List<Tuple2<String, String>> flattenRoleMap(Map<String, Set<String>> rolemap)
	{
		List<Tuple2<String, String>> ret = new ArrayList<Tuple2<String,String>>();
		
		for (Map.Entry<String, Set<String>> entry : rolemap.entrySet())
		{
			for (String rolename : entry.getValue())
			{
				ret.add(new Tuple2<String, String>(entry.getKey(), rolename));
			}
		}
		
		return ret;
	}
}
