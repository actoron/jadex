package jadex.application;

import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Environment service implementation.
 */
@Service
public class EnvironmentService	implements IEnvironmentService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The spaces. */
	protected Map<String, IExtensionInstance>	spaces;
	
	//-------- methods --------
	
	/**
	 *  Init the spaces.
	 */
	@ServiceStart
	public IFuture<Void>	initSpaces()
	{
		Future<Void>	ret	= new Future<Void>();
		this.spaces	= new LinkedHashMap<String, IExtensionInstance>();
		
		ApplicationConfigurationInfo	config	= (ApplicationConfigurationInfo)component.getModel().getConfiguration(component.getConfiguration());
		IExtensionInfo[]	infos	= config.getExtensions();
		final IResultListener<Void>	lis	= new CounterResultListener<Void>(infos.length, new DelegationResultListener<Void>(ret));
		
		for(final IExtensionInfo ei: infos)
		{
			ei.createInstance(component.getExternalAccess(), component.getFetcher()).addResultListener(
				component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IExtensionInstance>()
			{
				public void resultAvailable(final IExtensionInstance instance)
				{
					spaces.put(ei.getName(), instance);	// Make space known before init, in case initial avatars are created that create agents that want to get their spaces. (hack?)
					instance.init().addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(lis));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					lis.exceptionOccurred(exception);
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the spaces.
	 */
	@ServiceShutdown
	public IFuture<Void>	terminateSpaces()
	{
		Future<Void>	ret	= new Future<Void>();
		final IResultListener<Void>	lis	= new CounterResultListener<Void>(spaces.size(), new DelegationResultListener<Void>(ret));
		
		for(final IExtensionInstance instance: spaces.values())
		{
			instance.terminate().addResultListener(
				component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					lis.resultAvailable(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					lis.exceptionOccurred(exception);
				}
			}));
		}
		
		return ret;
	}

	
	//-------- IEnvironmentService --------
	
	/**
	 *	Get a space. 
	 */
	public @Reference(remote=false) IFuture<Object>	getSpace(String name)
	{
		return new Future<Object>(spaces.get(name));
	}
	
	/**
	 *	Get a space for a component.
	 */
	public static IFuture<Object> getSpace(IInternalAccess component, final String name)
	{
		IEnvironmentService es	= component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IEnvironmentService.class));
		return es.getSpace(name);
	}
}
