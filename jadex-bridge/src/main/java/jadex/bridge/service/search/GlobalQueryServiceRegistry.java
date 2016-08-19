package jadex.bridge.service.search;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;
import jadex.commons.collection.BloomFilter;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.transformation.binaryserializer.BinarySerializer;

/**
 *  Registry that allows for adding global queries with local registry.
 *  Uses search to emulate the persistent query.
 */
public class GlobalQueryServiceRegistry extends ServiceRegistry
{
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The global query delay. */
	protected long delay;
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		super.addQuery(query).addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
			
		// Emulate persistent query by searching periodically
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Class<T> mytype = query.getType()==null? null: (Class<T>)query.getType().getType0();
					searchRemoteServices(query.getOwner(), mytype, query.getFilter())
						.addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));
					
					DuplicateRemovalIntermediateResultListener<T> lis = new DuplicateRemovalIntermediateResultListener<T>(new UnlimitedIntermediateDelegationResultListener<T>(ret));
					
					if(!ret.isDone() && containsQuery())
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
					
					return IFuture.DONE;
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		
		
		super.removeQuery(query);
	}
	
	
	/**
	 *  A result listener that filters duplicates.
	 */
	public static class DuplicateRemovalIntermediateResultListener<E> extends IntermediateDelegationResultListener<E>
	{
		/** The bloom filter. */
		protected BloomFilter filter;
		
		/**
		 *  Create a new listener.
		 *  @param future The delegation target.
		 *  @param undone use undone methods.
		 *  @param customResultListener Custom result listener that overwrites the
		 *        delegation behaviour.
		 */
		public DuplicateRemovalIntermediateResultListener(IntermediateFuture<E> future, IFunctionalResultListener<E> customIntermediateResultListener)
		{
			this(future, false, customIntermediateResultListener);
		}

		/**
		 *  Create a new listener.
		 *  @param future The delegation target.
		 *  @param undone use undone methods.
		 *  @param customResultListener Custom result listener that overwrites the
		 *        delegation behaviour. Can be null
		 */
		public DuplicateRemovalIntermediateResultListener(IntermediateFuture<E> future, boolean undone, IFunctionalResultListener<E> customIntermediateResultListener)
		{
			this(future, undone);
			this.customIntermediateResultListener = customIntermediateResultListener;
		}
		
		/**
		 *  Create a new listener.
		 */
		public DuplicateRemovalIntermediateResultListener(IntermediateFuture<E> future)
		{
			this(future, false);
		}
		
		/**
		 *  Create a new listener.
		 */
		public DuplicateRemovalIntermediateResultListener(IntermediateFuture<E> future, boolean undone)
		{
			super(future, undone);
		}
		
		/**
		 *  Check results before sending them further.
		 */
		public void customIntermediateResultAvailable(E result)
		{
			if(!filter.add(objectToByteArray(result)))
			{
				super.customIntermediateResultAvailable(result);
			}
			else
			{
				System.out.println("Filtered out duplicate: "+result);
			}
		}
		
		/**
		 *  Convert a value to a byte array. 
		 *  @param value The value.
		 *  @return The byte array.
		 */
		protected byte[] objectToByteArray(E value)
		{
			byte[] ret = null;
			if(value instanceof Serializable)
			{
				ByteArrayOutputStream bos = null;
				ObjectOutputStream out = null;
				try
				{
					bos = new ByteArrayOutputStream();
					out = new ObjectOutputStream(bos);
					out.writeObject(value);
					ret = bos.toByteArray();
				}
				catch(Exception e)
				{
					SUtil.rethrowAsUnchecked(e);
				}
				finally
				{
					if(bos!=null)
						try{bos.close();}catch(Exception e){SUtil.rethrowAsUnchecked(e);}
					if(out!=null)
						try{out.close();}catch(Exception e){SUtil.rethrowAsUnchecked(e);}
				}
			}	
			else 
			{
				BinarySerializer.objectToByteArray(value, null);
			}
			
			return ret;
		}
	}
}
