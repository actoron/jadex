package jadex.extension.envsupport;

import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IExtensionInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.IEnvironmentSpace;

/**
 *  Environment service implementation.
 */
@Service
public class	EnvironmentService	implements IEnvironmentService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The environment space. */
	protected IEnvironmentSpace	space;
	
	//-------- methods --------
	
	/**
	 *  Init the space.
	 */
	@ServiceStart
	public IFuture<Void>	initSpace()
	{
		MEnvSpaceInstance	mesi	= null;
		ConfigurationInfo	config	= component.getModel().getConfiguration(component.getConfiguration());
		for(IExtensionInfo ei: config.getExtensions())
		{
			if(ei instanceof MEnvSpaceInstance)
			{
				mesi	= (MEnvSpaceInstance)ei;
				break;
			}
		}
		
		Future<Void>	ret	= new Future<Void>();
		if(mesi!=null)
		{
			try
			{
				Class<AbstractEnvironmentSpace>	clazz	= SReflect.findClass(mesi.getType().getClassName(), component.getModel().getAllImports(), component.getClassLoader());
				AbstractEnvironmentSpace	aspace	= clazz.newInstance();
				aspace.setInitData(component, mesi, component.getFetcher());
				space	= aspace;
				return space.init();
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setException(new RuntimeException("No space found in configuration."));
		}
		
		return ret;
	}
	
	//-------- IEnvironmentService --------
	
	/**
	 *	Get the environment space. 
	 */
	public @Reference(remote=false) IFuture<IEnvironmentSpace>	getSpace()
	{
		return new Future<IEnvironmentSpace>(space);
	}
	
	/**
	 *	Get the environment space for a component
	 */
	public static IFuture<IEnvironmentSpace>	getSpace(IInternalAccess component)
	{
		final Future<IEnvironmentSpace>	ret	= new Future<IEnvironmentSpace>();
		
		component.getComponentFeature(IRequiredServicesFeature.class).searchService(IEnvironmentService.class, RequiredServiceInfo.SCOPE_APPLICATION)
			.addResultListener(new ExceptionDelegationResultListener<IEnvironmentService, IEnvironmentSpace>(ret)
		{
			public void customResultAvailable(IEnvironmentService es)
			{
				es.getSpace().addResultListener(new DelegationResultListener<IEnvironmentSpace>(ret));
			}
		});
		
		return ret;
	}
}
