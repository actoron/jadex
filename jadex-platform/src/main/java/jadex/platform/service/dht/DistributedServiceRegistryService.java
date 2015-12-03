package jadex.platform.service.dht;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.dht.IDistributedServiceRegistryService;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.ServiceRegistration;
import jadex.bridge.service.types.dht.StoreEntry;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * Distributed Service Registry
 */
@Service
public class DistributedServiceRegistryService extends DistributedKVStoreService implements IDistributedServiceRegistryService
{
	
	/** Delay in ms between two stabilize runs **/
	protected static final long	CHECK_LEASE_DELAY		= 2 * ServiceRegistration.LEASE_TIME;
	
	/** indicates whether the service was started **/
	private boolean started;
	
	@Override
	public void onServiceStarted()
	{
		super.onServiceStarted();
		if (!started) {
			executor.waitForDelay(CHECK_LEASE_DELAY, checkValidityStep);
			this.started = true;
		}
	}
	
	/**
	 * Publish a service in the distributed registry.
	 * @param typeName Type of the service (fully-qualified)
	 * @param serviceIdentifier SID of the service
	 * @return Void
	 */
	public IFuture<Void> publish(String typeName, IServiceIdentifier serviceIdentifier)
	{
		final Future<Void> fut = new Future<Void>();
		ServiceRegistration sereg = new ServiceRegistration();
		sereg.setSid(serviceIdentifier);
		add(typeName, sereg).addResultListener(new ExceptionDelegationResultListener<IID, Void>(fut) {
			@Override
			public void customResultAvailable(IID result)
			{
				fut.setResult(null);
			}
		});
		
		return fut;
	}
	
	@Override
	protected IFuture<IID> storeLocal(final IID hash, final String key, final Object value, boolean addToCollection)
	{
		return executor.scheduleStep(new IComponentStep<IID>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public IFuture<IID> execute(IInternalAccess ia)
			{
				StoreEntry entry = keyMap.get(key);
				Set<ServiceRegistration> regSet;
				if (entry == null) {
					regSet = new HashSet<ServiceRegistration>();
					entry = new StoreEntry(hash, key, regSet);
					keyMap.put(key, entry);
				} else {
					Object oldValue = entry.getValue();
					regSet = (Set<ServiceRegistration>)oldValue;
				}
				
				ServiceRegistration sereg = (ServiceRegistration)value;
				
				// remove existing registration to renew lease.
				boolean remove = regSet.remove(sereg);
				regSet.add(sereg);
				
				if (remove) {
					log("Renewed lease: " + key + "(hash: " + hash +")");
				} else {
					log("Stored service: " + key + "(hash: " + hash +")" + " locally.");
				}
				return ring.getId();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Collection<ServiceRegistration>> lookup(String key)
	{
		return (IFuture<Collection<ServiceRegistration>>) super.lookup(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Collection<ServiceRegistration>> lookup(String key, IID idHash)
	{
		return (IFuture<Collection<ServiceRegistration>>) super.lookup(key, idHash);
	}
	
	private IComponentStep<Void>	checkValidityStep = new RepetitiveComponentStep<Void>(CHECK_LEASE_DELAY)
	{
		public IFuture<Void> customExecute(IInternalAccess ia)
		{
			IFuture<Void> checkValidity = checkValidity();
			return checkValidity;
		}
	};
	
	/**
	 * Check the validity of all entries and remove them, if invalid.
	 * @return Void
	 */
	protected IFuture<Void> checkValidity()
	{
		System.out.println("Check validity");
		Calendar	leaseExpireTime = Calendar.getInstance();
		leaseExpireTime.add(Calendar.MILLISECOND, -ServiceRegistration.LEASE_TIME);
		long expiredMillis = leaseExpireTime.getTimeInMillis();
		
		Iterator<StoreEntry> storeIt = keyMap.values().iterator();
		while(storeIt.hasNext())
		{
			StoreEntry storeEntry = storeIt.next();
			
			Collection<ServiceRegistration> regs = (Collection<ServiceRegistration>)storeEntry.getValue();
			
			boolean anyValid = false;
			Iterator<ServiceRegistration> regsIt = regs.iterator();
			while(regsIt.hasNext())
			{
				ServiceRegistration reg = regsIt.next();
				if (reg.getTimestamp() < expiredMillis) {
					System.out.println("expired: " + storeEntry.getKey());
					regsIt.remove();
				} else {
//					System.out.println("not expired: " + reg.getTimestamp().get(Calendar.HOUR_OF_DAY) + ":" + reg.getTimestamp().get(Calendar.MINUTE));
					anyValid = true;
				}
			}
			
			if (!anyValid) {
				storeIt.remove();
			}
			
		}
		return Future.DONE;
	}
	

}
