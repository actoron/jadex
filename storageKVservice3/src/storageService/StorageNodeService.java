package storageService;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.PojoMicroAgent;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author frederik
 * 
 */
@Service
public class StorageNodeService implements IStorageNodeService {

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;

	/**
	 * Init the service.
	 */
	@ServiceStart
	public IFuture<Void> startService() {
		final Future<Void> ret = new Future<Void>();
		ret.setResult(null);
		System.out.println("## StorageNodeService started: "
				+ agent.getComponentIdentifier().getName());
		// find other StorageNodeServices and exchange data
//		synchronizeData();
//		test_getKeyVersionMap();
//		test_getKeyVersionMapNonFut();
//		test_getDB();
		return ret;
	}
	
	/**
	 * Shut the service down.
	 */
	@ServiceShutdown
	public void shutdownService() {
		System.out.println("## StorageNodeService shutdown: "
				+ agent.getComponentIdentifier().getName());
	}
	
	/**
	 * 
	 * @param key
	 * @param version
	 * @param value
	 * @return 
	 */
	@Override
	public IFuture<Boolean> receiveUpdate(String key, Version version, Object value) {
		System.out.println(getId() + ": receiveUpdate(" + key + ", " + version + ")");
		return getAgent().writeLocal(key, version, value);
	}

	@Override
	public IFuture<List<KeyVersionPair>> getKeyVersionPairs() {
		return getAgent().getKeyVersionPairs();
	}

