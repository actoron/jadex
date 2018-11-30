package jadex.platform.service.security;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IUntrustedMessageHandler;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.IRwMap;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.RwMapWrapper;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.security.SSecurity;
import jadex.commons.transformation.traverser.SCloner;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
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
@Agent(autostart=Boolean3.TRUE,
	predecessors="jadex.platform.service.clock.ClockAgent")
@Arguments(value={
	@Argument(name="usesecret", clazz=Boolean.class, defaultvalue="null"),
	@Argument(name="printsecret", clazz=Boolean.class, defaultvalue="null"),
	@Argument(name="refuseunauth", clazz=Boolean.class, defaultvalue="null"),
	@Argument(name="platformsecret", clazz=String[].class, defaultvalue="null"),
	@Argument(name="networknames", clazz=String[].class, defaultvalue="null"),
	@Argument(name="networksecrets", clazz=String[].class, defaultvalue="null"),
	@Argument(name="roles", clazz=String.class, defaultvalue="null")
})
//@Service // This causes problems because the wrong preprocessor is used (for pojo services instead of remote references)!!!
@ProvidedServices(@ProvidedService(type=ISecurityService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)))
@Properties(value=@NameValue(name="system", value="true"))
public class SecurityAgent implements ISecurityService, IInternalService
{
	/** Properties id for the settings service. */
	public static final String	PROPERTIES_ID	= "securityservice";
	
	/** Header property for security messages. */
	protected static final String SECURITY_MESSAGE = "__securitymessage__";
	
	/** Name of the global network. */
	public static final String GLOBAL_NETWORK_NAME = "___GLOBAL___";
	
	/** Default root certificate for global network. */
	public static final String DEFAULT_GLOBAL_ROOT_CERTIFICATE = "pem:-----BEGIN CERTIFICATE-----|MIICszCCAhWgAwIBAgIVAP5jQirZLKNnSHf1FES8qkWMJyvKMAoGCCqGSM49BAME|MDYxHTAbBgNVBAMMFEphZGV4IEdsb2JhbCBSb290IFgxMRUwEwYDVQQKDAxBY3Rv|cm9uIEdtYkgwHhcNMTgwODAxMDkxNjA5WhcNMjgwNzI5MDkxNjA5WjA2MR0wGwYD|VQQDDBRKYWRleCBHbG9iYWwgUm9vdCBYMTEVMBMGA1UECgwMQWN0b3JvbiBHbWJI|MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQA6K9sA0U88s0/6nLTwZhXwzBesBr/|MpNAqpZtCBe2sD+3sjppYtnug3RUbRFYNZsYPMMHBqOWyo0BR7N5DxeSJ8AB/T/z|zTC9PqjDUcIazUDCf0XsSSx08a3UqBPZ5EzKRtOvf3cx/qCp/0/fND3iKWfrNhng|LxYMS0d/BMlNRE3vQl6jgbwwgbkwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8E|BAMCAoQwSQYDVR0OBEIEQLAcDiIifZpM0BihTvohWfxP5bHk3iHeA/O5vLaTp7o5|Lw+2E2CcyIXfNcMRhQ5lAymDVYBwJjr0ZjgzvXOsJhIwSwYDVR0jBEQwQoBAsBwO|IiJ9mkzQGKFO+iFZ/E/lseTeId4D87m8tpOnujkvD7YTYJzIhd81wxGFDmUDKYNV|gHAmOvRmODO9c6wmEjAKBggqhkjOPQQDBAOBiwAwgYcCQgGYPCBbcI/ai9nAqzuU|1oXIn4KFguj/95xbVm4HBb9wsNrB0K8LtdXsvB4BR2HeRCB0cWqyCKZimBbaJIoD|BTcs2gJBTXfqb/KlKCwrO6KXLOtah5sgASt+QZ3uD6AXBNrBfBjC5nUBWkx/zJd+|sllyYoekCGy/UAvwNIB4aFkTHnQGyS4=|-----END CERTIFICATE-----|";
	
	/** Timeout used for internal expirations */
//	protected static final long TIMEOUT = 60000;
	
	/** Component access. */
	@Agent
	protected IInternalAccess agent;
	
	@AgentFeature
	protected IExecutionFeature execfeat;
	
	/** Flag whether to use the platform secret for authentication. */
	@AgentArgument
	protected boolean usesecret = true;
	
	/** Flag whether the platform secret should be printed during start. */
	@AgentArgument
	protected boolean printsecret = true;
	
	/** 
	 *  Flag whether to grant default authorization
	 *  (allow basic service calls if name, network or platform is authenticated).
	 */
	@AgentArgument
	protected boolean defaultauthorization = true;
	
	/** Flag whether to refuse unauthenticated connections. */
	@AgentArgument
	protected boolean refuseunauth = false;
	
	/** Flag if connection with platforms without authenticated names are allowed. */
	@AgentArgument
	protected boolean allownoauthname = true;
	
	/** Flag if connection with platforms without authenticated networks are allowed. */
	@AgentArgument
	protected boolean allownonetwork = true;
	
	/** Flag whether to use the default Java trust store. */
	@AgentArgument
	protected boolean loadjavatruststore = true;
	
	/** Flag if the security should add a global network
	 *  if no global network is set.
	 */
	@AgentArgument
	protected boolean addglobalnetwork = true;
	
	/** Flag if the security should create a random default network
	 *  if no network is set.
	 */
	@AgentArgument
	protected boolean createdefaultnetwork = true;
	
	/** Handshake timeout. */
	@AgentArgument
	protected long handshaketimeout = -1;
	
	/** Handshake timeout scale factor. */
	@AgentArgument
	protected double handshaketimeoutscale = 2.0;
	
	/** Handshake reset scale factor. */
	@AgentArgument
	protected double resettimeoutscale = 0.02;
	
	/** Flag enabling debug printouts. */
	@AgentArgument
	protected boolean debug = false;
	
	/** Local platform authentication secret. */
	protected AbstractAuthenticationSecret platformsecret;
	
	/** Remote platform authentication secrets. */
	protected Map<IComponentIdentifier, AbstractAuthenticationSecret> remoteplatformsecrets = new HashMap<IComponentIdentifier, AbstractAuthenticationSecret>();;
	
