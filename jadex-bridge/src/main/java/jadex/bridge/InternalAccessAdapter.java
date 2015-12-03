package jadex.bridge;

import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.modelinfo.IModelInfo;
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
	public IComponentIdentifier	getComponentIdentifier()
	{
		return access.getComponentIdentifier();
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getComponentFeature(Class<? extends T> type)
	{
		return access.getComponentFeature(type);
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getComponentFeature0(Class<? extends T> type)
	{
		return access.getComponentFeature0(type);
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription	getComponentDescription()
	{
		return access.getComponentDescription();
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
