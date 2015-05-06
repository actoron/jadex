package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IKVStore;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.Tuple;
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

@Service
public class KVStore implements IKVStore
{
	private Map<String, StoreEntry>	kvmap;
	
	private IRingNode ring;
	
	@ServiceComponent
	private IInternalAccess agent;

	private Logger	logger;

	private IComponentIdentifier	myCid;
	private IID	myId;
	
	class StoreEntry extends Tuple2<IID,String> {

		private static final long serialVersionUID = 1L;

		public StoreEntry(IID hash, String value) {
			super(hash, value);
		}
		
		public IID getIdHash() {
			return getFirstEntity();
		}
		
		public String getValue() {
			return getSecondEntity();
		}
	}

	public KVStore()
	{
		this.kvmap = new HashMap<String, StoreEntry>();
		this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	public void setRing(IRingNode ring)
	{
		this.ring = ring;
		System.out.println("store: set ring");
		myCid = ring.getCID().get();
		myId = ring.getId().get();
		System.out.println("store: done set ring");
	}

	@Override
	public IFuture<IID> publish(final String key, final String value)
	{
		final Future<IID> ret = new Future<IID>();
		ring.findSuccessor(ID.get(key)).addResultListener(new DefaultResultListener<IFinger>()
		{

			@Override
			public void resultAvailable(IFinger result)
			{
				IComponentIdentifier providerId = result.getSid().getProviderId();
				if (providerId.equals(myCid)) {
					// use local access
					storeLocal(key, value).addResultListener(new DelegationResultListener<IID>(ret));
				} else {
					// search for remote kvstore service
					IFuture<IKVStore> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IKVStore.class, providerId.getParent());
					searchService.addResultListener(new DefaultResultListener<IKVStore>()
					{
						@Override
						public void resultAvailable(IKVStore result)
						{
							logger.log(Level.INFO, myId + ": Storing key: " + key + "(hash: " + ID.get(key) + ")" + " in: " + result);
							IFuture<IID> publish = result.storeLocal(key, value);
							publish.addResultListener(new DelegationResultListener<IID>(ret));
						}
					});
				}
			}
		});
		return ret;
	}
	
	public IFuture<IID> storeLocal(String key, String value) {
		if (!isResponsibleFor(ID.get(key))) {
			logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + ID.get(key) + ". My successor is " + ring.getSuccessor().get().getNodeId());
		}
		logger.log(Level.INFO, myId + ": Storing key: " + key + "(hash: " + ID.get(key) +")" + " locally.");
		kvmap.put(key, new StoreEntry(ID.get(key), value));
		return ring.getId();
	}

	
	@Override
	public IFuture<String> lookup(String key) {
		return lookup(key, ID.get(key));
	}
	
	@Override
	public IFuture<IID> lookupResponsibleStore(String key) {
		final Future<IID> ret = new Future<IID>();
		final IExecutionFeature execFeature = agent.getComponentFeature(IExecutionFeature.class);
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

	@Override
	public IFuture<String> lookup(final String key, final IID idHash)
	{
		final Future<String> ret = new Future<String>();
		final IExecutionFeature execFeature = agent.getComponentFeature(IExecutionFeature.class);
		ring.findSuccessor(idHash).addResultListener(new DefaultResultListener<IFinger>()
		{

			@Override
			public void resultAvailable(IFinger result)
			{
				logger.log(Level.INFO, myId + ": retrieving key: + " +key+" (hash: " + idHash + ")");
				final IComponentIdentifier providerId = result.getSid().getProviderId();
				execFeature.scheduleStep(new IComponentStep<String>()
				{

					@Override
					public IFuture<String> execute(IInternalAccess ia)
					{
						final Future<String> ret = new Future<String>();
						if (providerId.equals(myCid)) {
							// use local access
							System.out.println(myId + ": retrieving from local map: "  +key+ " (hash: " + idHash +")");
							if (!isResponsibleFor(idHash)) {
								logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + idHash + ". My successor is " + ring.getSuccessor().get().getNodeId());
							}
							StoreEntry storeEntry = kvmap.get(key);
							if (storeEntry != null) {
								ret.setResult(storeEntry.getValue());
							} else {
								ret.setResult(null);
							}
						} else {
							// search for remote kvstore service
							System.out.println(myId + ": retrieving from remote: " + " (hash: " + idHash +")");
							IFuture<IKVStore> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IKVStore.class, providerId.getParent());
							searchService.addResultListener(new DefaultResultListener<IKVStore>()
							{
								@Override
								public void resultAvailable(IKVStore result)
								{
//												System.out.println("Found remote kvstore");
									IFuture<String> string = result.lookup(key, idHash);
//												System.out.println("adding listener");
									string.addResultListener(new DefaultResultListener<String>()
									{

										@Override
										public void resultAvailable(String result)
										{
//														System.out.println("got result: " + result);
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
//									System.out.println("got result #2: " + result);
						ret.setResult(result);
					}
				});
			}
		});
		return ret;
	}
	
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Future<Set<String>> getStoredKeys() {
		Collection<String> values = kvmap.keySet();
		HashSet<String> hashSet = new HashSet<String>();
		for (String entry : values) {
			hashSet.add(entry);
		}
		return new Future<Set<String>>(hashSet);
	}

	private boolean isResponsibleFor(IID hash)
	{
		IFinger suc = ring.getSuccessor().get();
		return (suc == null) ? true : (myId.isInInterval(hash, suc.getNodeId(), true, false));
	}

	@Override
	public IFuture<IRingNode> getRingNode()
	{
		return new Future<IRingNode>(ring);
	}

}
