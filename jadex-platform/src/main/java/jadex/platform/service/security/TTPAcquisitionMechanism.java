package jadex.platform.service.security;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.security.ParameterInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;

import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *  todo: ensure authentication between ttp and platform. Works only
 *  if they already share a certificate.
 */
public class TTPAcquisitionMechanism extends AAcquisitionMechanism
{
	//-------- attributes --------
	
	/** The component id of the trusted third party. (with or without adresses to reach it) */
	protected IComponentIdentifier ttpcid;
	
	/** The security service of the ttp. */
	protected ISecurityService ttpsecser;
	
	/** Must ttp be verified (i.e. must its certificate be known and is checked). */
	protected boolean verified;
	
	//-------- constructors --------

	/**
	 *  Create a new mechanism.
	 */
	public TTPAcquisitionMechanism()
	{
		this(null);
	}
	
	/**
	 *  Create a new mechanism.
	 */
	public TTPAcquisitionMechanism(String ttpname)
	{
		this.ttpcid = new ComponentIdentifier(ttpname);
		this.verified = true;
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public IFuture<Certificate> acquireCertificate(final String name)
	{
		final Future<Certificate> ret = new Future<Certificate>();
		final IComponentIdentifier cid = new ComponentIdentifier(name);

		getTTPSecurityService().addResultListener(new ExceptionDelegationResultListener<ISecurityService, Certificate>(ret)
		{
			public void customResultAvailable(ISecurityService ss)
			{
				ss.getPlatformCertificate(cid).addResultListener(new DelegationResultListener<Certificate>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the mechanism info for the gui.
	 */
	public MechanismInfo getMechanismInfo()
	{
		List<ParameterInfo> params = new ArrayList<ParameterInfo>();
		params.add(new ParameterInfo("verify", "If turned on, the ttp is verified (its certificate must be in local keystore)", boolean.class, verified));
		params.add(new ParameterInfo("ttpcid", "The component identifier (or name) of the trusted third party", IComponentIdentifier.class, ttpcid));
		MechanismInfo ret = new MechanismInfo("Trusted Third Party", getClass(), params);
		return ret;
	}
	
	/**
	 *  Set a mechanism parameter value.
	 */
	public void setParameterValue(String name, Object value)
	{
		System.out.println("set param val: "+name+" "+value);
		ttpsecser = null;
		
		if("ttpcid".equals(name))
		{
			ttpcid = (IComponentIdentifier)value;
		}
		else
		{
			throw new RuntimeException("Unknown parameter: "+name);
		}
	}
	
	/**
	 *  Get the security service of the ttp.
	 */
	protected IFuture<ISecurityService> getTTPSecurityService()
	{
		final Future<ISecurityService> ret = new Future<ISecurityService>();
		
		if(ttpsecser!=null)
		{
			ret.setResult(ttpsecser);
		}
		else if(ttpcid!=null)
		{
			// real cid with addresses?
			if(ttpcid.getAddresses()!=null && ttpcid.getAddresses().length>0)
			{
				SServiceProvider.getService(getSecurityService().getComponent().getServiceContainer(), ttpcid, ISecurityService.class)
					.addResultListener(new DelegationResultListener<ISecurityService>(ret)
				{
					public void customResultAvailable(final ISecurityService ss)
					{
						verifyTTP(ss).addResultListener(new ExceptionDelegationResultListener<Void, ISecurityService>(ret)
						{
							public void customResultAvailable(Void result)
							{
								ret.setResult(ss);
							}
						});
					}
				});
			}
			// or just a name? Then find the ttp security service by searching (and comparing names)
			else
			{
				SServiceProvider.getServices(secser.getComponent().getServiceContainer(), ISecurityService.class, RequiredServiceInfo.SCOPE_GLOBAL)
					.addResultListener(new IIntermediateResultListener<ISecurityService>()
				{
					// Flag that indicates if ttpservice was found and is checked now
					protected boolean found = false;
					
					public void intermediateResultAvailable(final ISecurityService ss)
					{
						if(!found && ((IService)ss).getServiceIdentifier().getProviderId().getPlatformPrefix().equals(ttpcid.getName()))
						{
							found = true;
							verifyTTP(ss).addResultListener(new ExceptionDelegationResultListener<Void, ISecurityService>(ret)
							{
								public void customResultAvailable(Void result)
								{
									ret.setResult(ss);
								}
							});
						}
					}
					
					public void finished()
					{
						if(!found)
							ret.setExceptionIfUndone(new SecurityException("TTP not found: "+ttpcid.getName()));
					}
					
					public void resultAvailable(Collection<ISecurityService> result)
					{
						for(ISecurityService ss: result)
						{
							intermediateResultAvailable(ss);
						}
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(!found)
							ret.setExceptionIfUndone(exception);
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> verifyTTP(ISecurityService ss)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(verified)
		{
			try
			{
				// Check if local keystore contains ttp certificate
				final Certificate cert = getSecurityService().getKeyStore().getCertificate(ttpcid.getPlatformPrefix());
				if(cert==null)
				{
					ret.setException(new SecurityException("TTP certificate not available in keystore: "+ttpcid));
				}
				else
				{
					final byte[] content = new byte[20];
					new Random().nextBytes(content);
				
					// Let ttp sign some data
					ss.signCall(content).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
					{
						public void customResultAvailable(byte[] signed)
						{
							// Check if signed data can be verified with stored certificate
							if(getSecurityService().verifyCall(content, signed, cert))
							{
								ret.setResult(null);
							}
							else
							{
								ret.setException(new SecurityException("TTP authentication failed: "+ttpcid));
							}
						}
					});
				}
			}
			catch(KeyStoreException e)
			{
				ret.setException(new SecurityException("TTP certificate not available in keystore: "+ttpcid));
			}
			
		}
		else
		{
			// Ok if verification is turned of 
			ret.setResult(null);
		}
		
		return ret;
	}
}