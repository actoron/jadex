package jadex.platform.service.security;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.security.IAuthorizable;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.KeyStoreEntry;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.ChangeEvent;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;

import java.net.InetAddress;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Service
public class SecurityService implements ISecurityService
{
	//-------- constants --------
	
	/** Properties id for the settings service. */
	public static final String	PROEPRTIES_ID	= "securityservice";

	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The service id. */
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** Flag to enable / disable password protection. */
	protected boolean	usepass;
	
	/** Determines if password was specified during creation. (i.e. in Platform Configuration) */
	protected boolean argsusepass;
	
	/** Print password on startup or change. */
	protected boolean	printpass;
	
	/** The local password (if any). */
	protected String	password;
	
	/** The stored passwords. */
	protected Map<String, String>	platformpasses;
	
	/** The stored passwords. */
	protected Map<String, String>	networkpasses;
	
	/** The trusted lan mode. */
	protected boolean trustedlan;
	
	/** Determines if trusted lan was specified during creation. */
	protected boolean argstrustedlan;
	
	/** The default message validity duration. */
	protected long valdur;
	
	
	/** The path to the keystore. */
	protected String storepath;
	
	/** The keystore password. */
	protected String storepass;
	
	/** The key password. */
	protected String keypass;
	
	/** The ContextService. */
	protected IContextService contextser;
	
	/** The currently valid digests. (secret -> timestamp, digest)*/
	protected Map<String, Tuple2<Long, byte[]>> digests;

	
	/** The keystore. */
	protected KeyStore keystore;
	
	/** The list of key aquire mechanisms. */
	protected List<AAcquisitionMechanism> mechanisms;
	
	/** The currently selected mechanism. */
	protected int selmech;
	
	/** The futures of active subscribers. */
	protected Set<SubscriptionIntermediateFuture<ChangeEvent<Object>>> subscribers;
	
	
	/** The mappings of virtual names to platform names. */
	protected Map<String, Set<String>> virtualsmap;
	
	
	/** The network ips, cached for speed. */
	protected List<InetAddress> networkips;
	
	//-------- setup --------
	
	/**
	 *  Create a security service.
	 */
	public SecurityService()
	{
		this(Boolean.TRUE, true, Boolean.FALSE, null, null, null, null, null);
	}

	/**
	 *  Create a security service.
	 */
	public SecurityService(Boolean usepass, boolean printpass, Boolean trustedlan, 
		String[] networknames, String[] networkpasses)
	{
		this(usepass, printpass, trustedlan, networknames, networkpasses, null, null, null);
	}
	
