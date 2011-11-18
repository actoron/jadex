package jadex.base.service.security;

import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.security.IAuthorizable;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.Base64;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
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
	
	/** The local password (if any). */
	protected String	password;
	
	/** The stored passwords. */
	protected Map<String, String>	passwords;
	
	//-------- setup --------
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void>	start()
	{
		final Future<Void>	ret	= new Future<Void>();
		this.passwords	= new LinkedHashMap<String, String>();
		
		component.getServiceContainer().searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ISettingsService, Void>(ret)
		{
			public void customResultAvailable(final ISettingsService settings)
			{
				settings.getProperties(PROEPRTIES_ID)
					.addResultListener(new ExceptionDelegationResultListener<Properties, Void>(ret)
				{
					public void customResultAvailable(final Properties props)
					{
						if(props==null)
						{
							// generate new password, if no security settings exist, yet.
							password	= UUID.randomUUID().toString().substring(0, 8);
						}
						
						final IExternalAccess	access	= component.getExternalAccess();
						
						settings.registerPropertiesProvider(PROEPRTIES_ID, new IPropertiesProvider()
						{
							public IFuture<Void> setProperties(final Properties props)
							{
								return access.scheduleImmediate(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										password	= props.getStringProperty("password");
										if(props.getStringProperty("passwords")!=null)
										{
											passwords	= JavaReader.objectFromXML(props.getStringProperty("passwords"), ia.getClassLoader());
										}
										else
										{
											passwords	= new LinkedHashMap<String, String>();
										}
										return IFuture.DONE;
									}
								});
							}
							
							public IFuture<Properties> getProperties()
							{
								return access.scheduleImmediate(new IComponentStep<Properties>()
								{
									public IFuture<Properties> execute(IInternalAccess ia)
									{
										Properties	ret	= new Properties();
										ret.addProperty(new Property("password", password));
										ret.addProperty(new Property("passwords", JavaWriter.objectToXML(passwords, ia.getClassLoader())));
										return new Future<Properties>(ret);
									}
								});
							}
						}).addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								// If new password was generated, save settings such that new platform instances use it.
								if(props==null)
								{
									settings.saveProperties().addResultListener(new DelegationResultListener<Void>(ret));
								}
								else
								{
									super.customResultAvailable(result);
								}
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	//-------- password management --------
	
	/**
	 *  Get the local password.
	 *  @return	The password of the local platform (if any).
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getLocalPassword()
	{
		return new Future<String>(password);
	}

	/**
	 *  Set the local password.
	 *  If the password is set to null, acces is granted to all requests.
	 *  @param password	The password of the local platform (if any). 
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setLocalPassword(String password)
	{
		this.password	= password;
		return IFuture.DONE;
	}

	/**
	 *  Get the password for a target component.
	 *  @param target	The id of the target component.
	 *  @return	The stored password. Returns null if no password is stored, unless the
	 *    component is a local component in which case the local password (if any) is returned.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<String>	getTargetPassword(IComponentIdentifier target)
	{
		return new Future<String>(passwords.get(getStrippedPlatformName(target)));
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
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setTargetPassword(IComponentIdentifier target, String password)
	{
		if(password!=null)
		{
			passwords.put(getStrippedPlatformName(target), password);
		}
		else
		{
			// Use remove to avoid keeping old mappings forever (name would still be stored otherwise)
			passwords.remove(getStrippedPlatformName(target));
		}
		return IFuture.DONE;		
	}
	
	/**
	 *  Get all stored passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	public IFuture<Map<String, String>>	getStoredPasswords()
	{
		return new Future<Map<String, String>>(passwords);
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
		if(password!=null)
		{
			if(request.getAuthenticationData()!=null)
			{
				if(Math.abs(request.getTimestamp()-System.currentTimeMillis())<3000000)	// Todo: make freshness period configurable.
				{
					if(!Arrays.equals(request.getAuthenticationData(), buildDigest(request.getTimestamp(), password)))
					{
						error	= "Password incorrect.";
					}
				}
				else
				{
					error	= "Timestamp too old.";
				}
			}
			else
			{
				error	= "Password required.";
			}
		}
		
		IFuture<Void>	ret	= error==null ? new Future<Void>((Void)null) : new Future<Void>(new SecurityException(error));
		try
		{
			ret.get(null);
			System.out.println("Request valid: "+request);
		}
		catch(Exception e)
		{
			System.out.println("Request invalid: "+request+", "+e);			
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
		String	stripped	= getStrippedPlatformName(target);
		// Use stored password or local password for local targets.  
		String	pw	= passwords.containsKey(stripped) ? passwords.get(stripped)
			: stripped.equals(getStrippedPlatformName(component.getComponentIdentifier())) ? password : null;
		
		if(pw!=null)
		{
			long	timestamp	= System.currentTimeMillis();
			byte[]	authdata	= buildDigest(timestamp, pw);
			request.setTimestamp(timestamp);
			request.setAuthenticationData(authdata);
		}
		
		return IFuture.DONE;
	}

	//-------- static part --------
	
	/**
	 *  Get the stripped platform name.
	 *  @param cid	The component identifier.
	 *  @return The platform name without auto-generated suffix.
	 */
	public static String	getStrippedPlatformName(IComponentIdentifier cid)
	{		
		// Strip auto-generated platform suffix (hack???).
		// cf. Starter and SecurityService
		String	ret	= cid.getPlatformName();
		if(ret.indexOf('_')!=-1)
		{
			ret	= ret.substring(0, ret.lastIndexOf('_'));
		}
		return ret;
	}
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[]	buildDigest(long timestamp, String password)
	{
		try
		{
			MessageDigest	md	= MessageDigest.getInstance("MD5");
			byte[]	input	= (byte[])SUtil.joinArrays(password.getBytes(), SUtil.longToBytes(timestamp));
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
		// Test performance of algorithms
		// See http://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigesthttp://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigest
		String[]	names	= new String[]
		{
			"MD2",
			"MD5",
			"SHA-1",
			"SHA-256",
			"SHA-384",
			"SHA-512"
		};
		
		String	pw	= "platformpass";
		IComponentIdentifier	cid	= new ComponentIdentifier("platform_xyz", new String[]{"hasfgjdlah", "t4qohnc37rtcb0q479tfb", "3t7qh90c3tq0dch9347qgbz0234", "w34q256vz348956qfhz03489fh6c"});
		byte[]	cidbytes	= JavaWriter.objectToByteArray(cid, AbstractRemoteCommand.class.getClassLoader());
		for(int runs=0; runs<3; runs++)
		{
			for(int i=0; i<names.length; i++)
			{
				MessageDigest	md	= MessageDigest.getInstance(names[i]);
				long	start	= System.nanoTime();
				for(long time=0; time<100000; time++)
				{
					byte[]	input	= (byte[])SUtil.joinArrays(pw.getBytes(), SUtil.longToBytes(time));
//					input	= (byte[])SUtil.joinArrays(input, JavaWriter.objectToByteArray(cid, AbstractRemoteCommand.class.getClassLoader()));
					input	= (byte[])SUtil.joinArrays(input, cidbytes);
					byte[]	output	= md.digest(input);
					if(runs==0 && (time==0 || time==1))
					{
						System.out.println("digest ("+names[i]+"): "+new String(Base64.encode(output)));
					}
				}
				if(runs==2)
				{
					System.out.println(names[i]+" took: "+((System.nanoTime()-start)/100000000)/10.0+" ms per 1000 messages");
				}
			}
		}
	}
}
