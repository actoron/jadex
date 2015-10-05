package jadex.platform.service.dht;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.dht.IDistributedKVStoreDebugService;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.bridge.service.types.dht.IRingApplicationService.State;
import jadex.bridge.service.types.dht.RingNodeEvent;
import jadex.bridge.service.types.dht.StoreEntry;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that allows storing of key/value pairs in a DHT ring.
 */
@Service
public class DistributedKVStoreService implements IDistributedKVStoreService, IDistributedKVStoreDebugService
{
	
	/** Delay in ms between two stabilize runs **/
	protected static final long	CHECK_STORED_DATA_DELAY		= 60 * 1000;
	
	/** Map that stores the actual data. Key -> StoreEntry **/
	protected Map<String, StoreEntry>	keyMap;
	
	/** The local Ring Node  to access the DHT Ring. **/
	protected IRingApplicationService ring;
	/** The local ID **/
	protected  IID	myId;
	
	/** The local agent access. **/
	@ServiceComponent
	protected IInternalAccess agent;

	/** The logger. **/
	protected  Logger	logger;

	/** Flag that indicates whether this Service is already usable. */
	protected boolean	initialized;
	
	/** The execution Feature of the agent. **/
	protected IExecutionFeature executor;
	
	/**
	 * Constructor.
	 */
	public DistributedKVStoreService()
	{
		this.keyMap = new ConcurrentHashMap<String, StoreEntry>();
		this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Sets the initialized flag.
	 */
	public void setInitialized(boolean value)
	{
		this.initialized = value;
	}
	
	/**
	 * Gets the initialized flag.
	 */
	public boolean isInitialized()
	{
		return initialized;
	}
	
	@ServiceStart
	public void onServiceStarted() {
//		System.out.println("KVStoreService started");
		executor = agent.getComponentFeature(IExecutionFeature.class);
	}

	/**
	 * Set the local ringNode.
	 * 
	 * @param ring the new ringNode
	 */
	public void setRingService(IRingApplicationService ring)
	{
		this.ring = ring;
		myId = ring.getId().get();
		ISubscriptionIntermediateFuture<RingNodeEvent> subscription = ring.subscribeForEvents();
		IntermediateDefaultResultListener<RingNodeEvent> eventListener = new IntermediateDefaultResultListener<RingNodeEvent>()
		{
			public void intermediateResultAvailable(RingNodeEvent event)
			{
				eventReceived(event);
			}
		};
		subscription.addIntermediateResultListener(eventListener);
	}
	
	/**
	 * Publish a key/value pair in the corresponding node.
	 * 
	 * @param key The Key.
	 * @param value The Value.
	 * @return The ID of the node this key was saved in.
	 */
	public IFuture<IID> put(String key, Object value)
	{
		return store(key, value, false);
	}
	
	/**
	 * Add a key/value pair to the collection addressed by the given key.
	 * 
	 * @param key The Key.
	 * @param value The Value to add.
	 * @return The ID of the node this key was saved in.
	 */
	public IFuture<IID> add(String key, Object value)
	{
		return store(key, value, true);
	}

	protected IFuture<IID> store(final String key, final Object value, final boolean addToCollection) {
		return store(ID.get(key), key, value, addToCollection);
	}
	
	protected IFuture<IID> store(final IID hash, final String key, final Object value, final boolean addToCollection)
	{
		return executor.scheduleStep(new IComponentStep<IID>()
		{

			@Override
			public IFuture<IID> execute(IInternalAccess ia)
			{
				log("store for " + key);
				final Future<IID> ret = new Future<IID>();
				ring.findSuccessor(hash).addResultListener(new DefaultResultListener<IFinger>()
				{

					@Override
					public void resultAvailable(IFinger finger)
					{
						final IID nodeId = finger.getNodeId();
						// if (providerId.equals(myCid)) {
						if(nodeId.equals(myId))
						{
							// use local access
							storeLocal(key, value, addToCollection).addResultListener(new DelegationResultListener<IID>(ret));
						}
						else
						{
							getStoreService(finger).addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
							{
								@Override
								public void resultAvailable(IDistributedKVStoreService result)
								{
									log("Storing key: " + key + "(hash: " + hash + ")" + " in: " + nodeId);
									
									IFuture<IID> publish;
									if (addToCollection) {
										publish = result.add(key, value);
									} else {
										publish = result.put(key, value);
									}
//										IFuture<IID> publish = result.publish(key, value);
									publish.addResultListener(new DelegationResultListener<IID>(ret));
								}
								
								public void exceptionOccurred(Exception exception) {
									log("Failed to store key: " + key + "(hash: " + hash + ")" + " in: " + nodeId);
								};
							});
						}
					}
				});
				return ret;
			}
		});
	}

	
	
	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param key The key
	 * @param value The value
	 * @param addToCollection If true, the value will be added to the collection stored 
	 * @return the ID of the local node.
	 */
	private IFuture<IID> storeLocal(String key, Object value, boolean addToCollection) {
		IID hash = ID.get(key);
		return storeLocal(hash, key, value, addToCollection);
	}
	
	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param hash The hash
	 * @param key The key
	 * @param value The value
	 * @return the ID of the local node.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected IFuture<IID> storeLocal(final IID hash, final String key, final Object value, final boolean addToCollection) {
		
//		if (!isResponsibleFor(hash)) {
//			logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + hash + ". My successor is " + ring.getSuccessor().get().getNodeId());
//		}
		
//		return executor.scheduleStep(new IComponentStep<IID>()
//		{
//
//			@Override
//			public IFuture<IID> execute(IInternalAccess ia)
//			{
				StoreEntry entry = keyMap.get(key);
				if (entry == null) {
					entry = new StoreEntry(hash, key, addToCollection ? new ArrayList() : value);
					keyMap.put(key, entry);
				}
				
				Object oldValue = entry.getValue();
				if (addToCollection) {
					if (oldValue instanceof Collection) {
						Collection col = (Collection)oldValue;
						col.add(value);
					} else {
						logger.severe("Tried to add value to a collection, but single value is already saved for key: " + key);
					}
				} else {
					if (oldValue instanceof Collection) {
						logger.warning("Replaced a collection instead of adding a value for key: " + key);
					}
				}
				log("Stored key: " + key + "(hash: " + hash +")" + " locally.");
//				System.out.println(myId + ": Stored key: " + key + "(hash: " + hash +")" + " locally.");
				//		System.out.println(keyMap.size());
				//		idMap.put(hash, entry);
				return ring.getId();
//			}
//		});
		
	}

