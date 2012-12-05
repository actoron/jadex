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

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  todo: ensure authentication between ttp and platform. Works only
 *  if they already share a certificate.
 */
public class TTPAcquisitionMechanism extends AAcquisitionMechanism
{
	//-------- attributes --------
	
	/** The name of the trusted third party. */
	protected String ttpname;
	protected IComponentIdentifier ttpcid;
	protected ISecurityService ttpsecser;
	
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
		this.ttpname = ttpname;
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
		params.add(new ParameterInfo("ttpname", IComponentIdentifier.class, ttpname));
		params.add(new ParameterInfo("ttpcid", IComponentIdentifier.class, ttpcid));
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
		
		if("ttpname".equals(name))
		{
			ttpname = (String)value;
			ttpcid = null;
		}
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
	 * 
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
			SServiceProvider.getService(getSecurityService().getComponent().getServiceContainer(), ttpcid, ISecurityService.class)
				.addResultListener(new DelegationResultListener<ISecurityService>(ret));
		}
		else if(ttpname!=null)
		{
			SServiceProvider.getServices(secser.getComponent().getServiceContainer(), ISecurityService.class, RequiredServiceInfo.SCOPE_GLOBAL)
				.addResultListener(new IIntermediateResultListener<ISecurityService>()
			{
				public void intermediateResultAvailable(ISecurityService ss)
				{
					if(((IService)ss).getServiceIdentifier().getProviderId().getPlatformPrefix().equals(ttpname))
					{
						ret.setResult(ss);
					}
				}
				
				public void finished()
				{
					ret.setExceptionIfUndone(new SecurityException("TTP not found: "+ttpname));
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
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
}