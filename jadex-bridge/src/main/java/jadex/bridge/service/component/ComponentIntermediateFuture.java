package jadex.bridge.service.component;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.commons.Cloner;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;

/**
 *  The component future ensures that intermediate future notifications
 *  are executed on the calling component thread.
 */
public class ComponentIntermediateFuture<E> extends IntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The adapter. */
	protected IComponentAdapter	adapter;
	
	/** The external acces. */
	protected IExternalAccess	ea;
	
	/** The parameter copy flag. */
	protected boolean copy;
	
	/** The marshal manager. */
	protected IMarshalService marshal;
	
	/** The clone filter. */
	protected IFilter filter;

	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentIntermediateFuture(IExternalAccess ea, IComponentAdapter adapter, IFuture source, 
		boolean copy, final IMarshalService marshal)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		this.copy = copy;
		this.marshal = marshal;
		this.filter = new IFilter()
		{
			public boolean filter(Object object)
			{
				return marshal.isLocalReference(object);
			}
		};
		source.addResultListener(new IntermediateDelegationResultListener<E>(this));
	}
		
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyListener(final IResultListener<Collection<E>> listener)
	{
		// Hack!!! Notify multiple listeners at once?
		if(adapter.isExternalThread())
		{
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ComponentIntermediateFuture.super.notifyListener(listener);
					return IFuture.DONE;
				}
			});
		}
		else
		{
			super.notifyListener(listener);
		}
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyIntermediateResult(final IIntermediateResultListener<E> listener, final E result)
	{
		// Hack!!! Notify multiple results at once?
		if(adapter.isExternalThread())
		{
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
					return IFuture.DONE;
				}
			});
		}
		else
		{
			super.notifyIntermediateResult(listener, result);
		}
	}
	
	/**
	 *  Add an intermediate result.
	 */
	public void	addIntermediateResult(E result)
	{
		// Copy result if
		// - copy flag is true
		// - and result is not a reference object
		if(copy && result!=null)
		{
			boolean copy = !marshal.isLocalReference(result);
			if(copy)
			{
//				System.out.println("copy result: "+result);
				result = (E)Cloner.deepCloneObject(result, marshal.getCloneProcessors(), filter);
			}
		}
		super.addIntermediateResult(result);
	}
}