	/**
	 * Lookup a key and return the responsible Node ID.
	 * 
	 * @param key Requested key.
	 * @return IID of the responsible node.
	 */
	public IFuture<IID> lookupResponsibleStore(String key) {
		final Future<IID> ret = new Future<IID>();
//		final IExecutionFeature execFeature = agent.getComponentFeature(IExecutionFeature.class);
		final IID id = ID.get(key);
		log("lookupResponsibleStore for " + key);
		ring.findSuccessor(id).addResultListener(new ExceptionDelegationResultListener<IFinger, IID>(ret)
		{

			@Override
			public void customResultAvailable(IFinger result) throws Exception {
				ret.setResult(result.getNodeId());
				super.customResultAvailable(result);
			}
			
		});
		
		return ret;
	}
	
	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<?> lookup(String key) {
		return lookup(key, ID.get(key));
	}

	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @param idHash The hashed key to find the corresponding node.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<?> lookup(final String key, final IID idHash)
	{
//		final Future<Object> ret = new Future<Object>();
		if (!initialized) {
			Future<Object> future = new Future<Object>();
			System.out.println("KVStore not initialized!");
			future.setResult(null);
			return future;
		}
		return executor.scheduleStep(new IComponentStep<Object>()
		{

			@Override
			public IFuture<Object> execute(IInternalAccess ia)
			{
				log("lookup for " + key);
				final Future<Object> fut = new Future<Object>();
				// faster local lookup: check if key is saved locally.
				StoreEntry storeEntry = keyMap.get(key);
				if (storeEntry != null && idHash.equals(storeEntry.getIdHash())) {
					log("retrieving from local map: "  +key+ " (hash: " + idHash +")");
					fut.setResult(storeEntry.getValue());
					return fut;
				}
				
				// if not stored locally, use the expensive successor lookup.
				ring.findSuccessor(idHash).addResultListener(new DefaultResultListener<IFinger>()
				{
				
					@Override
					public void resultAvailable(final IFinger finger)
					{
						if(finger.getNodeId().equals(myId))
						{
							// use local access
							
							log("retrieving from local map: "  +key+ " (hash: " + idHash +")");
//							if(!isResponsibleFor(idHash))
//							{
//								logger.log(Level.WARNING, myId + ": lookupLocal called even if i do not feel responsible for: " + idHash + ". My successor is " + ring.getSuccessor().get().getNodeId());
//							}
							StoreEntry storeEntry = keyMap.get(key);
							if(storeEntry != null) // should not happen as this was checked in beforehand
							{
								fut.setResult(storeEntry.getValue());
							}
							else
							{
								fut.setResult(null);
							}
						} else {
							log("retrieving key: " +key+" (hash: " + idHash + ") from successor: " + finger.getNodeId());
							//	final IComponentIdentifier providerId = result.getSid().getProviderId();
							executor.scheduleStep(new IComponentStep<Object>()
							{
								
								public IFuture<Object> execute(IInternalAccess ia)
								{
									final Future<Object> ret = new Future<Object>();;
									// search for remote kvstore service
									//							System.out.println(myId + ": retrieving from remote: " + " (hash: " + idHash +")");
									IFuture<IDistributedKVStoreService> storeService = getStoreService(finger);
									//							IFuture<IDistributedKVStoreService> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IDistributedKVStoreService.class,
									//								providerId.getParent());
									storeService.addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
									{
										public void resultAvailable(IDistributedKVStoreService result)
										{
											@SuppressWarnings("unchecked")
											IFuture<Object> value = (IFuture<Object>)result.lookup(key, idHash);
											value.addResultListener(new DefaultResultListener<Object>()
											{
											
												@Override
												public void resultAvailable(Object result)
												{
													ret.setResult(result);
												}
											});
										}
										
										public void exceptionOccurred(Exception exception) {
											log("failed to retrieve key: " +key+" (hash: " + idHash + ") from successor: " + finger.getNodeId());
											ret.setException(exception);
										};
									});
									return ret;
								}
							}).addResultListener(new DelegationResultListener<Object>(fut));
						}
						
					}
				});
				return fut;
			}
		});
	}

	/**
	 * Returns all keys stored in this node.
	 * 
	 * @return Set of Keys.
	 */
	@Override
	public IFuture<Set<String>> getLocalKeySet()
	{
		Set<String> hashSet = keyMap.keySet();
		return new Future<Set<String>>(hashSet);
	}
	
	/**
	 * Returns all IDs stored in this node.
	 * 
	 * @return Set of Keys.
	 */
