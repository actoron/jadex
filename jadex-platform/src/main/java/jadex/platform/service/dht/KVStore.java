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
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class KVStore implements IKVStore
{
	private Map<ID, String>	kvmap;
	
	private IRingNode ring;
	
	@ServiceComponent
	private IInternalAccess agent;

	private Logger	logger;

	private IComponentIdentifier	myCid;
	private IID	myId;

	public KVStore()
	{
		this.kvmap = new HashMap<ID, String>();
		this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	@ServiceStart
	public void onServiceStart() {
//		System.out.println("agent is set to: " + agent);
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
		if (!isResponsibleFor(key)) {
			logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + ID.get(key) + ". My successor is " + ring.getSuccessor().get().getNodeId());
		}
		logger.log(Level.INFO, myId + ": Storing key: " + key + "(hash: " + ID.get(key) +")" + " locally.");
		kvmap.put(ID.get(key), value);
		return ring.getId();
	}


	@Override
	public IFuture<String> lookup(final String key)
	{
		final Future<String> ret = new Future<String>();
		final IExecutionFeature execFeature = agent.getComponentFeature(IExecutionFeature.class);
		ring.findSuccessor(ID.get(key)).addResultListener(new DefaultResultListener<IFinger>()
		{

			@Override
			public void resultAvailable(IFinger result)
			{
				logger.log(Level.INFO, myId + ": retrieving key: " + key + "(hash: " + ID.get(key) + ")");
				final IComponentIdentifier providerId = result.getSid().getProviderId();
				execFeature.scheduleStep(new IComponentStep<String>()
				{

					@Override
					public IFuture<String> execute(IInternalAccess ia)
					{
						final Future<String> ret = new Future<String>();
						if (providerId.equals(myCid)) {
							// use local access
							System.out.println(myId + ": retrieving from local map: " + " (hash: " + ID.get(key) +")");
							if (!isResponsibleFor(key)) {
								logger.log(Level.WARNING, myId + ": storeLocal called even if i do not feel responsible for: " + ID.get(key) + ". My successor is " + ring.getSuccessor().get().getNodeId());
							}
							ret.setResult(kvmap.get(ID.get(key)));
//									storeLocal(key, value).addResultListener(new DelegationResultListener<ID>(ret));
						} else {
							// search for remote kvstore service
							System.out.println(myId + ": retrieving from remote: " + " (hash: " + ID.get(key) +")");
							IFuture<IKVStore> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IKVStore.class, providerId.getParent());
							searchService.addResultListener(new DefaultResultListener<IKVStore>()
							{
								@Override
								public void resultAvailable(IKVStore result)
								{
//												System.out.println("Found remote kvstore");
									IFuture<String> string = result.lookup(key);
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
	

	private boolean isResponsibleFor(String key)
	{
		IFinger suc = ring.getSuccessor().get();
		return (suc == null) ? true : (myId.isInInterval(ID.get(key), suc.getNodeId(), true, false));
	}

	@Override
	public IFuture<IRingNode> getRingNode()
	{
		return new Future<IRingNode>(ring);
	}

}
