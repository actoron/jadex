package jadex.bdiv3.runtime.impl;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;

/**
 *  Plan body implementation as a component.
 */
public class ComponentPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The component to create. */
	protected String	component;
	
	/** The created component. */
	protected IComponentIdentifier	cid;
	
	/** True, when the plan was aborted. */
	protected boolean	aborted;
	
	//-------- constructors --------
	
	/**
	 *  Create a component plan body.
	 */
	public ComponentPlanBody(String component, IInternalAccess ia, RPlan rplan)
	{
		super(ia, rplan);
		this.component	= component;
	}
	
	//-------- AbstractPlanBody template methods --------
	
	@Override
	public Class<?>[] getBodyParameterTypes()
	{
		return null;
	}
	
	@Override
	public Class< ? >[] getPassedParameterTypes()
	{
		return null;
	}
	
	@Override
	public Class< ? >[] getFailedParameterTypes()
	{
		return null;
	}
	
	@Override
	public Class<?>[] getAbortedParameterTypes()
	{
		return null;
	}
	
	@Override
	public Object invokeBody(Object[] params) throws BodyAborted
	{
		Future<Void>	ret	= new Future<>();
		IComponentManagementService cms = ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
		cms.createComponent(null, component, new CreationInfo(ia.getId()))
			.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
		{
			@Override
			public void firstResultAvailable(IComponentIdentifier result)
			{
				cid	= result;
			}
			
			@Override
			public void secondResultAvailable(Map<String, Object> result)
			{
				ret.setResult(null);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	@Override
	public Object invokePassed(Object[] params)
	{
		return null;
	}
	
	@Override
	public Object invokeFailed(Object[] params)
	{
		return null;
	}
	
	@Override
	public Object invokeAborted(Object[] params)
	{
		return null;
	}
	
	/**
	 *  Get the plan body.
	 */
	public Object getBody()
	{
		return cid;
	}
	
	/**
	 *  Issue abortion of the plan body, if currently running.
	 */
	public void abort()
	{
		if(cid!=null)
		{
			// todo: fix synchronous subcomponents!? may be called from inner or outer component.
			IComponentManagementService cms = ((IInternalRequiredServicesFeature)ia.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
			cms.destroyComponent(cid);
		}
		
		super.abort();
	}
}