	/**
	 *  Create a security service.
	 */
	public SecurityService(Boolean usepass, boolean printpass, Boolean trustedlan, 
		String[] networknames, String[] networkpasses, AAcquisitionMechanism[] mechanisms, 
		Map<String, Set<String>> namemap, Long valdur)
	{
		this.valdur = valdur==null? 5*65536: valdur.longValue(); // 5 min default
		this.virtualsmap = namemap==null? new HashMap<String, Set<String>>(): namemap;
		this.subscribers = new LinkedHashSet<SubscriptionIntermediateFuture<ChangeEvent<Object>>>();
		this.platformpasses	= new LinkedHashMap<String, String>();
		this.networkpasses	= new LinkedHashMap<String, String>();
		
		if(networknames!=null)
		{
			for(int i=0; i<networknames.length; i++)
			{
				this.networkpasses.put(networknames[i], networkpasses==null? "": networkpasses[i]);
			}
		}
		
		this.digests = new HashMap<String, Tuple2<Long, byte[]>>();
		this.usepass = usepass!=null? usepass.booleanValue(): true;
		this.argsusepass = usepass != null;
		this.printpass = printpass;
		this.trustedlan = trustedlan!=null? trustedlan.booleanValue(): false;
		this.argstrustedlan = trustedlan != null;
		this.storepath = "./keystore";
		this.storepass = "keystore";
		this.keypass = "keystore";
		this.mechanisms = new ArrayList<AAcquisitionMechanism>();
		if(mechanisms!=null)
		{
			for(AAcquisitionMechanism mech: mechanisms)
			{
				mech.init(this);
				this.mechanisms.add(mech);
			}
		}
		else
		{
			AAcquisitionMechanism mech = new DecentralizedAcquisitionMechanism();
			mech.init(this);
			this.mechanisms.add(mech);
			mech = new TTPAcquisitionMechanism();
			mech.init(this);
			this.mechanisms.add(mech);
		}
	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void>	start()
	{
		final Future<Void>	ret	= new Future<Void>();
//		this.trustednets = new ArrayList<String>();
		
		component.getComponentFeature(IRequiredServicesFeature.class).searchService(IContextService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IContextService, Void>(ret)
		{
			public void customResultAvailable(IContextService result)
			{
				contextser = result;
				
				contextser.getNetworkIps().addResultListener(new ExceptionDelegationResultListener<List<InetAddress>, Void>(ret)
				{
					public void customResultAvailable(List<InetAddress> ips)
					{
						networkips = ips;
						
						setTrustedLanMode(trustedlan);
					
						getSettingsService().addResultListener(new ExceptionDelegationResultListener<ISettingsService, Void>(ret)
						{
							public void customResultAvailable(final ISettingsService settings)
							{
								if(settings==null)
								{
									// generate new password, if no security settings exist, yet.
									password	= UUID.randomUUID().toString().substring(0, 12);
			//						usepass	= true;
									ret.setResult(null);
								}
								else
								{
									settings.getProperties(PROEPRTIES_ID)
										.addResultListener(new ExceptionDelegationResultListener<Properties, Void>(ret)
									{
										public void customResultAvailable(final Properties props)
										{
											// generate new password, if no security settings exist, yet.
											final boolean	genpass	= props==null || props.getProperty("password")==null; 
											if(genpass)
											{
												password	= UUID.randomUUID().toString().substring(0, 12);
					//							usepass	= true;
											}
	
											if(props!=null)
											{
												selmech = props.getIntProperty("selected_mechanism"); 
	//											System.out.println("selm: "+selmech);
												publishEvent(new ChangeEvent<Object>(null, PROPERTY_SELECTEDMECHANISM, Integer.valueOf(selmech)));
											}
											
											final IExternalAccess	access	= component.getExternalAccess();
											
											settings.registerPropertiesProvider(PROEPRTIES_ID, new IPropertiesProvider()
											{
												public IFuture<Void> setProperties(final Properties props)
												{
													return access.scheduleStep(new ImmediateComponentStep<Void>()
													{
														public IFuture<Void> execute(IInternalAccess ia)
														{
															String spa = props.getStringProperty("storepath");
															if(spa!=null && spa.length()>0)
																storepath = spa;
															String sps = props.getStringProperty("storepass");
															if(sps!=null && spa.length()>0)
																storepass = sps;
															String kp = props.getStringProperty("keypass");
															if(kp!=null && kp.length()>0)
																keypass = kp;
															long vd = props.getLongProperty("validityduration");
															if(vd>0)
																valdur = vd;
															
															if(!argsusepass)
															{
																usepass = props.getBooleanProperty("usepass");
					//											System.out.println("usepass: "+usepass);
															}
															password	= props.getStringProperty("password");
															
															if(!argstrustedlan)
															{
																setTrustedLanMode(props.getBooleanProperty("trustedlan"));
															}
															
															Property[]	passes	= props.getProperties("passwords");
					//										platformpasses	= new LinkedHashMap<String, String>();
															for(int i=0; i<passes.length; i++)
															{
																String val = passes[i].getValue();
																platformpasses.put(passes[i].getName(), val==null? "": val);
															}
															
															// Not allowed to add internal trusted platforms if flag is false
//															List<InetAddress> addrs = contextser.getNetworkIps();
															Set<String> trs = new HashSet<String>();
															for(InetAddress addr: networkips)
															{
																trs.add(addr.getHostAddress());
															}
																
															Property[]	networks	= props.getProperties("networks");
					//										networkpasses	= new LinkedHashMap<String, String>();
															for(int i=0; i<networks.length; i++)
															{
					//											System.out.println("value:"+networks[i].getValue()+".");
																String val = networks[i].getValue();
																if(trustedlan || !trs.contains(networks[i].getName()))
																{
																	networkpasses.put(networks[i].getName(), val==null? "": val);
																}
															}
															
															selmech = props.getIntProperty("selmech");
															
															if(mechanisms!=null)
															{
																for(AAcquisitionMechanism mech: mechanisms)
																{
																	Properties mps = props.getSubproperty(SReflect.getInnerClassName(mech.getClass()));
																	if(mps!=null)
																	{
																		mech.setProperties(mps);
																	}
																}
															}
															
															Properties sb = props.getSubproperty("virtuals");
															if(sb!=null)
															{
																Property[] ps = sb.getProperties();
																for(Property p: ps)
																{
																	addVirtual(p.getType(), p.getValue());
																}
															}
															
															publishCurrentState();
															
															return IFuture.DONE;
														}
													});
												}
												
												public IFuture<Properties> getProperties()
												{
													return access.scheduleStep(new ImmediateComponentStep<Properties>()
													{
														public IFuture<Properties> execute(IInternalAccess ia)
														{
															Properties	ret	= new Properties();
															ret.addProperty(new Property("validityduration", ""+valdur));
															ret.addProperty(new Property("usepass", ""+usepass));
															ret.addProperty(new Property("password", password));
															ret.addProperty(new Property("selected_mechanism", ""+selmech));
															if(platformpasses!=null)
															{
																for(String platform: platformpasses.keySet())
																{
																	ret.addProperty(new Property(platform, "passwords", platformpasses.get(platform)));
																}
															}
															if(networkpasses!=null)
															{
																for(String network: networkpasses.keySet())
																{
																	ret.addProperty(new Property(network, "networks", networkpasses.get(network)));
																}
															}
															ret.addProperty(new Property("trustedlan", ""+trustedlan));
															
															ret.addProperty(new Property("storepath", storepath));
															ret.addProperty(new Property("storepass", storepass));
															ret.addProperty(new Property("keypass", keypass));
															
															ret.addProperty(new Property("selmech", ""+selmech));
															
															if(mechanisms!=null)
															{
																for(AAcquisitionMechanism mech: mechanisms)
																{
																	ret.addSubproperties(SReflect.getInnerClassName(mech.getClass()), mech.getProperties());
																}
															}
															
															if(virtualsmap!=null && !virtualsmap.isEmpty())
															{
																Properties sb = new Properties();
																ret.addSubproperties("virtuals", sb);
																for(Map.Entry<String, Set<String>> virtual: virtualsmap.entrySet())
																{
																	Set<String> vals = virtual.getValue();
																	if(vals==null || vals.isEmpty())
																	{
																		sb.addProperty(new Property(virtual.getKey(), null));
																	}
																	else
																	{
																		for(String value: virtual.getValue())
																		{
																			sb.addProperty(new Property(virtual.getKey(), value));
																		}
																	}
																}
															}
															
	//														System.out.println("fini2");
															
															return new Future<Properties>(ret);
														}
													});
												}
											}).addResultListener(new DelegationResultListener<Void>(ret)
											{
												public void customResultAvailable(Void result)
												{
													// If new password was generated, save settings such that new platform instances use it.
													if(genpass)
													{
														if(printpass && usepass)
														{
															System.out.println("Generated platform password: "+password);
														}
														settings.saveProperties().addResultListener(new DelegationResultListener<Void>(ret));
													}
													else
													{
														if(printpass && usepass)
														{
															System.out.println("Using stored platform password: "+password);
														}
														super.customResultAvailable(result);
													}
												}
											});
										}
									});
								}
							}
						});
					}
				});
			}
		});
		
	
		
		return ret;
	}

	/**
	 *  Get the settings service.
	 */
	public IFuture<ISettingsService> getSettingsService()
	{
		final Future<ISettingsService> ret = new Future<ISettingsService>();
		IFuture<ISettingsService> fut = component.getComponentFeature(IRequiredServicesFeature.class).searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		fut.addResultListener(new DelegationResultListener<ISettingsService>(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Save keystore on disk
		saveKeyStore();
		
		// Save settings
		component.getComponentFeature(IRequiredServicesFeature.class).searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.deregisterPropertiesProvider(PROEPRTIES_ID)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						SecurityService.this.platformpasses	= null;
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				SecurityService.this.platformpasses	= null;
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the keystore.
	 */
	protected KeyStore getKeyStore()
	{
		if(keystore==null)
		{
			// Fetch keystore and possible auto-generate self-signed certificate
			String name = component.getComponentIdentifier().getPlatformPrefix();
			this.keystore = SSecurity.getKeystore(storepath, storepass, keypass, name);
		}
		
		return keystore;
	}
	
	//-------- password management --------
	
	/**
	 *  Check if password protection is enabled.
	 *  @return	True, if password protection is enabled.
	 */
	public IFuture<Boolean>	isUsePassword()
	{
		return new Future<Boolean>(usepass);
	}

	/**
	 *  Enable / disable password protection.
	 *  @param enable	If true, password protection is enabled, otherwise disabled.
	 *  @throws Exception, when enable is true and no password is set.
	 */
	public IFuture<Void>	setUsePassword(boolean enable)
	{
		IFuture<Void>	ret;
		if(enable && password==null)
		{
			ret	= new Future<Void>(new IllegalStateException("Cannot enable password protection, no password set."));
		}
		else
		{
			this.usepass	= enable;
			ret	= IFuture.DONE;
			
			if(printpass && usepass)
			{
				System.out.println("Using stored platform password: "+password);
			}
			
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_USEPASS, enable));
		}
		
		return ret;
	}

	/**
	 *  Get the local password.
	 *  @return	The password of the local platform (if any).
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getLocalPassword()
	{
		return new Future<String>(password);
	}

	/**
	 *  Set the local password.
	 *  @param password	The password of the local platform. 
	 *  @throws  Exception, when a null password is provided and use password is true.
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setLocalPassword(String password)
	{
		IFuture<Void>	ret;
		if(password==null && usepass)
		{
			ret	= new Future<Void>(new IllegalStateException("Cannot set password to null, when password protection is enabled."));
		}
		else
		{
			this.password	= password;
			ret	= IFuture.DONE;
			if(printpass && usepass)
			{
				System.out.println("Using new platform password: "+password);
			}
			
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_LOCALPASS, password));
		}
		return ret;
	}
	
	/**
	 *  Get the validity duration.
	 *  @return The validityduration.
	 */
	public IFuture<Long> getValidityDuration()
	{
		return new Future<Long>(Long.valueOf(valdur));
	}

	/**
	 *  Set the validity duration.
	 *  @param validityduration The validityduration to set.
	 */
	public IFuture<Void> setValidityDuration(long validityduration)
	{
		this.valdur = validityduration;
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_VALIDITYDURATION, Long.valueOf(valdur)));
		return IFuture.DONE;
	}

