package jadex.bridge;

import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;

/**
 *  Internal access adapter.
 */
public class InternalAccessAdapter implements IInternalAccess//, INonUserAccess
{
	/** The delegate access. */
	protected IInternalAccess access;
	
	/**
	 *  Create a new adapter.
	 */
	public InternalAccessAdapter(IInternalAccess access)
	{
		this.access = access;
	}
	
//	//-------- INonUserAccess interface --------
//	
//	/**
//	 *  Get the exception, if any.
//	 *  
//	 *  @return The failure reason for use during cleanup, if any.
//	 */
//	public Exception	getException()
//	{
//		return ((INonUserAccess)access).getException();
//	}
//	
//	/**
//	 *  Get the shared platform data.
//	 *  
//	 *  @return The objects shared by all components of the same platform (registry etc.). See starter for available data.
//	 */
//	public Map<String, Object>	getPlatformData()
//	{
//		return ((INonUserAccess)access).getPlatformData();
//	}

	//-------- IInternalAccess interface --------
	
	/**
	 *  @deprecated From 3.0. Use getComponentFeature(IArgumentsResultsFeature.class).getArguments()
	 *  Get an argument value per name.
	 *  @param name The argument name.
	 *  @return The argument value.
	 */
	public Object getArgument(String name)
	{
		return access.getArgument(name);
	}
	
	/**
	 *  @deprecated From 3.0. Use internal access.
	 *  @return The interpreter.
	 */
	public IInternalAccess getInterpreter()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IInternalAccess getServiceContainer()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IInternalAccess getServiceProvider()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - Use getComponentFeature(IRequiredServicesFeatures.class).getService()
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return getFeature(IRequiredServicesFeature.class).getService(name);
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).scheduleStep()
	 *  Execute a component step.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return getFeature(IExecutionFeature.class).scheduleStep(step);
	}
	
	/**
	 * 	@deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).waitForDelay()
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
	{
		return getFeature(IExecutionFeature.class).waitForDelay(delay, step);
	}
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return access.getModel();
	}

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		return access.getConfiguration();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getId()
	{
		return access.getId();
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature(Class<? extends T> type)
	{
		return access.getFeature(type);
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature0(Class<? extends T> type)
	{
		return access.getFeature0(type);
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription	getDescription()
	{
		return access.getDescription();
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return access.killComponent();
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(Exception e)
	{
		return access.killComponent(e);
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return access.getExternalAccess();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return access.getLogger();
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	// Todo: move to IPlatformComponent?
	public IValueFetcher getFetcher()
	{
		return access.getFetcher();
	}
		
	/**
	 *  Get the parameter guesser.
	 *  @return The parameter guesser.
	 */
	// Todo: move to IPlatformComponent?
	public IParameterGuesser getParameterGuesser()
	{
		return access.getParameterGuesser();
	}
		
	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader()
	{
		return access.getClassLoader();
	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type)
	{
		return access.getChildren(type);
	}
	
	/**
	 *  Get the exception, if any.
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception getException()
	{
		return access.getException();
	}
}