//	public IFuture<Set<IID>> getLocalIds() {
//		return new Future<Set<IID>>(idMap.keySet());
//	}
	
	/**
	 * Returns all entries that belong to the given node Id
	 * and deletes them on this node.
	 * @param targetNodeId
	 * @return Set of all matching entries.
	 */
	public IFuture<Collection<StoreEntry>> pullEntries(final IID targetNodeId) {
		// Another node requests entries. I store only entries with: predecessor.id < entry.id <= myId.
		// The target node must have: predecessor.id < target.id < myId, because it has me as its successor.
		// In consequence, i can pass all entries with: myId < entry.id < targetNodeId (because we are in a circle).
		
		return executor.scheduleStep(new IComponentStep<Collection<StoreEntry>>()
		{

			@Override
			public IFuture<Collection<StoreEntry>> execute(IInternalAccess ia)
			{
				Set<StoreEntry> result = new LinkedHashSet<StoreEntry>();
				
				Iterator<StoreEntry> it = keyMap.values().iterator();
				
				while(it.hasNext())
				{
					StoreEntry entry = (StoreEntry)it.next();
					if (entry.getIdHash().isInInterval(myId, targetNodeId, false, true)) {
						result.add(entry);
						it.remove();
					}
				}
				return new Future<Collection<StoreEntry>>(result);
			}
		});
	}
	
	@Override
	public IFuture<Void> pushEntries(Collection<StoreEntry> entries)
	{
		final IFinger predec = ring.getPredecessor().get();
//		log("pushEntries received. Current predecessor: " + (predec != null ? predec.getNodeId() : null));
		log("pushEntries received with " + entries.size() + " entries.");
		final Collection<StoreEntry> collForPredec = new ArrayList<StoreEntry>();
		boolean responsible = true;
		for(final StoreEntry storeEntry : entries)
		{
			responsible = true;
			if (predec != null && !predec.getNodeId().equals(myId)) {
				if (storeEntry.getIdHash().isInInterval(myId, predec.getNodeId(), false, true)) {
					// this entry belongs to my predecessor
					responsible = false;
					collForPredec.add(storeEntry);
				}
			}
			// When i receive a push, it means my successor is not responsible for this data.
			// This means either i am responsible, or the data should be passed on to my predecessor.
			if (responsible) {
//				System.out.println("Saving locally");
				if (storeEntry.getValue() instanceof Collection) {
					// respect existing local collection instead of replacing it as whole.
					Collection collection = (Collection)storeEntry.getValue();
					for(Object singleValue : collection)
					{
						storeLocal(storeEntry.getIdHash(), storeEntry.getKey(), singleValue, true);
					}
				} else {
					storeLocal(storeEntry.getIdHash(), storeEntry.getKey(), storeEntry.getValue(), false);
				}
			}
			
			// predec case: asynchronously push to predecessor
			if (!collForPredec.isEmpty()) {
				getStoreService(predec).addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
				{
					public void resultAvailable(IDistributedKVStoreService result)
					{
						result.pushEntries(collForPredec);
					}
					
					// dont care about exceptions, as data has to be periodically refreshed anyways.
					@Override
					public void exceptionOccurred(Exception exception)
					{
						log("pushEntries: could not pass data to " + predec.getNodeId() + ", as it is unavailable.");
					}
				});
			}
		}
		return Future.DONE;
	}

	/**
	 * Checks whether this store service is responsible for saving/retrieving a
	 * key with the given hash value.
	 * 
	 * @param hash
	 * @return true, if this store service is responsible, else false.
	 */