	@Override
	public IFuture<List<VersionValuePair>> get(String key) {
		return getAgent().read(key);
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public IFuture<List<DBEntry>> requestUpdates(List<KeyVersionPair> list) {
		System.err.println("## Feature not implemented");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFuture<Object> requestUpdate(KeyVersionPair keyVersionPair) {
		return getAgent().read(keyVersionPair.getKey(), keyVersionPair.getVersion());
	}
	
//	private void synchronizeData() {
//		IFuture<IStorageNodeService> seed = getSeed();
//		seed.addResultListener(new DefaultResultListener<IStorageNodeService>() {
//
//			@Override
//			public void resultAvailable(final IStorageNodeService node) {
//				if(node == null) {
//					System.out.println("no seed found");
//					return;
//				}
//				System.out.println("seed: " + node.toString());
//				final Map<String, Version> localMap = getAgent().getKeyVersionMapNonFut();
//				
//				
//				IFuture<List<KeyVersionPair>> seedList = node.getKeyVersionPairs();
//				seedList.addResultListener(new DefaultResultListener<List<KeyVersionPair>>() {
//
//					@Override
//					public void resultAvailable(List<KeyVersionPair> result) {
//						// TODO Problem: alle elemente sind "null"
//						Iterator<KeyVersionPair> it = result.iterator();
//						while(it.hasNext()) {
//							KeyVersionPair kvp = it.next();
//							
//							if(kvp == null) return;
//							
//							final String key = kvp.getKey();
//							final Version version = kvp.getVersion();
//							if(! localMap.containsKey(key)) {
//								IFuture<Object> valueFut = node.requestUpdate(kvp);
//								valueFut.addResultListener(new DefaultResultListener<Object>() {
//
//									@Override
//									public void resultAvailable(Object value) {
//										getAgent().writeLocal(key, version, value);
//									}
//								});
//							} else {
//								//TODO complete this
//								Version localVersion = localMap.get(key);	
//							}
//						}
//					}
//				});
//				
//			}
//		}); 
//	}
	

//	private void sendUpdateToAll(final String key, final Version version, final Object value) {
//		IIntermediateFuture<IStorageNodeService> fut = agent
//				.getServiceContainer().getRequiredServices("nodeServices");
//		fut.addResultListener(new IIntermediateResultListener<IStorageNodeService>() {
//
//			@Override
//			public void exceptionOccurred(Exception exception) {
//				System.err.println("sendUpdateToAll Exception: " + exception.toString());
//			}
//
//			@Override
//			public void resultAvailable(Collection<IStorageNodeService> result) {
//				System.out.println("sendUpdateToAll resultAvailable");
//			}
//
//			@Override
//			public void intermediateResultAvailable(IStorageNodeService result) {
//				System.out.println("sendUpdateToAll intermediateResultAvailable");
//				if(result.equals(this)) {
//					System.out.println("sendUpdateToAll found own service");
//				} else {
//					System.out.println("sendUpdateToAll found other service");
//					result.receiveUpdate(key, version, value);
//				}
//			}
//
//			@Override
//			public void finished() {
//				System.out.println("sendUpdateToAll finished");
//			}
//			
//		});
//	}
	
	private StorageAgent getAgent() {
		return (StorageAgent) ((PojoMicroAgent)agent).getPojoAgent();
	}

//	private boolean isLocalService(IStorageNodeService service) {
//		return (((IService) service).getServiceIdentifier().getProviderId()).equals(
//				getId());
//	}
	
//	/*
//	 * return Future with node from other component or null if none could be found
//	 */
//	private IFuture<IStorageNodeService> getSeed() {
//		final Future<IStorageNodeService> ret = new Future<IStorageNodeService>();
//		
//		IIntermediateFuture<IStorageNodeService> fut = agent
//				.getServiceContainer().getRequiredServices("nodeServices");
//		 fut.addResultListener(new IIntermediateResultListener<IStorageNodeService>() {
//
//			@Override
//			public void exceptionOccurred(Exception exception) {
//				System.err.println("getSeed Exception: " + exception.toString());
//			}
//
//			@Override
//			public void resultAvailable(Collection<IStorageNodeService> result) {
//				// is not called!!!
//				System.out.println("getSeed resultAvailable");
//				Iterator<IStorageNodeService> it = result.iterator();
//				boolean noneFound = true;
//				while(noneFound && it.hasNext()) {
//					IStorageNodeService node = it.next();
//					if(! isLocalService(node)) {
//						noneFound = false;
//						ret.setResult(node);
//					}
//				}
//				if(noneFound) {
//					ret.setResult(null);
//				}
//			}
//
//			@Override
//			public void intermediateResultAvailable(IStorageNodeService node) {
//				System.out.println("getSeed intermediateResultAvailable");
//				if(! isLocalService(node)) {
//					System.out.println("getSeed: found seed: " + node.toString());
//					ret.setResult(node);
//					finished();
//				}
//			}
//
//			@Override
//			public void finished() {
//				System.out.println("getSeed finished");
//			}
//			 
//		 });
//		 return ret;
//	}
	
	public IComponentIdentifier getId() {
		IComponentIdentifier id = this.agent.getComponentIdentifier();
		return id;
	}
	
	@Override
	public IFuture<Boolean> ping() {
		return getAgent().rebindNodeServices();
	}

	@Override
	public IFuture<Map<String, List<Version>>> getKeyVersionsMap() {
		
		return new Future<Map<String, List<Version>>>(getAgent().getKeyVersionsMapNonFut());
	}
	
	
	
//	/////////////
//	// test agent
//	/////////////
//	public void test_getKeyVersionMap() {
//		System.out.println("test_getKeyVersionMap");
//		IFuture<Map<String, Version>> fut = getAgent().getKeyVersionMap();
//		fut.addResultListener(new DefaultResultListener<Map<String, Version>>() {
//
//			@Override
//			public void resultAvailable(Map<String, Version> result) {
//				Iterator<String> it = result.keySet().iterator();
//				while(it.hasNext()) {
//					String key = it.next();
//					if(key == null) {
//						System.out.println("test_getKeyVersionMap: null");
//					}
//					else {
//						System.out.println("test_getKeyVersionMap: " + key + ": " + result.get(key));
//					}
//				}
//			}
//		});
//	}
//	
//	public void test_getKeyVersionMapNonFut() {
//		System.out.println("test_getKeyVersionMapNonFut");
//		Map<String, Version> map = getAgent().getKeyVersionMapNonFut();
//		Iterator<String> it = map.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			if (key == null) {
//				System.out.println("test_getKeyVersionMapNonFut: null");
//			} else {
//				System.out.println("test_getKeyVersionMapNonFut: " +key + ": " + map.get(key));
//			}
//		}
//	}
//
//	public void test_getDB() {
//		System.out.println("test_getDB");
//		IFuture<List<DBEntry>> fut = getAgent().getDB();
//		fut.addResultListener(new DefaultResultListener<List<DBEntry>>() {
//
//			@Override
//			public void resultAvailable(List<DBEntry> result) {
//				Iterator<DBEntry> it = result.iterator();
//				while (it.hasNext()) {
//					DBEntry entry = it.next();
//					if (entry == null) {
//						System.out.println("test_getDB: null");
//					} else {
//						System.out.println("test_getDB: " + entry.toString());
//					}
//				}
//			}
//		});
//	}


}
