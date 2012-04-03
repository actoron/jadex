package jadex.base.service.security;

import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
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
import jadex.commons.future.IResultListener;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/* $if android $
import android.util.Log;
$endif $ */

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
	
	/** Flag to enable / disable password protection. */
	protected Boolean	usepass;
	
	/** Print password on startup or change. */
	protected boolean	printpass;
	
	/** The local password (if any). */
	protected String	password;
	
	/** The stored passwords. */
	protected Map<String, String>	passwords;
	
	//-------- setup --------
	
	/**
	 *  Create a security service.
	 */
	public SecurityService()
	{
		this(Boolean.TRUE, true);
	}
	
	/**
	 *  Create a security service.
	 */
	public SecurityService(Boolean usepass, boolean printpass)
	{
		this.usepass = usepass;
		this.printpass = printpass;
	}
	
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
						// generate new password, if no security settings exist, yet.
						final boolean	genpass	= props==null || props.getProperty("password")==null; 
						if(genpass)
						{
							password	= UUID.randomUUID().toString().substring(0, 12);
//							usepass	= true;
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
										if(usepass==null)
										{
											boolean up = props.getBooleanProperty("usepass");
											usepass	= up? Boolean.TRUE: Boolean.FALSE;
//											System.out.println("usepass: "+usepass);
										}
										password	= props.getStringProperty("password");
										if(props.getProperty("passwords")!=null)
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
										ret.addProperty(new Property("usepass", usepass==null? Boolean.TRUE.toString(): usepass.toString()));
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
								if(genpass)
								{
									if(printpass && (usepass==null || usepass.booleanValue()))
									{
										/* $if android $
										Log.i("jadex-android", "Generated platform password: "+password);
										$else $ */
										System.out.println("Generated platform password: "+password);
										/* $endif $ */
									}
									settings.saveProperties().addResultListener(new DelegationResultListener<Void>(ret));
								}
								else
								{
									if(printpass && (usepass==null || usepass.booleanValue()))
									{
										/* $if android $
										Log.i("jadex-android", "Using stored platform password: "+password);
										$else $ */
										System.out.println("Using stored platform password: "+password);
										/* $endif $ */
									}
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

	/**
	 *  Shutdown the service.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		final Future<Void>	ret	= new Future<Void>();
		component.getServiceContainer().searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.deregisterPropertiesProvider(PROEPRTIES_ID)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						SecurityService.this.passwords	= null;
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				SecurityService.this.passwords	= null;
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	//-------- password management --------
	
	/**
	 *  Check if password protection is enabled.
	 *  @return	True, if password protection is enabled.
	 */
	public IFuture<Boolean>	isUsePassword()
	{
		return new Future<Boolean>(usepass==null? Boolean.TRUE: usepass);
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
			this.usepass	= enable? Boolean.TRUE: Boolean.FALSE;
			ret	= IFuture.DONE;
			
			if(printpass && (usepass==null || usepass.booleanValue()))
			{
				/* $if android $
				Log.i("jadex-android", "Using stored platform password: "+password);
				$else $ */
				System.out.println("Using stored platform password: "+password);
				/* $endif $ */
			}
		}
		return ret;
	}

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
	 *  @param password	The password of the local platform. 
	 *  @throws  Exception, when a null password is provided and use password is true.
	 */
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setLocalPassword(String password)
	{
		IFuture<Void>	ret;
		if(password==null && (usepass==null || usepass.booleanValue()))
		{
			ret	= new Future<Void>(new IllegalStateException("Cannot set password to null, when password protection is enabled."));
		}
		else
		{
			this.password	= password;
			ret	= IFuture.DONE;
			if(printpass && (usepass==null || usepass.booleanValue()))
			{
				/* $if android $
				Log.i("jadex-android", "Using new platform password: "+password);
				$else $ */
				System.out.println("Using new platform password: "+password);
				/* $endif $ */
			}
		}
		return ret;
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
		String	starget	= target.getPlatformPrefix();
		String	ret	= passwords.get(starget);
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
	// Todo: password is transferred in plain text unless transport uses encryption.
	public IFuture<Void>	setTargetPassword(IComponentIdentifier target, String password)
	{
		if(password!=null)
		{
			passwords.put(target.getPlatformPrefix(), password);
		}
		else
		{
			// Use remove to avoid keeping old mappings forever (name would still be stored otherwise)
			passwords.remove(target.getPlatformPrefix());
		}
		return IFuture.DONE;		
	}

	/**
	 *  Get all stored passwords.
	 *  @return A map containing the stored passwords as pairs (platform name -> password).
	 */
	// Todo: passwords are transferred in plain text unless transport uses encryption.
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
		if(Security.PASSWORD.equals(request.getSecurityLevel()) && (usepass==null || usepass.booleanValue()) && password!=null)
		{
			if(request.getAuthenticationData()!=null)
			{
				if(Math.abs(request.getTimestamp()-System.currentTimeMillis())<3000000)	// Todo: make freshness period configurable.
				{
					if(!Arrays.equals(request.getAuthenticationData(), buildDigest(request.getTimestamp(), password)))
					{
//						System.out.println("received auth data: "+new String(Base64.encode(request.getAuthenticationData()))+", "+request.getTimestamp());
//						System.out.println("expected auth data: "+new String(Base64.encode(buildDigest(request.getTimestamp(), password)))+", "+password);
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
		
		return error==null ? new Future<Void>((Void)null) : new Future<Void>(new SecurityException(error+" "+request));
	}
	
	
	/**
	 *  Preprocess a request.
	 *  Adds authentication data to the request, if required by the intended target.
	 *  @param request	The request to be preprocessed.
	 *  @param target	The target to which the request should be sent later.
	 */
	public IFuture<Void>	preprocessRequest(IAuthorizable request, IComponentIdentifier target)
	{
		String	stripped	= target.getPlatformPrefix();
		// Use stored password or local password for local targets.  
		String	pw	= passwords.containsKey(stripped) ? passwords.get(stripped)
			: stripped.equals(component.getComponentIdentifier().getPlatformPrefix()) ? password : null;
		
		if(pw!=null)
		{
			long	timestamp	= System.currentTimeMillis();
			byte[]	authdata	= buildDigest(timestamp, pw);
			request.setTimestamp(timestamp);
			request.setAuthenticationData(authdata);
//			System.out.println("sending auth data: "+new String(Base64.encode(request.getAuthenticationData()))+", "+pw+", "+timestamp);
		}
		
		return IFuture.DONE;
	}

	//-------- static part --------
	
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
		// See http://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigest
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