//	private boolean isResponsibleFor(IID hash)
//	{
//		IFinger suc = ring.getSuccessor().get();
//		return (suc == null) ? true : (myId.isInInterval(hash, suc.getNodeId(), true, false));
//	}

	/**
	 * Lookup the storage service for a given finger.
	 * @param finger
	 * @return {@link IDistributedKVStoreService}
	 */
	public IFuture<IDistributedKVStoreService> getStoreService(IFinger finger) {
		IFuture<IDistributedKVStoreService> searchService = SServiceProvider.getService(agent, finger.getSid().getProviderId(), IDistributedKVStoreService.class);
		searchService.addResultListener(new IResultListener<IDistributedKVStoreService>()
		{
			public void resultAvailable(IDistributedKVStoreService result)
			{
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{
				// TODO: on exceptions, invalidate finger.
			}
		});
		return searchService;
	}
	
	
	
	@Override
	public void disableSchedules()
	{
		checkDataStep = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return Future.DONE;
			}
		};
	}
	
	/**
	 * Check all entries for validity and move them to another node, if necessary.
	 * @return Void
	 */
	public IFuture<Void> checkData()
	{
		final Future<Void> ret = new Future<Void>();
		if (ring.getState() == State.JOINED) {
			final IFinger predec = ring.getPredecessor().get();
			if (predec != null && !predec.getNodeId().equals(myId)) {
				final Collection<StoreEntry> collForPredec = new ArrayList<StoreEntry>();
				
				Iterator<StoreEntry> it = keyMap.values().iterator();
				while(it.hasNext())
				{
					StoreEntry storeEntry = it.next();
					// check responsibility for this entry
					if (storeEntry.getIdHash().isInInterval(myId, predec.getNodeId(), false, true)) {
						// this entry belongs to my predecessor
						collForPredec.add(storeEntry);
						it.remove();
					}
				}
				if (!collForPredec.isEmpty()) {
					getStoreService(predec).addResultListener(new ExceptionDelegationResultListener<IDistributedKVStoreService, Void>(ret)
					{
						public void customResultAvailable(IDistributedKVStoreService result) {
							log("checkData moving " + collForPredec.size() + " items to predecessor: " + predec.getNodeId());
							result.pushEntries(collForPredec).addResultListener(new ExceptionDelegationResultListener<Void,Void>(ret)
							{
								public void customResultAvailable(Void result) {
									log("checkData moved " + collForPredec.size() + " items to predecessor: " + predec.getNodeId());
									ret.setResult(null);
								};
								public void exceptionOccurred(Exception exception) {
									// re-add temporarily?
									log("Could not move " + collForPredec.size() + " items to predecessor: " + predec.getNodeId());
								};
							});
						};
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							log("Could not move " + collForPredec.size() + " items to predecessor: " + predec.getNodeId());
						}
					});
				} else {
					ret.setResult(null);
				}
			} else {
				ret.setResult(null);
			}
		} else {
			ret.setResult(null);
		}
		
		return ret;
	}

	protected IComponentStep<Void> checkDataStep = new RepetitiveComponentStep<Void>(CHECK_STORED_DATA_DELAY)
	{

		@Override
		public IFuture<Void> customExecute(IInternalAccess ia)
		{
			return checkData();
		}
	};
	
	/**
	 * Called upon events received from the ring service.
	 * @param event
	 */
	protected void eventReceived(final RingNodeEvent event)
	{
		switch(event.type)
		{
			case JOIN:
				agent.getExternalAccess().scheduleStep(checkDataStep, CHECK_STORED_DATA_DELAY);
				break;
			case SUCCESSOR_CHANGE:
				// move data with id in (predecessor, myId] from successor,
				// so get everything < myId.
				final IFinger successor = event.newFinger;
				if (!successor.getNodeId().equals(myId)) {
					getStoreService(successor).addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
					{
						public void resultAvailable(final IDistributedKVStoreService sucStore)
						{
							sucStore.pullEntries(myId).addResultListener(new DefaultResultListener<Collection<StoreEntry>>()
							{
								public void resultAvailable(Collection<StoreEntry> result)
								{
	//								System.out.println("I am: " + myId + ", got " + result.size() + " entries from " + successor.getNodeId());
									if (!result.isEmpty()) {
										pushEntries(result).get();
									}
								}
							});
						}
					});
				}
				break;
			case PART:
				break;
			case PREDECESSOR_CHANGE:
				if (event.newFinger != null && !event.newFinger.getNodeId().equals(myId)) {
					// when someone joins me, I'm only informed after stabilize.
					// So between join and stabilize, i could have stored keys that belong to my predecessor
					// - or to any other node i didn't know before.
					
//					executor.scheduleStep(new IComponentStep<Void>()
//					{
//
//						@Override
//						public IFuture<Void> execute(IInternalAccess ia)
//						{
//							final Future<Void> ret = new Future<Void>();
//							final Set<StoreEntry> entries = new LinkedHashSet<StoreEntry>();
//							
//							Iterator<StoreEntry> it = keyMap.values().iterator();
////							System.out.println(myId + ": Got " + keyMap.size() + " entries in total.");
//			
//							while(it.hasNext())
//							{
//								StoreEntry entry = (StoreEntry)it.next();
//								if (entry.getIdHash().isInInterval(myId, event.newFinger.getNodeId(), false, true)) {
//									entries.add(entry);
//									it.remove();
//								}
//							}
//							
//							getStoreService(event.newFinger).addResultListener(new ExceptionDelegationResultListener<IDistributedKVStoreService, Void>(ret)
//							{
//			
//								@Override
//								public void customResultAvailable(IDistributedKVStoreService predec)
//								{
////									System.out.println("I am: " + myId + ", pushing " + entries.size() + " entries to " + event.newFinger.getNodeId());
//									predec.pushEntries(entries).addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
//									{
//			
//										@Override
//										public void customResultAvailable(Void result)
//										{
//											ret.setResult(null);
//										}
//										
//										@Override
//										public void exceptionOccurred(Exception exception)
//										{
//											super.exceptionOccurred(exception);
//											// re-add local entries.
//											System.err.println("Couldn't push entries.");
//											for(StoreEntry e : entries)
//											{
//												keyMap.put(e.getKey(), e);
//											}
//										}
//									});
//								}
//							});
//							return ret;
//						}
//					});
					
				}
				break;
			case FINGERTABLE_CHANGE:
				break;
			default:
				break;
		}
	}
	
	protected void log(String message) {
		logger.log(Level.INFO, myId + ": " + message);
	}
}