	/** Flag whether to allow platforms to be associated with roles (clashes, spoofing problem?). */
//	protected boolean allowplatformroles = false;
	
	/** Available virtual networks. */
//	protected Map<String, AbstractAuthenticationSecret> networks = new HashMap<String, AbstractAuthenticationSecret>();
	protected MultiCollection<String, AbstractAuthenticationSecret> networks = new MultiCollection<>(new HashMap<>(), LinkedHashSet.class);
	
	/** The platform name certificate if available. */
	protected AbstractX509PemSecret platformnamecertificate;
	
	/** The platform names that are trusted and identified by name. */
	protected Set<String> trustedplatforms = new HashSet<>();
	
	/** Trusted authorities for certifying platform names. */
	protected Set<X509CertificateHolder> nameauthorities = new HashSet<>();
	
	/** Custom (non-Java default) trusted authorities for certifying platform names. */
	protected Set<X509CertificateHolder> customnameauthorities = new HashSet<>();
	
	/** Available crypt suites. */
	protected Map<String, Class<?>> allowedcryptosuites = new LinkedHashMap<String, Class<?>>();
	
	/** CryptoSuites currently initializing, value=Handshake state. */
	protected Map<String, HandshakeState> initializingcryptosuites = new HashMap<String, HandshakeState>();
	
	/** CryptoSuites currently in use. */
	protected IRwMap<String, ICryptoSuite> currentcryptosuites = new RwMapWrapper<>(new HashMap<String, ICryptoSuite>());
	
	/** CryptoSuites that are expiring with expiration time. */
	protected MultiCollection<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites = new MultiCollection<String, Tuple2<ICryptoSuite,Long>>();
//	protected Map<String, Tuple2<ICryptoSuite, Long>> expiringcryptosuites;
	
	/** Map of entities and associated roles. */
	protected Map<String, Set<String>> roles = new HashMap<String, Set<String>>();
	
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
		if (handshaketimeout < 0)
			handshaketimeout = (long) (Starter.getDefaultTimeout(agent.getId().getRoot()) * handshaketimeoutscale);
		if (handshaketimeout <= 0)
			handshaketimeout = 60000;
		final Future<Void> ret = new Future<Void>();
		
