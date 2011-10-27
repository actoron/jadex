package jadex.bridge.service.component.interceptors;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.ComponentFuture;
import jadex.bridge.service.component.ComponentIntermediateFuture;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.commons.IFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.lang.reflect.Method;

/**
 *  The decoupling return interceptor ensures that the result
 *  notifications of a future a delivered on the calling 
 *  component thread.
 */
public class DecouplingReturnInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The parameter copy flag. */
	protected boolean copy;
	
	/** The marshal service. */
	protected IMarshalService marshal;
	
	/** The clone filter (fascade for marshal). */
	protected IFilter filter;

	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingReturnInterceptor(IExternalAccess ea, IComponentAdapter adapter, boolean copy)
	{
		assert ea!=null;
		assert adapter!=null;
		this.ea = ea;
		this.adapter = adapter;
		this.copy = copy;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Fetch marshal service first time.
		
		if(marshal==null)
		{
			SServiceProvider.getService(ea.getServiceProvider(), IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
			{
				public void customResultAvailable(IMarshalService result)
				{
					marshal = result;
					filter = new IFilter()
					{
						public boolean filter(Object object)
						{
							return marshal.isLocalReference(object);
						}
					}; 
					internalExecute(sic).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			internalExecute(sic).addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> internalExecute(final ServiceInvocationContext sic)
	{
//		return sic.invoke();
		Future<Void> fut	= new Future<Void>();
//		return sic.invoke();
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
				Object	res	= sic.getResult();
				
				if(res instanceof IIntermediateFuture)
				{
					Method method = sic.getMethod();
					Reference ref = method.getAnnotation(Reference.class);
					boolean copy = !marshal.isRemoteObject(sic.getObject()) && (ref!=null? !ref.local(): true);
					sic.setResult(new ComponentIntermediateFuture(ea, adapter, (IFuture)res, copy, marshal));
				}
				else if(res instanceof IFuture)
				{
					Method method = sic.getMethod();
					Reference ref = method.getAnnotation(Reference.class);
					boolean copy = !marshal.isRemoteObject(sic.getObject()) && (ref!=null? !ref.local(): true);
					sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res, copy, marshal));
				}
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
