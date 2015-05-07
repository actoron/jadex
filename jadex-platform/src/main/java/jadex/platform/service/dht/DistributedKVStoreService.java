package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that allows storing of key/value pairs in a DHT ring.
 */
@Service
public class DistributedKVStoreService implements IDistributedKVStoreService
{
	/** Map that stores the actual data. Key -> StoreEntry **/
	protected Map<String, StoreEntry>	kvmap;
	
	/** The local Ring Node  to access the DHT Ring. **/
	protected IRingApplicationService ring;
	/** The local CID **/
//	protected  IComponentIdentifier	myCid;
	/** The local ID **/
	protected  IID	myId;
	
	/** The local agent access. **/
	@ServiceComponent
	protected IInternalAccess agent;

	/** The logger. **/
	protected  Logger	logger;

	/**
	 * Constructor.
	 */
	public DistributedKVStoreService()
	{
		this.kvmap = new HashMap<String, StoreEntry>();
		this.logger = Logger.getLogger(this.getClass().getName());
	}

	/**
	 * Set the local ringNode.
	 * 
	 * @param ring the new ringNode
	 */
	public void setRingService(IRingApplicationService ring)
	{
		this.ring = ring;
		// myCid = ring.getCID().get();
		myId = ring.getId().get();
	}

//	/**
//	 * Get the local ringNode.
//	 */
//	public IFuture<IRingNodeService> getRing() {
//		return new Future<IRingNodeService>(ring);
//	}