	/**
	 *  Get the password for a target component.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getPlatformPassword(IComponentIdentifier target)
	{
		String	starget	= target.getPlatformPrefix();
		String	ret	= platformpasses.get(starget);
		if(ret==null && starget.equals(component.getComponentIdentifier().getPlatformPrefix()))
		{
			ret	= this.password;
		}
		return new Future<String>(ret);
	}

	/**
	 *  Set the password for a target component.
	 *  Note that passwords are currently stored on a per platform basis,
	 *  i.e. there is only one stored password for all components of the same platform.
	 *  Moreover, the security service strips the auto-generated extension from the platform
	 *  name and therefore can reuse the password for different instances of the same platform.
	 *  @param target	The id of the target component.
	 *  @param password	The password or null if no password should be used.
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setPlatformPassword(IComponentIdentifier target, String password)
	{
		if(password!=null)
		{
			platformpasses.put(target.getPlatformPrefix(), password);
		}
		else
		{
			// Use remove to avoid keeping old mappings forever (name would still be stored otherwise)
			platformpasses.remove(target.getPlatformPrefix());
		}
		
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_PLATFORMPASS, platformpasses));

		return IFuture.DONE;		
	}

	/**
	 *  Get the password for a network.
	 *  @param targetName	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getNetworkPassword(String network)
	{
		String	ret	= networkpasses.get(network);
		return new Future<String>(ret);
	}

	/**
	 *  Set the password for a network.
	 *  @param network	The id of the network.
	 *  @param password	The password or null if no password should be used.
	 */
	@SecureTransmission
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setNetworkPassword(String network, String password)
	{
		if(password!=null)
		{
			networkpasses.put(network, password);			
		}
		else
		{
			// Use remove to avoid keeping old mappings forever (name would still be stored otherwise)
			networkpasses.remove(network);
		}
		
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_NETWORKPASS, networkpasses));

		return IFuture.DONE;	
	}
	
	/**
	 *  Get all stored passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	@SecureTransmission
	// Todo: passwords are transferred in plain text unless transport uses encryption.
	public IFuture<Map<String, String>>	getPlatformPasswords()
	{
		return new Future<Map<String, String>>(platformpasses);
	}	
	
	/**
	 *  Get all stored network passwords.
	 *  @return A map containing the stored passwords as pairs (network name -> password).
	 */
	@SecureTransmission
	// Todo: passwords are transferred in plain text unless transport uses encryption.
	public IFuture<Map<String, String>>	getNetworkPasswords()
	{
		return new Future<Map<String, String>>(networkpasses);
	}
	
	/**
	 *  Set the trusted lan mode.
	 *  @param allowed The flag if it is allowed.
	 */
	public IFuture<Void> setTrustedLanMode(boolean allowed)
	{
		List<InetAddress> addrs = networkips;//contextser.getNetworkIps();
		
		if(allowed)
		{
			for(InetAddress addr: addrs)
			{
				if(!networkpasses.keySet().contains(addr.getHostAddress()))
				{
					setNetworkPassword(addr.getHostAddress(), "");
				}
			}
		}
		else if(!allowed)
		{
			for(InetAddress addr: addrs)
			{
				setNetworkPassword(addr.getHostAddress(), null);
			}
		}
		
		this.trustedlan = allowed;
		
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_TRUSTEDLAN, allowed));
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get the trusted lan mode.
	 *  @return True if is in trusted lan mode.
	 */
	public IFuture<Boolean> isTrustedLanMode()
	{
		return new Future<Boolean>(trustedlan? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set the keystore info.
	 *  @return The path to the keystore. The password of the store. The password of the key.
	 */
	public IFuture<String[]> getKeystoreInfo()
	{
		return new Future<String[]>(new String[]{storepath, storepass, keypass});
	}
	
	/**
	 *  Set the keystore info.
	 *  @param storepath The path to the keystore.
	 *  @param storepass The password of the store.
	 *  @param keypass The password of the key.
	 */
	public IFuture<Void> setKeystoreInfo(String storepath, String storepass, String keypass)
	{
		if(storepath!=null)
			this.storepath = storepath;
		if(storepass!=null)
			this.storepass = storepass;
		if(keypass!=null)
			this.keypass = keypass;
		
		// reset keystore
		this.keystore = null;
		
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTORESETTINGS, new String[]{storepath, storepass, keypass}));
		
		return IFuture.DONE;
	}

	//-------- request validation --------
	
	/**
	 *  Validate a request.
	 *  @param request	The request to be validated.
	 *  @throws	SecurityException, when request is not valid.
	 */
	public IFuture<Void>	validateRequest(IAuthorizable request)
	{
		String	error	= null;
		if(Security.PASSWORD.equals(request.getSecurityLevel()) && usepass && password!=null)
		{
			if(request.getAuthenticationData()!=null)
			{
				error = checkDigests(request, password, networkpasses);
			}
			else
			{
				error	= "Shared secret required.";
			}
		}
		
		return error==null ? new Future<Void>((Void)null) : new Future<Void>(new SecurityException(error+" "+request));
	}
	
	/**
	 *  Get the digest.
	 */
	public byte[] getDigest(long timestamp, String secret)
	{
		byte[] ret;
		// Get the digest that belongs to the secret
		Tuple2<Long, byte[]> tst = digests.get(secret);
		Long ts = Long.valueOf(timestamp);
		
		// Check if the timestamp of the digest is still ok
		if(tst!=null && tst.getFirstEntity().equals(ts))
		{
//			System.out.println("reuse: "+timestamp+" "+secret);
			ret = tst.getSecondEntity();
		}
		else
		{
			ret = buildDigest(timestamp, secret);
			digests.put(secret, new Tuple2<Long, byte[]>(ts, ret));
		}
		return ret;
	}
	
	/**
	 *  Check if the test digest in contained in the digest list. 
	 */
	public static boolean checkDigest(byte[] test, List<byte[]> digests)
	{
		boolean ret = false;
		for(byte[] dig: digests)
		{
			ret = Arrays.equals(dig, test);
			if(ret)
				break;
		}
		return ret;
	}
	
	/**
	 *  Check if there is a shared secret.
	 */
	public String checkDigests(IAuthorizable request, String password, Map<String, String> networkpasses)
	{
		String ret = null;
		
		List<byte[]> digests = request.getAuthenticationData();
		long timestamp = request.getTimestamp();
		long vd = request.getValidityDuration()==0? valdur: request.getValidityDuration();
		
		String prefix = request.getValidityDuration()==0? request.getDigestContent(): 
			request.getValidityDuration()+request.getDigestContent();
//		String prefix = request.getValidityDuration()+request.getDigestContent();
		
		boolean tst = false;
		// because timestamp is stripped, validity duration needs to be extended into future,
		// i.e. timestamp is valid in relative range -1..2 as follows:
		// |- vd - | - stripped timestamp in vd range -|- vd -|
		// -1      0                                   1      2
		long	dt	= System.currentTimeMillis()-timestamp;
		if(dt<0 && dt>-vd || dt>=0 && dt<2*vd)
		{
			// test if other knows my password
			tst = checkDigest(buildDigest(timestamp, prefix+password), digests);
			
			// test if other shares one of my networks
			if(!tst)
			{
				for(String net: networkpasses.keySet())
				{
					byte[] netdig = buildDigest(timestamp, prefix+net+networkpasses.get(net));
					tst = checkDigest(netdig, digests);
					if(tst)
						break;
				}
			}
			
			if(!tst)
			{
				ret = "No shared secret.";
			}
		}
		else
		{
//			System.out.println("Timestamp too old: "+timestamp+", vd="+vd);
			ret = "Timestamp too old.";
		}
		
		return ret;
	}
	
	/**
	 *  Preprocess a request.
	 *  Adds authentication data to the request, if required by the intended target.
	 *  @param request	The request to be preprocessed.
	 *  @param target	The target to which the request should be sent later.
	 */
	public IFuture<Void>	preprocessRequest(IAuthorizable request, IComponentIdentifier target)
	{
		long	timestamp	= System.currentTimeMillis();
//		System.out.println("ts1: "+SUtil.arrayToString(SUtil.longToBytes(timestamp)));
//		timestamp = timestamp>>>16<<16; // New digest every minute
		long vd = request.getValidityDuration()==0? valdur: request.getValidityDuration();
		int num = SUtil.log2(vd);
		for(int i=0; i<num; i++)
		{
			timestamp >>>= 1;
		}
		for(int i=0; i<num; i++)
		{
			timestamp <<= 1;
		}
//		System.out.println("ts2: "+SUtil.arrayToString(SUtil.longToBytes(timestamp)));
		request.setTimestamp(timestamp);
		
		List<byte[]> authdata = new ArrayList<byte[]>();

		String prefix = request.getValidityDuration()==0? request.getDigestContent(): 
			request.getValidityDuration()+request.getDigestContent();
		
		if(target!=null)
		{
			// First password of target
			String	stripped	= target.getPlatformPrefix();
			// Use stored password or local password for local targets.  
			String	pw	= platformpasses.containsKey(stripped)? platformpasses.get(stripped)
				: stripped.equals(component.getComponentIdentifier().getPlatformPrefix())? password : null;
	
			if(pw!=null)
			{
				authdata.add(getDigest(timestamp, prefix+pw));
	//			System.out.println("sending auth data: "+new String(Base64.encode(request.getAuthenticationData()))+", "+pw+", "+timestamp);
			}
		}
		else
		{
			// + own
			authdata.add(getDigest(timestamp, prefix+password));
			
			// Add all password authentications
			for(String name: platformpasses.keySet())
			{
				authdata.add(getDigest(timestamp, prefix+name+platformpasses.get(name)));
			}
		}
		
		// Add all network authentications
		for(String net: networkpasses.keySet())
		{
			authdata.add(getDigest(timestamp, prefix+net+networkpasses.get(net)));
		}
		
		// Add trusted authentications (in case other has turned on lan trusted and this one not)
		if(!trustedlan)
		{
			for(InetAddress addr: networkips)//contextser.getNetworkIps())
			{
				authdata.add(getDigest(timestamp, prefix+addr.getHostAddress()));
			}
		}
		
		request.setAuthenticationData(authdata);
		
//		System.out.println(authdata.size());
		
		return IFuture.DONE;
	}
	
	/**
	 *  Sign a byte[] with the platform key that is stored in the
	 *  keystore under the platform prefix name.
	 */
	public IFuture<byte[]> signCall(byte[] content)
	{
		Future<byte[]> ret = new Future<byte[]>();
		
		try
		{
//			Provider[] provs = java.security.Security.getProviders();
//			System.out.println("prov: "+SUtil.arrayToString(provs));
//			java.security.Security.addProvider(new BouncyCastleProvider());
//			provs = java.security.Security.getProviders();
//			System.out.println("prov: "+SUtil.arrayToString(provs));
			
			String name = component.getComponentIdentifier().getPlatformPrefix();
			Key key = getKeyStore().getKey(name, keypass.toCharArray());
			Certificate cert = getKeyStore().getCertificate(name);
			Signature eng = Signature.getInstance(SSecurity.getAlgorithm(cert));
			byte[] signed = SSecurity.signContent((PrivateKey)key, eng, content);
		    ret.setResult(signed);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Check if the name belongs to the mappings of one
	 *  of the virtual names.
	 *  @param virtuals The virtual names.
	 *  @param name The name to check.
	 *  @return True, if name is ok.
	 */
	public IFuture<Void> checkVirtual(String[] virtuals, String name)
	{
		boolean ret = false;
		
		if(virtuals!=null)
		{
			for(int i=0; i<virtuals.length && !ret; i++)
			{
				Set<String> names = virtualsmap.get(virtuals[i]);
				if(names!=null)
				{
					ret = names.contains(name);
				}
			}
		}
		
		return ret? new Future<Void>((Void)null): new Future<Void>(new SecurityException("Name not mapped by virtual names: "+name));
	}
	
	/**
	 *  Add a name to the mappings of a virtual name.
	 *  @param virtual The virtual name.
	 *  @param name The name to add.
	 */
	public IFuture<Void> addVirtual(String virtual, String name)
	{
		Future<Void> ret = new Future<Void>();
		
		Set<String> names = virtualsmap.get(virtual);
		if(names==null)
		{
			names	= new HashSet<String>();
			virtualsmap.put(virtual, names);
		}
		if(name!=null)
		{
			if(!names.add(name))
			{
				ret.setException(new RuntimeException("Virtual name already assigned."));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Remove a name from the mappings of a virtual name.
	 *  @param virtual The virtual name.
	 *  @param name The name to remove.
	 */
	public IFuture<Void> removeVirtual(String virtual, String name)
	{
		Set<String> names = virtualsmap.get(virtual);
		if(names!=null)
		{
			if(name==null)
			{
				virtualsmap.remove(virtual);
			}
			else
			{
				names.remove(name);
			}
//			if(names.isEmpty())
//			{
//				virtualsmap.remove(virtual);
//			}
		}

		return IFuture.DONE;		
	}
	
	/**
	 *  Get the virtual names and their contents.
	 *  @return The map of virtual names and their platform mappings.
	 */
	@SecureTransmission
	public IFuture<Map<String, Set<String>>> getVirtuals()
	{
		return new Future<Map<String, Set<String>>>(virtualsmap);
	}

	
	/**
	 *  Verify an authenticated service call.
	 *  @param content The content that should be checked.
	 *  @param signed The desired output hash.
	 *  @param name The callers name (used to find the certificate and public key). 
	 */
	public IFuture<Void> verifyCall(final byte[] content, final byte[] signed, final String name)
	{
		final Future<Void> ret = new Future<Void>();
		
		getCertificate(name).addResultListener(new ExceptionDelegationResultListener<Certificate, Void>(ret)
		{
			public void customResultAvailable(Certificate cert)
			{
				try
				{
					if(verifyCall(content, signed, cert))
					{
						ret.setResult(null);
					}
					else
					{
						ret.setException(new SecurityException("Authentication exception."));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ret.setException(e);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the certificate of a platform.
	 *  @param cid The platform component identifier (null for own certificate).
	 *  @return The certificate.
	 */
	public IFuture<Certificate> getPlatformCertificate(IComponentIdentifier cid)
	{
		return getCertificate(cid==null? null: cid.getPlatformPrefix());
	}
	
	/**
	 *  Get info about the current keystore that is used.
	 */
	public IFuture<Map<String, KeyStoreEntry>> getKeystoreDetails()
	{
		Future<Map<String, KeyStoreEntry>> ret = new Future<Map<String, KeyStoreEntry>>();
		Map<String, KeyStoreEntry> res = new HashMap<String, KeyStoreEntry>();
		
		try
		{
			KeyStore ks = getKeyStore();
			Enumeration<String> en = ks.aliases();
			while(en.hasMoreElements())
			{
				String alias = en.nextElement();
			
				KeyStoreEntry kse = new KeyStoreEntry();
				
				try
				{
					ks.getEntry(alias, null);
				}
				catch(Exception e)
				{
					kse.setProtected(true);
				}
				
				if(ks.isCertificateEntry(alias))
				{
					kse.setType(ISecurityService.CERTIFICATE);
				}
				else if(!ks.isKeyEntry(alias) && ks.getCertificateChain(alias)!=null 
					&& ks.getCertificateChain(alias).length!=0)
				{
					kse.setType(ISecurityService.TRUSTED_CERTIFICATE);
				}
				else 
				{
					kse.setType(ISecurityService.KEYPAIR);
				}
				
				Certificate cert = ks.getCertificate(alias);
				if(cert instanceof X509Certificate)
				{
					X509Certificate xcert = (X509Certificate)cert;
//					kse.setAlgorithm(xcert.getSigAlgName());
					Date from = xcert.getNotBefore();
					kse.setFrom(from.getTime());
					Date to = xcert.getNotAfter();
					kse.setTo(to.getTime());
				}
				
				if(cert!=null)
				{
					kse.setAlgorithm(cert.getPublicKey().getAlgorithm()+" "+SSecurity.getKeyLength(cert.getPublicKey()));
					Certificate[] chain = ks.getCertificateChain(alias);
					kse.setCertificates(chain!=null? chain: new Certificate[]{cert});
				}
				
				kse.setAlias(alias);
				kse.setDate(ks.getCreationDate(alias).getTime());
				res.put(alias, kse);
				
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		ret.setResult(res);
		
		return ret;
	}
	
	/**
	 *  Remove a key store entry.
	 *  @param String alias The alias name.
	 */
	public IFuture<Void> removeKeyStoreEntry(String alias)
	{
		try
		{
			KeyStore ks = getKeyStore();
			ks.deleteEntry(alias);
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
			saveKeyStore();
			return IFuture.DONE;
		}
		catch(Exception e)
		{
			return new Future<Void>(e);
		}
	}
	
	/**
	 *  Internal verify method that just checks if f-pubkey(content)=signed.
	 */
	public boolean verifyCall(byte[] content, byte[] signed, Certificate cert)
	{
		boolean ret = false;
		
		try
		{
			Signature eng = Signature.getInstance(SSecurity.getAlgorithm(cert));
			ret = SSecurity.verifyContent(cert.getPublicKey(), eng, content, signed);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Add a trusted certificate of a platform.
	 *  @param name The entry name.
	 *  @param cert The certificate.
	 */
	public IFuture<Void> addPlatformCertificate(IComponentIdentifier cid, Certificate cert)
	{
		try
		{
			KeyStore ks = getKeyStore();
			ks.setCertificateEntry(cid.getPlatformPrefix(), cert);
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
//			publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, getKeystoreDetails()));
			saveKeyStore();
			return IFuture.DONE;
		}
		catch(Exception e)
		{
			return new Future<Void>(e);
		}
	}
	
	/**
	 *  Get a certificate with an alias name.
	 *  Uses a mechanism to acquire a certificate.
	 *  @param name The alias name.
	 *  @return The certificate.
	 */
	protected IFuture<Certificate> getCertificate(final String name)
	{
		final Future<Certificate> ret = new Future<Certificate>();
			
		try
		{
			Certificate cert = null;
			
			// null can be used for own platform name
			String prefix = component.getComponentIdentifier().getPlatformPrefix();
			if(name==null || prefix.equals(name))
			{
				cert = getKeyStore().getCertificate(prefix);
				ret.setResult(cert); // should never be null
			}
			else
			{
				cert = getKeyStore().getCertificate(name);
				if(cert!=null)
				{
					ret.setResult(cert);
				}
				else
				{
					acquireCertificate(name).addResultListener(new DelegationResultListener<Certificate>(ret)
					{
						public void customResultAvailable(Certificate cert)
						{
							try
							{
								// Store certificate in store
								getKeyStore().setCertificateEntry(name, cert);
								publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
								saveKeyStore();
								super.customResultAvailable(cert);
							}
							catch(Exception e)
							{
								super.exceptionOccurred(e);
							}
						}
					});
				}
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Create a key pair entry (with associated certificate).
	 *  @param cid The entry name.
	 *  @param algorithm The algorithm.
	 *  @param keysize The key size (in bits).
	 */
	public IFuture<Void> createKeyPair(IComponentIdentifier cid, String algorithm, int keysize, String pass, int validity)
	{
		try
		{
			KeyPair keys = SSecurity.generateKeyPair(algorithm, keysize);
			Certificate cert = SSecurity.generateCertificate(keys, validity, "MD5With"+algorithm);
			getKeyStore().setKeyEntry(cid.getPlatformPrefix(), keys.getPrivate(), pass==null? null: pass.toCharArray(), new Certificate[]{cert});
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
			saveKeyStore();
			return IFuture.DONE;
		}
		catch(Exception e)
		{
			return new Future<Void>(e);
		}
	}

	/**
	 *  Delegates an acquire certificate call to the selected
	 *  (or no) mechanism.
	 */
	protected IFuture<Certificate> acquireCertificate(final String name)
	{
		if(selmech>-1)
		{
			return mechanisms.get(selmech).acquireCertificate(name);
		}
		else
		{
			return new Future<Certificate>(new SecurityException("No certificate and aquiring disabled."));
		}
	}
	
	
	
	/**
	 *  Get the component.
	 *  @return The component.
	 */
	public IInternalAccess getComponent()
	{
		return component;
	}
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Set a mechanism parameter.
	 */
	public IFuture<Void> setAcquisitionMechanismParameterValue(Class<?> type, String name, Object value)
	{
		Future<Void> ret = new Future<Void>();
		AAcquisitionMechanism mech = getMechanism(type);
		if(mech!=null)
		{
			mech.setParameterValue(name, value);
			ret.setResult(null);
		}
		else
		{
			ret.setException(new RuntimeException("Mechanism not found: "+type));
		}
		return ret;
	}
	
	/**
	 *  Get the supported certificate acquire mechanism infos.
	 */
	public IFuture<List<MechanismInfo>> getAcquisitionMechanisms()
	{
		List<MechanismInfo> ret = new ArrayList<MechanismInfo>();
		
		for(AAcquisitionMechanism mech: mechanisms)
		{
			ret.add(mech.getMechanismInfo());
		}
		
		return new Future<List<MechanismInfo>>(ret);
	}
	
	/**
	 *  Get the active acquisition mechanism.
	 */
	public IFuture<Integer> getSelectedAcquisitionMechanism()
	{
		return new Future<Integer>(selmech);
	}
	
	/**
	 *  Set the acquire mechanism.
	 */
	public IFuture<Void> setAcquisitionMechanism(Class<?> type)
	{
		int oldselmech = selmech;
		
		if(type==null)
		{
			selmech = -1;
		}
		else
		{
			for(int i=0; i<mechanisms.size(); i++)
			{
				AAcquisitionMechanism mech = mechanisms.get(i);
				if(mech.getClass().equals(type))
				{
					selmech = i;
					break;
				}
			}
		}
		
		if(oldselmech!=selmech)
			publishEvent(new ChangeEvent<Object>(null, PROPERTY_SELECTEDMECHANISM, Integer.valueOf(selmech)));
		
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to changes.
	 */
	public ISubscriptionIntermediateFuture<ChangeEvent<Object>> subscribeToEvents()
	{
//		final SubscriptionIntermediateFuture<ChangeEvent<Object>> ret = new SubscriptionIntermediateFuture<ChangeEvent<Object>>();
		final SubscriptionIntermediateFuture<ChangeEvent<Object>> ret	= (SubscriptionIntermediateFuture<ChangeEvent<Object>>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, component);

		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscribers.remove(ret);
			}
		});
		subscribers.add(ret);

		// signal with null subscription done
		ret.addIntermediateResultIfUndone(null);
		
		// Signal current state
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_KEYSTORESETTINGS, new String[]{storepath, storepass, keypass}));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_PLATFORMPASS, platformpasses));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_NETWORKPASS, networkpasses));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_LOCALPASS, password));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_USEPASS, usepass? Boolean.TRUE: Boolean.FALSE));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_TRUSTEDLAN, trustedlan? Boolean.TRUE: Boolean.FALSE));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_SELECTEDMECHANISM, selmech));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_VALIDITYDURATION, Long.valueOf(valdur)));
		ret.addIntermediateResultIfUndone(new ChangeEvent<Object>(null, PROPERTY_VIRTUALS, virtualsmap));

//		System.out.println("ret fut is: "+ret+" "+ret.hashCode());
		
		return ret;
	}
	
	/**
	 *  Get the mechanism for a type.
	 *  @param type The type.
	 *  @return The mechanism.
	 */
	protected AAcquisitionMechanism getMechanism(Class<?> type)
	{
		AAcquisitionMechanism ret = null;
		for(AAcquisitionMechanism mech: mechanisms)
		{
			if(mech.getClass().equals(type))
			{
				ret = mech;
				break;
			}
		}
		if(ret==null)
			throw new RuntimeException("Mechanism not found: "+type);
		return ret;
	}
	
	/**
	 *  Publish events to all subscribers.
	 */
	protected void publishEvent(ChangeEvent<Object> event)
	{
		for(SubscriptionIntermediateFuture<ChangeEvent<Object>> sub: subscribers)
		{
			sub.addIntermediateResult(event);
		}
	}
	
	/**
	 *  Save the keystore.
	 */
	protected void saveKeyStore()
	{
		if(keystore!=null)
		{
			SSecurity.saveKeystore(keystore, storepath, storepass);
		}
	}
	
	/**
	 *  Publish the current state.
	 */
	protected void publishCurrentState()
	{
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTORESETTINGS, new String[]{storepath, storepass, keypass}));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_KEYSTOREENTRIES, null));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_PLATFORMPASS, platformpasses));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_NETWORKPASS, networkpasses));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_LOCALPASS, password));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_USEPASS, usepass? Boolean.TRUE: Boolean.FALSE));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_TRUSTEDLAN, trustedlan? Boolean.TRUE: Boolean.FALSE));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_SELECTEDMECHANISM, selmech));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_VALIDITYDURATION, Long.valueOf(valdur)));
		publishEvent(new ChangeEvent<Object>(null, PROPERTY_VIRTUALS, virtualsmap));
	}
	
	//-------- static part --------

	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] buildDigest(long timestamp, String secret)
	{
//		System.out.println("build digest: "+timestamp+" "+secret);
		byte[]	input	= (byte[])SUtil.joinArrays(secret.getBytes(), SUtil.longToBytes(timestamp));
		return buildDigest(input);
	}
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] buildDigest(byte[] input)
	{
//		System.out.println("build digest: "+timestamp+" "+secret);
		try
		{
			MessageDigest	md	= MessageDigest.getInstance("SHA-384");
			byte[]	output	= md.digest(input);
			return output;
		}
		catch(NoSuchAlgorithmException e)
		{
			// Shouldn't happen?
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String args[]) throws Exception
	{
//		long	timestamp	= System.currentTimeMillis();
////		System.out.println("ts1: "+SUtil.arrayToString(SUtil.longToBytes(timestamp)));
//		
//		timestamp = timestamp>>>16<<16; // New digest every minute
//		long ts2 = timestamp>>>16;
//		long ts = timestamp;
//		for(int i=0; i<16; i++)
//		{
//			ts >>>= 1;
//		}
//		
//		// Test performance of algorithms
//		// See http://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigest
//		String[]	names	= new String[]
//		{
//			"MD2",
//			"MD5",
//			"SHA-1",
//			"SHA-256",
//			"SHA-384",
//			"SHA-512"
//		};
		
//		String	pw	= "platformpass";
//		IComponentIdentifier	cid	= new ComponentIdentifier("platform_xyz", new String[]{"hasfgjdlah", "t4qohnc37rtcb0q479tfb", "3t7qh90c3tq0dch9347qgbz0234", "w34q256vz348956qfhz03489fh6c"});
//		byte[]	cidbytes	= JavaWriter.objectToByteArray(cid, AbstractRemoteCommand.class.getClassLoader());
//		for(int runs=0; runs<3; runs++)
//		{
//			for(int i=0; i<names.length; i++)
//			{
//				MessageDigest	md	= MessageDigest.getInstance(names[i]);
//				long	start	= System.nanoTime();
//				for(long time=0; time<100000; time++)
//				{
//					byte[]	input	= (byte[])SUtil.joinArrays(pw.getBytes(), SUtil.longToBytes(time));
////					input	= (byte[])SUtil.joinArrays(input, JavaWriter.objectToByteArray(cid, AbstractRemoteCommand.class.getClassLoader()));
//					input	= (byte[])SUtil.joinArrays(input, cidbytes);
//					byte[]	output	= md.digest(input);
//					if(runs==0 && (time==0 || time==1))
//					{
//						System.out.println("digest ("+names[i]+"): "+new String(Base64.encode(output)));
//					}
//				}
//				if(runs==2)
//				{
//					System.out.println(names[i]+" took: "+((System.nanoTime()-start)/100000000)/10.0+" ms per 1000 messages");
//				}
//			}
//		}
	}
	
//	public static void main(String[] args)
//	{
//		Date d1 = getDate(2012, 1, 14, 2, 24);
//		Date d2 = getDate(2012, 2, 6, 0, 0);
//		System.out.println(SUtil.SDF.format(d1)+" - "+SUtil.SDF.format(d2));
//		long delta = d2.getTime()-d1.getTime();
//		double days = delta/1000.0/60/60/24;
//		System.out.println(days);
//	}
//	
//	public static Date getDate(int y, int m, int d, int h, int min)
//	{
//		GregorianCalendar gc  = new GregorianCalendar();
//		gc.set(Calendar.YEAR, y);
//		gc.set(Calendar.MONTH, m);
//		gc.set(Calendar.DAY_OF_MONTH, d);
//		gc.set(Calendar.HOUR, h);
//		gc.set(Calendar.MINUTE, min);
//		return gc.getTime();
//	}
}