		loadSettings().addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
		{
			@SuppressWarnings("unchecked")
			public void customResultAvailable(Map<String, Object> settings)
			{
				boolean savesettings = false;
				Map<String, Object> args = agent.getFeature(IArgumentsResultsFeature.class).getArguments();
				for (Object val : args.values())
					savesettings |= val != null;
				
				usesecret = getProperty("usesecret", args, settings, usesecret);
				printsecret = getProperty("printsecret", args, settings, usesecret);
				refuseunauth = getProperty("refuseunauth", args, settings, refuseunauth);
				
				if (args.get("platformnamecertificate") != null)
					platformnamecertificate = (AbstractX509PemSecret) AbstractAuthenticationSecret.fromString((String) args.get("platformnamecertificate"), true);
				else
					platformnamecertificate = getProperty("platformnamecertificate", args, settings, platformnamecertificate);
				
				if (args.get("nameauthorities") != null)
				{
					nameauthorities = new HashSet<>();
					String authstr = (String) args.get("nameauthorities");
					String[] split = authstr.split(",");
					for (int i = 0; i < split.length; ++i)
					{
						if (split[i].length() > 0)
						{
							try
							{
								X509CertificateHolder cert = SSecurity.readCertificateFromPEM(split[i]);
								nameauthorities.add(cert);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				else
				{
					nameauthorities = getProperty("nameauthorities", args, settings, nameauthorities);
				}
				
				customnameauthorities.addAll(nameauthorities);
				
				if (loadjavatruststore)
				{
					String tst = System.getProperty("javax.net.ssl.trustStoreType");
					String tsf = System.getProperty("javax.net.ssl.trustStore");
					String tsp = System.getProperty("javax.net.ssl.trustStorePassword");
					
					if (tsf == null && tst == null)
					{
						String javahome = System.getProperty("java.home");
						Path path = Paths.get(javahome, "lib", "security", "jssecacerts");
			            if (!path.toFile().exists())
			            {
			            	path = Paths.get(javahome, "lib", "security", "cacerts");
			            }
						if (path.toFile().exists())
						{
							try
							{
								tsf = path.toFile().getCanonicalPath();
							}
							catch (IOException e)
							{
							}
						}
					}
					
					if (tsp == null)
						tsp = "changeit";
					if (tst == null)
						tst = KeyStore.getDefaultType();
					
					if (tst != null && tsf != null)
					{
						JcaPEMWriter jpw = null;
						try
						{
							KeyStore ks = KeyStore.getInstance(tst);
							InputStream is = null;
							try
							{
								is = new FileInputStream(tsf);
								is = new BufferedInputStream(is);
								ks.load(is, tsp.toCharArray());
								SUtil.close(is);
							}
							catch (Exception e)
							{
							}
							finally
							{
								SUtil.close(is);
							}
							
							Enumeration<String> aliases = ks.aliases();
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							OutputStreamWriter osw = new OutputStreamWriter(baos);
							jpw = new JcaPEMWriter(osw);
							while (aliases.hasMoreElements())
							{
								try
								{
									String alias = aliases.nextElement();
									Certificate cert = ks.getCertificate(alias);
									jpw.writeObject(cert);
//									SUtil.close(jpw);
//									SUtil.close(baos);
									jpw.flush();
									String pem = new String(baos.toByteArray(), SUtil.ASCII);
									baos.reset();
									try
									{
										nameauthorities.add(SSecurity.readCertificateFromPEM(pem));
									}
									catch (Exception e)
									{
									}
								}
								catch (Exception e)
								{
								}
							}
//							ts = System.currentTimeMillis() - ts;
//							System.out.println("READING TOOK " + ts);
						}
						catch (Exception e)
						{
						}
						finally
						{
							SUtil.close(jpw);
						}
					}
				}
				
				if (args.get("trustedplatforms") != null)
				{
					String authstr = (String) args.get("trustedplatforms");
					String[] split = authstr.split(",");
					for (int i = 0; i < split.length; ++i)
					{
						if (split[i].length() > 0)
						{
							trustedplatforms.add(split[i]);
						}
					}
				}
				else
				{
					trustedplatforms = getProperty("trustedplatforms", args, settings, trustedplatforms);
				}
				
				if (args.get("platformsecret") != null)
					platformsecret = AbstractAuthenticationSecret.fromString((String) args.get("platformsecret"), false);
				else
					platformsecret = getProperty("platformsecret", args, settings, platformsecret);
				
				String[] nn = (String[]) args.remove("networknames");
				String[] ns = (String[]) args.remove("networksecrets");
				if (args.get("networknames") != null || args.get("networksecrets") != null)
				{
					
					if (nn == null || ns == null || ns.length != nn.length)
					{
						agent.getLogger().warning("Network names and secrets do not match, ignoring...");
						nn = null;
						ns = null;
					}
				}
				if (nn != null)
				{
					for (int i = 0; i < nn.length; ++i)
						networks.add(nn[i], AbstractAuthenticationSecret.fromString(ns[i]));
				}
				else
				{
					networks = getProperty("networks", args, settings, networks);
				}
				
				File networksfile = new File("networks.cfg");
				if (networksfile.exists())
				{
					InputStream is = null;
					try
					{
						is = new FileInputStream(networksfile);
						is = new BufferedInputStream(is);
						java.util.Properties nwfileprops = new java.util.Properties();
						nwfileprops.load(is);
						SUtil.close(is);
						is = null;
						
						for (String propname : SUtil.notNull(nwfileprops.stringPropertyNames()))
						{
							String secretstr = nwfileprops.getProperty(propname);
							try
							{
								AbstractAuthenticationSecret secret = AbstractAuthenticationSecret.fromString(secretstr, true);
								networks.add(propname, secret);
							}
							catch (Exception e)
							{
							}
						}
					}
					catch (Exception e)
					{
					}
					finally
					{
						SUtil.close(is);
					}
				}
				
				if (addglobalnetwork && !networks.containsKey(GLOBAL_NETWORK_NAME))
					networks.add(GLOBAL_NETWORK_NAME, AbstractAuthenticationSecret.fromString(DEFAULT_GLOBAL_ROOT_CERTIFICATE, true));
				
				if ((networks.isEmpty() || (networks.size() == 1 && networks.containsKey(GLOBAL_NETWORK_NAME))) &&
					createdefaultnetwork)
				{
					networks.add(SUtil.createPlainRandomId("default_network", 6), KeySecret.createRandom());
					savesettings = true;
				}
				
				remoteplatformsecrets = getProperty("remoteplatformsecrets", args, settings, remoteplatformsecrets);
				roles = getProperty("roles", args, settings, roles);
				
				if (printsecret)
				{
					for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : networks.entrySet())
					{
						if (entry.getValue() != null && !GLOBAL_NETWORK_NAME.equals(entry.getKey()))
						{
							for (AbstractAuthenticationSecret secret : entry.getValue())
								System.out.println("Available network '" + entry.getKey() + "' with secret " + secret);
						}
					}
				}
				
				if (usesecret && platformsecret == null)
				{
					platformsecret = KeySecret.createRandom();
					savesettings = true;
//					System.out.println("Generated new platform access key: "+platformsecret.toString().substring(KeySecret.PREFIX.length() + 1));
				}
				
				if (printsecret && platformsecret != null)
				{
					String secretstr = platformsecret.toString();
					String pfname = agent.getId().getPlatformName();
					
					if (platformsecret instanceof PasswordSecret)
						System.out.println("Platform " + pfname + " access password: "+secretstr);
					else if (platformsecret instanceof KeySecret)
						System.out.println("Platform " + pfname + " access key: "+secretstr);
					else if (platformsecret instanceof AbstractX509PemSecret)
						System.out.println("Platform " + pfname + " access certificates: "+secretstr);
					else
						System.out.println("Platform " + pfname + " access secret: "+secretstr);
				}
				
				networknames = (Set<String>)Starter.getPlatformValue(agent.getId(), Starter.DATA_NETWORKNAMESCACHE);
//				networknames.addAll(networks.keySet());
				// Only add network names the platform is a member of (secret can sign).
				for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : networks.entrySet())
				{
					for (AbstractAuthenticationSecret secret : entry.getValue())
					{
						if (secret.canSign())
						{
							networknames.add(entry.getKey());
							break;
						}
					}
				}
				
				// TODO: Make configurable
				String[] cryptsuites = new String[] { NHCurve448ChaCha20Poly1305Suite.class.getCanonicalName() };
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
						ret.setException(e);
						return;
					}
				}
				
				if (savesettings)
					saveSettings();
				
				IMessageFeature msgfeat = agent.getFeature(IMessageFeature.class);
				msgfeat.addMessageHandler(new SecurityMessageHandler());
				msgfeat.addMessageHandler(new ReencryptRequestHandler());
				
				ret.setResult(null);
			}
		});
		
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// Warn about weak passwords.
				Map<PasswordSecret, String> pwsecrets = new HashMap<>();
				if (platformsecret instanceof PasswordSecret)
					pwsecrets.put((PasswordSecret) platformsecret, "local platform");
				
				for (Map.Entry<IComponentIdentifier, AbstractAuthenticationSecret> entry : remoteplatformsecrets.entrySet())
				{
					if (entry.getValue() instanceof PasswordSecret)
						pwsecrets.put((PasswordSecret) entry.getValue(), "for remote platform '" + entry.getKey().toString() + "'");
				}

				for (Map.Entry<String, Collection<AbstractAuthenticationSecret>> nwentry : networks.entrySet())
				{
					for (AbstractAuthenticationSecret secret : nwentry.getValue())
					{
						if (secret instanceof PasswordSecret)
							pwsecrets.put((PasswordSecret) secret, "network '" + nwentry.getKey() + "'");
					}
				}
				
				for (Map.Entry<PasswordSecret, String> entry : pwsecrets.entrySet())
				{
//					System.out.println("CHECKING " + secret + " " + secret.isWeak());
					if (entry.getKey().isWeak())
						agent.getLogger().severe(agent.getId().getName() + ": Weak password detected for " + entry.getValue() + ", password '" + entry.getKey().getPassword() + "' is too short, please use at least " + PasswordSecret.MIN_GOOD_PASSWORD_LENGTH + " random characters.");
				}
				
				// Reindex services since networks are now available.
				ServiceRegistry.getRegistry(agent.getId().getRoot()).updateService(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		return ret;
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
		
		return agent.scheduleStep(new IComponentStep<byte[]>()
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
						currentcryptosuites.getWriteLock().lock();
						try
						{
							if (cs.equals(currentcryptosuites.get(rplat)))
							{
								if (debug)
									System.out.println("Expiring: "+rplat);
								expireCryptosuite(rplat);
								cs = null;
							}
						}
						finally
						{
							currentcryptosuites.getWriteLock().unlock();
						}
					}
					
					if (cs != null)
					{
						ret.setResult(cs.encryptAndSign(content));
					}
					else
					{
						HandshakeState hstate = initializingcryptosuites.get(rplat);
						if (hstate == null)
						{
							initializeHandshake(rplat);
							hstate = initializingcryptosuites.get(rplat);
						}
						
						// Add sim blocker and print error msg when handshake doesn't work
						if(SSimulation.addBlocker(ret))
						{
							ia.waitForDelay(Starter.getScaledDefaultTimeout(ia.getId(), 0.5), true)
								.addResultListener(v ->
							{
								if(!ret.isDone())
								{
									System.out.println("Security handshake timeout from "+agent+" to "+rplat);
									checkCleanup();
									ret.setExceptionIfUndone(new TimeoutException("Security handshake timeout from "+agent+" to "+rplat));
								}
							});
						}
							
						hstate.getResultFuture().addResultListener(new ExceptionDelegationResultListener<ICryptoSuite, byte[]>(ret, true)
						{
							public void customResultAvailable(ICryptoSuite result) throws Exception
							{
								ret.setResultIfUndone(result.encryptAndSign(content));
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
	public IFuture<Tuple2<ISecurityInfo,byte[]>> decryptAndAuth(final IComponentIdentifier sender, final byte[] content)
	{
//		System.out.println("received: "+sender+" at "+agent.getId());
		
		checkCleanup();
		
		if (content == null || content.length == 0)
			return new Future<>(new IllegalArgumentException("Null messages and zero length messages cannot be decrypted."));
		
		String splat = sender.getRoot().toString();
		ICryptoSuite cs = currentcryptosuites.get(splat);
		if (cs != null && content.length > 0 && content[0] != -1)
		{
			byte[] cleartext = cs.decryptAndAuth(content);
			if (cleartext != null)
				return new Future<Tuple2<ISecurityInfo,byte[]>>(new Tuple2<ISecurityInfo, byte[]>(cs.getSecurityInfos(), cleartext));
		}
		
		return agent.scheduleStep(new IComponentStep<Tuple2<ISecurityInfo,byte[]>>()
		{
			public IFuture<Tuple2<ISecurityInfo, byte[]>> execute(IInternalAccess ia)
			{
				doCleanup();
				
				final Future<Tuple2<ISecurityInfo, byte[]>> ret = new Future<Tuple2<ISecurityInfo,byte[]>>();
				
				if (content.length > 0 && content[0] == -1)
				{
					// Security message
					byte[] newcontent = new byte[content.length - 1];
					System.arraycopy(content, 1, newcontent, 0, newcontent.length);
					SecurityInfo secinfos = new SecurityInfo();
					Tuple2<ISecurityInfo,byte[]> tup = new Tuple2<ISecurityInfo, byte[]>(secinfos, newcontent);
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
									if(cleartext != null)
									{
										ret.setResult(new Tuple2<ISecurityInfo, byte[]>(result.getSecurityInfos(), cleartext));
									}
									else
									{
										requestReencryption(splat, content).addResultListener(new IResultListener<byte[]>()
										{
											public void resultAvailable(byte[] result)
											{
												ICryptoSuite cs = currentcryptosuites.get(splat);
												if (cs != null)
													ret.setResult(new Tuple2<ISecurityInfo, byte[]>(cs.getSecurityInfos(), result));
												else
													ret.setException(new SecurityException("Could not establish secure communication with (case 1): " + splat.toString() + "  " + content));
											};
											
											public void exceptionOccurred(Exception exception)
											{
												ret.setException(exception);
											}
										});
//										Object reply = requestReencryption(splat, content);
//										if(reply == null)
//										{
//											ret.setException(new SecurityException("Could not establish secure communication with (case 1): " + splat.toString()));
//										}
//										else if (reply instanceof Exception)
//										{
//											ret.setException((Exception) reply);
//										}
//										else if (reply instanceof byte[])
//										{
//											cleartext = (byte[]) reply;
//											
//										}
//										else
//										{
//											ret.setException(new SecurityException("Unrecognized decryption request reply: " + reply));
//										}
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
							requestReencryption(splat, content).addResultListener(new IResultListener<byte[]>()
							{
								public void resultAvailable(byte[] result)
								{
									ICryptoSuite cs = currentcryptosuites.get(splat);
									if (cs != null)
										ret.setResult(new Tuple2<ISecurityInfo, byte[]>(cs.getSecurityInfos(), result));
									else
										ret.setException(new SecurityException("Could not establish secure communication with (case 2): " + splat.toString() + "  " + content));
								};
								
								public void exceptionOccurred(Exception exception)
								{
									ret.setException(exception);
								}
							});
//							Object reply = requestReencryption(splat, content);
//							if(reply == null)
//							{
//								ret.setException(new SecurityException("Could not establish secure communication with (case 2): " + splat.toString() + "  " + content));
//							}
//							else if (reply instanceof Exception)
//							{
//								ret.setException((Exception) reply);
//							}
//							else if (reply instanceof byte[])
//							{
//								cleartext = (byte[]) reply;
//								cs = currentcryptosuites.get(splat);
//							}
//							else
//							{
//								ret.setException(new SecurityException("Unrecognized decryption request reply: " + reply));
//							}
						}
					}
					
					if (cleartext != null)
					{
						ret.setResult(new Tuple2<ISecurityInfo, byte[]>(cs.getSecurityInfos(), cleartext));
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
		return agent.scheduleStep(new IComponentStep<Boolean>()
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
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				usesecret = useplatformsecret;
				saveSettings();
				return resetCryptoSuites();
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
		return agent.scheduleStep(new IComponentStep<Boolean>()
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
		return agent.scheduleStep(new IComponentStep<Void>()
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
	 *  @param secret The secret.
	 *  @return Null, when done.
	 */
	public IFuture<Void> setNetwork(final String networkname, final String secret)
	{
		if (secret == null)
			return new Future<>(new IllegalArgumentException("Secret is null."));
		
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				AbstractAuthenticationSecret asecret = AbstractAuthenticationSecret.fromString(secret);
				
				Collection<AbstractAuthenticationSecret> secrets = networks.get(networkname);
				if (secrets != null && secrets.contains(asecret))
					return IFuture.DONE;
				
				networks.add(networkname, asecret);
				if (asecret.canSign())
					networknames.add(networkname);
				
				saveSettings();
				
				return resetCryptoSuites();
			}
		});
	}
	
	/**
	 *  Remove a network.
	 * 
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove the network completely.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNetwork(String networkname, String secret)
	{
		return agent.scheduleStep(new IComponentStep<Void>()
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
					Collection<AbstractAuthenticationSecret> secrets = networks.get(networkname);
					secrets.remove(AbstractAuthenticationSecret.fromString(secret));
					if (secrets.isEmpty())
					{
						networks.remove(networkname);
						networknames.remove(networkname);
					}
					else
					{
						boolean removename = true;
						for (AbstractAuthenticationSecret secret : secrets)
						{
							if (secret.canSign())
							{
								removename = false;
								break;
							}
						}
						if (removename)
							networknames.remove(networkname);
					}
				}
				
				saveSettings();
				
				return resetCryptoSuites();
			}
		});
	}
	
	/**
	 *  Gets the current networks and secrets. 
	 *  
	 *  @return The current networks and secrets.
	 */
	public IFuture<MultiCollection<String, String>> getAllKnownNetworks()
	{
		return agent.scheduleStep(new IComponentStep<MultiCollection<String, String>>()
		{
			public IFuture<MultiCollection<String, String>> execute(IInternalAccess ia)
			{
				MultiCollection<String, String> ret = new MultiCollection<String, String>();
				
				for(Map.Entry<String, Collection<AbstractAuthenticationSecret>> entry : networks.entrySet())
				{
					for (AbstractAuthenticationSecret secret : entry.getValue())
						ret.add(entry.getKey(), secret.toString());
				}
				
				return new Future<MultiCollection<String,String>>(ret);
			}
		});
	}
	
	/** 
	 *  Adds an authority for authenticating platform names.
	 *  
	 *  @param pemcertificate The pem-encoded certificate.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addNameAuthority(String pemcertificate)
	{
//		final AbstractAuthenticationSecret asecret = AbstractAuthenticationSecret.fromString(secret);
//		if (!(asecret instanceof AbstractX509PemSecret))
//			return new Future<>(new IllegalArgumentException("Only X509 secrets allowed as name authorities"));
		final X509CertificateHolder cert = SSecurity.readCertificateFromPEM(pemcertificate);
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				nameauthorities.add(cert);
				customnameauthorities.add(cert);
				
				saveSettings();
				
				return IFuture.DONE;
			}
		});
	}
	
	/** 
	 *  Remvoes an authority for authenticating platform names.
	 *  
	 *  @param secret The secret, only X.509 secrets allowed.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNameAuthority(String pemcertificate)
	{
		final X509CertificateHolder cert = SSecurity.readCertificateFromPEM(pemcertificate);
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if (customnameauthorities.remove(cert))
					nameauthorities.remove(cert);
				
				saveSettings();
				
				return IFuture.DONE;
			}
		});
	}
	
	/** 
	 *  Adds an authority for authenticating platform names.
	 *  
	 *  @param secret The secret, only X.509 secrets allowed.
	 *  @return Null, when done.
	 */
	public IFuture<Set<String>> getNameAuthorities()
	{
		return agent.scheduleStep(new IComponentStep<Set<String>>()
		{
			public IFuture<Set<String>> execute(IInternalAccess ia)
			{
				Set<String> ret = new HashSet<>();
				for (X509CertificateHolder cert : SUtil.notNull(nameauthorities))
					ret.add(SSecurity.writeCertificateAsPEM(cert));
				return new Future<>(ret);
			}
		});
	}
	
	/** 
	 *  Gets all authorities not defined in the Java trust store for authenticating platform names.
	 *  
	 *  @return List of name authorities.
	 */
	public IFuture<Set<String>> getCustomNameAuthorities()
	{
		return agent.scheduleStep(new IComponentStep<Set<String>>()
		{
			public IFuture<Set<String>> execute(IInternalAccess ia)
			{
				Set<String> ret = new HashSet<>();
				for (X509CertificateHolder cert : SUtil.notNull(customnameauthorities))
					ret.add(SSecurity.writeCertificateAsPEM(cert));
				return new Future<>(ret);
			}
		});
	}
	
//	/**
//	 *  Gets the current network names. 
//	 *  @return The current networks names.
//	 */
//	public IFuture<String[]> getNetworkNames()
//	{
//		return agent.scheduleStep(new IComponentStep<String[]>()
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
		return agent.scheduleStep(new IComponentStep<Set<String>>()
		{
			public IFuture<Set<String>> execute(IInternalAccess ia)
			{
				return new Future<Set<String>>(new HashSet<>(networknames));
			}
		});
	}
	
	/** 
	 *  Adds a name of an authenticated platform to allow access.
	 *  
	 *  @param name The platform name, name must be authenticated with certificate.
	 *  @param roles The roles the platform should have, can be null or empty.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addTrustedPlatform(String name)
	{
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				trustedplatforms.add(name);
				
				saveSettings();
				
				return IFuture.DONE;
			}
		});
	}
	
	/** 
	 *  Adds a name of an authenticated platform to allow access.
	 *  
	 *  @param name The platform name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeTrustedPlatform(String name)
	{
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				trustedplatforms.remove(name);
				
				saveSettings();
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Gets the trusted platforms that are specified by names. 
	 *  @return The trusted platforms and their roles.
	 */
	public IFuture<Set<String>> getTrustedPlatforms()
	{
		return agent.scheduleStep(new IComponentStep<Set<String>>()
		{
			public IFuture<Set<String>> execute(IInternalAccess ia)
			{
				return new Future<>(new HashSet<>(trustedplatforms));
			}
		});
	}
	
	/**
	 *  Gets the current network names. 
	 *  @return The current networks names.
	 */
	@Excluded
	public Set<String> getNetworkNamesSync()
	{
		@SuppressWarnings("unchecked")
		Set<String> ret = Collections.EMPTY_SET;
		
		if(networknames!=null)
		{
			String[] nnames = networknames.toArray(new String[0]);
			ret = SUtil.arrayToSet(nnames);
		}	
		
		return ret;
	}
	
	/**
	 *  Gets the secret of a platform if available.
	 * 
	 *  @param cid ID of the platform.
	 *  @return Encoded secret or null.
	 */
	public IFuture<String> getPlatformSecret(final IComponentIdentifier cid)
	{
		return agent.scheduleStep(new IComponentStep<String>()
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
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// TODO: Refresh?
				if (secret == null)
				{
					if (cid == null || agent.getId().getRoot().equals(cid))
						platformsecret = null;
					else
						remoteplatformsecrets.remove(cid);
				}
				else
				{
					AbstractAuthenticationSecret authsec = AbstractAuthenticationSecret.fromString(secret);
					
					if (cid == null || agent.getId().getRoot().equals(cid))
						platformsecret = authsec;
					else
						remoteplatformsecrets.put(cid, authsec);
				}
				
				saveSettings();
				
				if (usesecret)
					return resetCryptoSuites();
				else
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
		return agent.scheduleStep(new IComponentStep<Void>()
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
				
				refreshCryptosuiteRoles();
				
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
		return agent.scheduleStep(new IComponentStep<Void>()
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
				
				refreshCryptosuiteRoles();
				
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
		return agent.scheduleStep(new IComponentStep<Map<String, Set<String>>>()
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
	public MultiCollection<String, AbstractAuthenticationSecret> getInternalNetworks()
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
		if (cid.equals(agent.getId().getRoot()))
			return getInternalPlatformSecret();
		return remoteplatformsecrets.get(cid.getRoot());
	}
	
	/**
	 *  Gets the name authorities.
	 */
	public Set<X509CertificateHolder> getInternalNameAuthorities()
	{
		return nameauthorities;
	}
	
	/**
	 *  Gets the trusted platform names.
	 */
	public Set<String> getInternalTrustedPlatforms()
	{
		return trustedplatforms;
	}
	
	/**
	 *  Get the platform name certificate.
	 */
	public AbstractX509PemSecret getInternalPlatformNameCertificate()
	{
		return platformnamecertificate;
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
	 *  Checks whether to allow unauthenticated connections.
	 *  
	 *  @return True, if used.
	 */
	public boolean getInternalRefuseUnauth()
	{
		return refuseunauth;
	}
	
	/**
	 *  Checks whether to allow connections without name authentication.
	 *  
	 *  @return True, if used.
	 */
	public boolean getInternalAllowNoAuthName()
	{
		return allownoauthname;
	}
	
	/**
	 *  Checks whether to allow connections without network authentication.
	 *  
	 *  @return True, if used.
	 */
	public boolean getInternalAllowNoNetwork()
	{
		return allownonetwork;
	}
	
	/**
	 *  Checks whether to allow the default authorization.
	 *  
	 *  @return True, if used.
	 */
	public boolean getInternalDefaultAuthorization()
	{
		return defaultauthorization;
	}
	
	/**
	 *  Sets the roles of a security info object.
	 *  @param secinf Security info.
	 */
	public void setSecInfoRoles(SecurityInfo secinf)
	{
		assert agent.isComponentThread();
		Set<String> siroles = new HashSet<String>();
		
		Set<String> platformroles = roles.get(secinf.getAuthenticatedPlatformName());
		if (platformroles != null)
			siroles.addAll(platformroles);
		else
			siroles.add(secinf.getAuthenticatedPlatformName());
		
		
		if (secinf.getNetworks() != null)
		{
			for (String network : secinf.getNetworks())
			{
				Set<String> r = roles.get(network);
				if (r != null)
					siroles.addAll(r);
				else
					siroles.add(network);
			}
		}
		secinf.setRoles(siroles);
	}
	
	/**
	 *  Get component ID.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return agent.getId();
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
					cleanuptask = agent.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							return ia.getFeature(IExecutionFeature.class).waitForDelay(handshaketimeout << 1, new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									doCleanup();
									cleanuptask = null;
									return IFuture.DONE;
								}
							}, true);
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
		assert agent.getFeature(IExecutionFeature.class).isComponentThread();
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
		
		String[] keys = expiringcryptosuites.keySet().toArray(new String[expiringcryptosuites.keySet().size()]);
		for (String pf : keys)
		{
			Collection<Tuple2<ICryptoSuite, Long>> coll = new ArrayList<Tuple2<ICryptoSuite, Long>>(expiringcryptosuites.get(pf));
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
	protected IFuture<Void> resetCryptoSuites()
	{
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if (cryptoreset == null)
				{
					long resetdelay = (long) (handshaketimeout * resettimeoutscale);
					cryptoreset = ia.getFeature(IExecutionFeature.class).waitForDelay(resetdelay, new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							List<String> pfnames = new ArrayList<>();
							currentcryptosuites.getWriteLock().lock();
							try
							{
								pfnames.addAll(currentcryptosuites.keySet());
								for (String pfname : pfnames)
									expireCryptosuite(pfname);
							}
							finally
							{
								currentcryptosuites.getWriteLock().unlock();
							}
							for (String pfname : pfnames)
								initializeHandshake(pfname);
							
							cryptoreset = null;
							
							if (debug)
								System.out.println("Cryptosuites reset.");
							
							return IFuture.DONE;
						}
					}, true);
				}
				return cryptoreset;
			}
		});
	}
	
	/**
	 *  Creates a crypto suite of a particular name.
	 * 
	 *  @param name Name of the suite.
	 *  @param convid Conversation ID of handshake.
	 *  @return The suite, null if not found.
	 */
	protected ICryptoSuite createCryptoSuite(String name, String convid)
	{
		ICryptoSuite ret = null;
		try
		{
			Class<?> clazz = allowedcryptosuites.get(name);
			if (clazz != null)
			{
				ret = (ICryptoSuite) clazz.newInstance();
				ret.setHandshakeId(convid);
			}
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Expires a cryptosuite.
	 * 
	 *  @param pfname Platform name.
	 */
	protected void expireCryptosuite(String pfname)
	{
		assert agent.isComponentThread();
		currentcryptosuites.getWriteLock().lock();
		try
		{
			ICryptoSuite cs = currentcryptosuites.get(pfname);
			if (cs != null)
			{
				expiringcryptosuites.add(pfname, new Tuple2<ICryptoSuite, Long>(cs, System.currentTimeMillis() + handshaketimeout));
				currentcryptosuites.remove(pfname);
			}
		}
		finally
		{
			currentcryptosuites.getWriteLock().unlock();
		}
	}
	
	/**
	 *  Refreshed crypto suite roles.
	 */
	protected void refreshCryptosuiteRoles()
	{
		assert agent.isComponentThread();
		currentcryptosuites.getWriteLock().lock();
		try
		{
			for (Map.Entry<String, ICryptoSuite> entry : currentcryptosuites.entrySet())
			{
				SecurityInfo secinfo = ((SecurityInfo) entry.getValue().getSecurityInfos());
				setSecInfoRoles(secinfo);
			}
			
			for (Map.Entry<String, HandshakeState> entry : initializingcryptosuites.entrySet())
			{
				HandshakeState state = entry.getValue();
				if (state != null)
				{
					ICryptoSuite suite = state.getCryptoSuite();
					if (suite != null)
					{
						SecurityInfo secinfo = (SecurityInfo) suite.getSecurityInfos();
						if (secinfo != null)
							setSecInfoRoles(secinfo);
					}
				}
			}
		}
		finally
		{
			currentcryptosuites.getWriteLock().unlock();
		}
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
//				exception.printStackTrace();
				if (debug)
					System.out.println("Failure send message to and removing suite for: "+receiver.getRoot().toString());
				
				HandshakeState state = initializingcryptosuites.remove(receiver.getRoot().toString());
				if(state != null)
				{
					state.getResultFuture().setException(new SecurityException("Could not reach " + receiver + " for handshake."));
//					{
//						@Override
//						public void printStackTrace()
//						{
//							super.printStackTrace();
//						}
//					});
				}
			}
			
			public void resultAvailable(Void result)
			{	
			}
		});
	}
	
	protected void initializeHandshake(String cid)
	{
		String convid = SUtil.createUniqueId(agent.getId().getRoot().toString());
		HandshakeState hstate = new HandshakeState();
		hstate.setExpirationTime(System.currentTimeMillis() + handshaketimeout);
		hstate.setConversationId(convid);
		hstate.setResultFuture(new Future<ICryptoSuite>());
		
		initializingcryptosuites.put(cid.toString(), hstate);
		
		String[] csuites = allowedcryptosuites.keySet().toArray(new String[allowedcryptosuites.size()]);
		InitialHandshakeMessage ihm = new InitialHandshakeMessage(agent.getId(), convid, csuites);
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
			ret = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISettingsService.class));
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Loads the settings.
	 */
	@SuppressWarnings("unchecked")
	protected IFuture<Map<String, Object>> loadSettings()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		getSettingsService().loadState(PROPERTIES_ID).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(result != null ? (Map<String, Object>) result : new HashMap<String, Object>());
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Saves the current settings.
	 */
	protected void saveSettings()
	{
		Map<String, Object> settings = new HashMap<String, Object>();
		
		settings.put("usesecret", usesecret);
		settings.put("printsecret", printsecret);
		settings.put("refuseunauth", refuseunauth);
		
		if (platformsecret != null)
			settings.put("platformsecret", platformsecret);
		if(networks != null && networks.size() > 0)
			settings.put("networks", networks);
		if (remoteplatformsecrets != null && remoteplatformsecrets.size() > 0)
			settings.put("remoteplatformsecrets", remoteplatformsecrets);
		if (roles != null && roles.size() > 0)
			settings.put("roles", roles);
		if (platformnamecertificate != null)
			settings.put("platformnamecertificate", platformnamecertificate);
		if (customnameauthorities != null && customnameauthorities.size() > 0)
			settings.put("nameauthorities", customnameauthorities);
		if (trustedplatforms != null && trustedplatforms.size() > 0)
			settings.put("trustedplatforms", trustedplatforms);
		
		getSettingsService().saveState(PROPERTIES_ID, settings);
		
		/*jadex.commons.Properties settings = new jadex.commons.Properties();
		
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
		getSettingsService().saveProperties().get();*/
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
		
		return agent.getFeature(IMessageFeature.class).sendMessage(message, addheader, receiver);
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
	
	/**
	 *  Request reencryption by source.
	 *  
	 *  @param source Source of the content.
	 *  @param content The encrypted content.
	 *  @return Reply of decryption request, may be exception.
	 */
	protected IFuture<byte[]> requestReencryption(String platformname, byte[] content)
	{
		if (debug)
			System.out.println("reencryption: "+platformname+" "+Arrays.hashCode(content) + " " + currentcryptosuites.get(platformname));
		expireCryptosuite(platformname);
		
//		Thread.dumpStack();
		
		ReencryptionRequest req = new ReencryptionRequest();
		req.setContent(content);
		
		Future<byte[]> ret = new Future<>();
		BasicComponentIdentifier source = new BasicComponentIdentifier("security@" + platformname);
		agent.getFeature(IMessageFeature.class).sendMessageAndWait(source, req)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				if (result instanceof byte[])
					ret.setResult((byte[]) result);
				else if (result instanceof Exception)
					exceptionOccurred((Exception) result);
				else
					ret.setException(new IllegalArgumentException("Received unknown reply: " + result));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		((IInternalExecutionFeature) execfeat).addSimulationBlocker(ret);
		return ret;
	}
	
	//-------- Message Handlers -------
	
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
		public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg)
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
		public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg)
		{
			if (msg instanceof InitialHandshakeMessage)
			{
				if(debug)
					System.out.println(agent+" initial handshake message: "+msg);
				
				final InitialHandshakeMessage imsg = (InitialHandshakeMessage) msg;
				IComponentIdentifier rplat = imsg.getSender().getRoot();
				
				final Future<ICryptoSuite> fut = new Future<ICryptoSuite>();
				
				HandshakeState state = initializingcryptosuites.get(rplat.toString());
				
				// Check if handshake is already happening. 
				if (state != null)
				{
					// Check if duplicate
					if (!state.getConversationId().equals(imsg.getConversationId()))
					{
						if (getComponentIdentifier().getRoot().toString().compareTo(rplat.toString()) < 0)
							fut.addResultListener(new DelegationResultListener<ICryptoSuite>(state.getResultFuture()));
						else
							return;
					}
					else
					{
						return;
					}
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
				state.setExpirationTime(System.currentTimeMillis() + handshaketimeout);
				
				ICryptoSuite oldcs = currentcryptosuites.get(rplat.toString());
				if (oldcs != null)
				{
					currentcryptosuites.getWriteLock().lock();
					try
					{
						if (oldcs.equals(currentcryptosuites.get(rplat.toString())))
						{
							// Test for duplicate.
							if (oldcs.getHandshakeId().equals(imsg.getConversationId()))
								return;
							
							if (debug)
								System.out.println("New handshake, removing existing suite: "+rplat);
							expireCryptosuite(rplat.toString());
						}
					}
					finally
					{
						currentcryptosuites.getWriteLock().unlock();
					}
				}
				
				initializingcryptosuites.put(rplat.toString(), state);
				
//				ICryptoSuite oldcs = currentcryptosuites_old.remove(rplat.toString());
//				if (oldcs != null)
//				{
//					System.out.println("Removing suite: "+rplat);
//					expiringcryptosuites.add(rplat.toString(), new Tuple2<ICryptoSuite, Long>(oldcs, System.currentTimeMillis() + timeout));
//				}
				
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
						ICryptoSuite suite = createCryptoSuite(rm.getChosenCryptoSuite(), convid);
						
						if (suite == null)
						{
							initializingcryptosuites.remove(rm.getSender().getRoot().toString());
							state.getResultFuture().setException(new SecurityException("Handshake with remote platform " + rm.getSender().getRoot().toString() + " failed."));
						}
						else
						{
							state.setCryptoSuite(suite);
							InitialHandshakeFinalMessage fm = new InitialHandshakeFinalMessage(agent.getId(), rm.getConversationId(), rm.getChosenCryptoSuite());
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
						ICryptoSuite suite = createCryptoSuite(fm.getChosenCryptoSuite(), convid);
						agent.getLogger().info("Suite: " + (suite != null?suite.getClass().toString():"null"));
						
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
								if (debug)
									System.out.println(agent.getId()+" finished handshake: " + fm.getSender());
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
							if (debug)
								System.out.println(agent.getId()+" finished handshake: " + secmsg.getSender() + " trusted:" + state.getCryptoSuite().getSecurityInfos().isTrustedPlatform());
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
	
	/**
	 *  Handler dealing with remote reencryption requests.
	 *
	 */
	protected class ReencryptRequestHandler implements IUntrustedMessageHandler
	{
		public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg)
		{
			return msg instanceof ReencryptionRequest;
		}
		
		public boolean isRemove()
		{
			return false;
		}
		
		public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg)
		{
			ReencryptionRequest req = (ReencryptionRequest) msg;
			String senderpf = ((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot().toString();
			
			Object ret = null;
			
			Collection<Tuple2<ICryptoSuite, Long>> expsuites = expiringcryptosuites.get(senderpf);
			if (expsuites != null && expsuites.size() > 0)
			{
//				String checkresults = "";
				for (Tuple2<ICryptoSuite, Long> expsuite : expsuites)
				{
					ISecurityInfo suiteinfos = expsuite.getFirstEntity().getSecurityInfos();
					
//					checkresults += ""+secinfos.isAdminPlatform()+" "+suiteinfos.isAdminPlatform()+" "+secinfos.isAdminPlatform()
//					+" msgtrust:"+secinfos.isTrustedPlatform()+" suitetrust:"+suiteinfos.isTrustedPlatform()+" "+secinfos.isTrustedPlatform()
//					+" "+secinfos.getAuthenticatedPlatformName()+" "+suiteinfos.getAuthenticatedPlatformName()
//					+" "+suiteinfos.getAuthenticatedPlatformName()+" "+secinfos.getAuthenticatedPlatformName()
//					+" "+Arrays.toString(secinfos.getNetworks().toArray())
//					+" "+Arrays.toString(suiteinfos.getNetworks().toArray())+"\n";
					
					if ((secinfos.isAdminPlatform() || !suiteinfos.isAdminPlatform()) &&
						(secinfos.isTrustedPlatform() || !suiteinfos.isTrustedPlatform()) && 
						SUtil.equals(secinfos.getAuthenticatedPlatformName(), suiteinfos.getAuthenticatedPlatformName()) || (suiteinfos.getAuthenticatedPlatformName() == null && secinfos.getAuthenticatedPlatformName() != null))
					{
						Set<String> msgnets = secinfos.getNetworks();
						if (msgnets.containsAll(suiteinfos.getNetworks()))
						{
							ret = expsuite.getFirstEntity().decryptAndAuthLocal(req.getContent());
							if (ret != null)
								break;
						}
					}
				}
				if (ret == null)
				{
					ret = new SecurityException("Found expired suites but none match required security criteria.");
//					ret = new SecurityException("Found " + expsuites.size() + " expired suites but none match required security criteria:\n" + checkresults);
				}
			}
			else
			{
				ret = new IllegalStateException("No expired suites found to decrypt message.");
			}
			
			agent.getFeature(IMessageFeature.class).sendReply(header, ret);
		}
	}
	
	//---- IInternalService stuff 
	
	private IServiceIdentifier sid;
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceId()
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
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	/**
	 *  Gets the right property from arguments, settings and default.
	 * 
	 *  @param property Property name.
	 *  @param args Arguments.
	 *  @param settings Settings.
	 *  @param defaultprop Default.
	 *  @return The property.
	 */
	@SuppressWarnings("unchecked")
	protected static final <T> T getProperty(String property, Map<String, Object> args, Map<String, Object> settings, T defaultprop)
	{
		T ret = defaultprop;
		if (args.get(property) != null)
			ret = (T) args.get(property);
		else if (settings.containsKey(property))
			ret = (T) settings.get(property);
		return ret;
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
