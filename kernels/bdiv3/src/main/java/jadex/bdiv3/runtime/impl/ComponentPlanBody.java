package jadex.bdiv3.runtime.impl;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

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
//		final Future<Void>	ret	= new Future<Void>();
//
//		rplan.setLifecycleState(RPlan.PlanLifecycleState.BODY);
//		// Todo: should also set processing state and RPLANS thread local?
//		
////		IComponentManagementService cms = ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
//		CreationInfo ci = new CreationInfo(ia.getId());
//		ci.setFilename(component);
//		
//		ia.createComponent(ret, ci)
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
		ia.createComponent(new CreationInfo(ia.getId()).setFilename(component)).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
			
			public void resultAvailable(IExternalAccess result)
			{
				cid = result.getId();
				result.waitForTermination().addResultListener(new IResultListener<Map<String,Object>>()
				{
					public void resultAvailable(Map<String, Object> result)
					{
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
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
			ia.getExternalAccess(cid).killComponent();
		}
		
		super.abort();
	}
}