	/**
	 * Publish a key/value pair in the corresponding node.
	 * 
	 * @param key The Key.
	 * @param value The Value.
	 * @return The ID of the node this key was saved in.
	 */
	public IFuture<IID> publish(final String key, final String value)
	{
		final Future<IID> ret = new Future<IID>();
		
		ring.findSuccessor(ID.get(key)).addResultListener(new DefaultResultListener<IFinger>()
		{

			@Override
			public void resultAvailable(IFinger result)
			{
				IComponentIdentifier providerId = result.getSid().getProviderId();
				IID nodeId = result.getNodeId();
				// if (providerId.equals(myCid)) {
				if(nodeId.equals(myId))
				{
					// use local access
					storeLocal(key, value).addResultListener(new DelegationResultListener<IID>(ret));
				}
				else
				{
					// search for remote kvstore service. This assumes every component providing a ring service also
					// provides a KVStore service...
					IFuture<IDistributedKVStoreService> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IDistributedKVStoreService.class,
						providerId.getParent());
					searchService.addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
					{
						@Override
						public void resultAvailable(IDistributedKVStoreService result)
						{
							logger.log(Level.INFO, myId + ": Storing key: " + key + "(hash: " + ID.get(key) + ")" + " in: " + result);
							IFuture<IID> publish = result.storeLocal(key, value);
//							IFuture<IID> publish = result.publish(key, value);
							publish.addResultListener(new DelegationResultListener<IID>(ret));
						}
					});
				}
			}
		});
		return ret;
	}
	
	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param key The key
	 * @param value The value
	 * @return the ID of the local node.
	 */
	public IFuture<IID> storeLocal(String key, String value) {
		if (!isResponsibleFor(ID.get(key))) {
			logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + ID.get(key) + ". My successor is " + ring.getSuccessor().get().getNodeId());
		}
		logger.log(Level.INFO, myId + ": Storing key: " + key + "(hash: " + ID.get(key) +")" + " locally.");
		kvmap.put(key, new StoreEntry(ID.get(key), value));
		return ring.getId();
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
		ring.findSuccessor(ID.get(key)).addResultListener(new ExceptionDelegationResultListener<IFinger, IID>(ret)
		{

			@Override
			public void customResultAvailable(IFinger result) {
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
	public IFuture<String> lookup(String key) {
		return lookup(key, ID.get(key));
	}

	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @param idHash The hashed key to find the corresponding node.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<String> lookup(final String key, final IID idHash)
	{
		final Future<String> ret = new Future<String>();
		final IExecutionFeature execFeature = agent.getComponentFeature(IExecutionFeature.class);
		ring.findSuccessor(idHash).addResultListener(new DefaultResultListener<IFinger>()
		{

			@Override
			public void resultAvailable(IFinger result)
			{
				logger.log(Level.INFO, myId + ": retrieving key: " +key+" (hash: " + idHash + ") from successor: " + result.getNodeId());
				final IComponentIdentifier providerId = result.getSid().getProviderId();
				final IID nodeId = result.getNodeId();
				execFeature.scheduleStep(new IComponentStep<String>()
				{

					@Override
					public IFuture<String> execute(IInternalAccess ia)
					{
						final Future<String> ret = new Future<String>();
//						if (providerId.equals(myCid)) {
						if(nodeId.equals(myId))
						{
							// use local access

							logger.info(myId + ": retrieving from local map: "  +key+ " (hash: " + idHash +")");
							if(!isResponsibleFor(idHash))
							{
								logger.log(Level.WARNING, myId + ": lookupLocal called even if i do not feel responsible for: " + idHash + ". My successor is " + ring.getSuccessor().get().getNodeId());
							}
							StoreEntry storeEntry = kvmap.get(key);
							if(storeEntry != null)
							{
								ret.setResult(storeEntry.getValue());
							}
							else
							{
								ret.setResult(null);
							}
						}
						else
						{
							// search for remote kvstore service
//							System.out.println(myId + ": retrieving from remote: " + " (hash: " + idHash +")");
							IFuture<IDistributedKVStoreService> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IDistributedKVStoreService.class,
								providerId.getParent());
							searchService.addResultListener(new DefaultResultListener<IDistributedKVStoreService>()
							{
								@Override
								public void resultAvailable(IDistributedKVStoreService result)
								{
									IFuture<String> string = result.lookup(key, idHash);
									string.addResultListener(new DefaultResultListener<String>()
									{

										@Override
										public void resultAvailable(String result)
										{
											ret.setResult(result);
										}
									});
								}
							});
						}
						return ret;
					}
				}).addResultListener(new DefaultResultListener<String>()
				{

					@Override
					public void resultAvailable(String result)
					{
						ret.setResult(result);
					}
				});
			}
		});
		return ret;
	}

	/**
	 * Returns all keys stored in this node.
	 * 
	 * @return Set of Keys.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Future<Set<String>> getLocalKeySet()
	{
		Collection<String> values = kvmap.keySet();
		HashSet<String> hashSet = new HashSet<String>();
		for(String entry : values)
		{
			hashSet.add(entry);
		}
		return new Future<Set<String>>(hashSet);
	}

	/**
	 * Checks whether this store service is responsible for saving/retrieving a
	 * key with the given hash value.
	 * 
	 * @param hash
	 * @return true, if this store serviceis responsible, else false.
	 */
	private boolean isResponsibleFor(IID hash)
	{
		IFinger suc = ring.getSuccessor().get();
		return (suc == null) ? true : (myId.isInInterval(hash, suc.getNodeId(), true, false));
	}

	/**
	 * Returns the local ring node.
	 * 
	 * @return The local ringnode.
	 */
	public IFuture<IRingApplicationService> getRingService()
	{
		return new Future<IRingApplicationService>(ring);
	}
	
	/**
	 * Entry in the storage map containing ID (hash) and value.
	 */
	static class StoreEntry extends Tuple2<IID, String>
	{

		private static final long	serialVersionUID	= 1L;

		/**
		 * Constructor.
		 * 
		 * @param hash
		 * @param value
		 */
		public StoreEntry(IID hash, String value)
		{
			super(hash, value);
		}

		/**
		 * Get the hash.
		 * 
		 * @return
		 */
		public IID getIdHash()
		{
			return getFirstEntity();
		}

		/**
		 * Get the value.
		 * 
		 * @return
		 */
		public String getValue()
		{
			return getSecondEntity();
		}
	}
}
