package storageService;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.PojoMicroAgent;

import java.util.Iterator;
import java.util.List;

import storageService.resolve.Resolver;

/**
 * 
 * @author frederik
 *
 */
@Service
public class StorageClientService implements IStorageClientService {

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;

	@Override
	public IFuture<Boolean> writeAny(String key, Object value) {
		return getAgent().write(key, value);
	}

	@Override
	public IFuture<Boolean> writeAll(String key, Object value) {
		return getAgent().writeAll(key, value);
	}

	@Override
	public IFuture<List<VersionValuePair>> read(String key) {
		final Future<List<VersionValuePair>> ret = new Future<List<VersionValuePair>>();
		IFuture<List<VersionValuePair>> fut = getAgent().read(key);
		fut.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

			@Override
			public void resultAvailable(List<VersionValuePair> list) {
				Iterator<VersionValuePair> it = list.iterator();
				while(it.hasNext()) {
					VersionValuePair vvp = it.next();
					if(vvp.getValue().equals(new Tombstone())) {
						it.remove();
					}
				}
				ret.setResult(list);
			}
		});
		return ret;
//		return getAgent().read(key);
	}

	@Override
	public IFuture<List<VersionValuePair>> readAll(String key) {
		return getAgent().readAll(key);
	}

	@Override
	public IFuture<Boolean> updateAny(String key, Object newValue, Version versionToUpdate) {
		return getAgent().update(key, newValue, versionToUpdate);
	}

	@Override
	public IFuture<List<String>> getKeys() {
		throw new RuntimeException("Feature not yet implemented");
	}

	@Override
	public IFuture<List<DBEntry>> getDB() {
		return getAgent().getDB();
	}

	private StorageAgent getAgent() {
		return (StorageAgent) ((PojoMicroAgent) agent).getPojoAgent();
	}

//	// ///////////
//	// test 
//	// ///////////
//
//	public void test_getKeyVersionMap() {
//		System.out.println("test_getKeyVersionMap");
//		IFuture<Map<String, Version>> fut = getAgent().getKeyVersionMap();
//		fut.addResultListener(new DefaultResultListener<Map<String, Version>>() {
//
//			@Override
//			public void resultAvailable(Map<String, Version> result) {
//				Iterator<String> it = result.keySet().iterator();
//				while (it.hasNext()) {
//					String key = it.next();
//					if (key == null) {
//						System.out.println("test_getKeyVersionMap: null");
//					} else {
//						System.out.println("test_getKeyVersionMap: " + key
//								+ ": " + result.get(key));
//					}
//				}
//			}
//		});
//	}
//
//	public void test_getKeyVersionsMapNonFut() {
//		System.out.println("test_getKeyVersionMapNonFut");
//		Map<String, Version> map = getAgent().getKeyVersionMapNonFut();
//		Iterator<String> it = map.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			if (key == null) {
//				System.out.println("test_getKeyVersionMapNonFut: null");
//			} else {
//				System.out.println("test_getKeyVersionMapNonFut: " + key + ": "
//						+ map.get(key));
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

	@Override
	public IFuture<VersionValuePair> readValue(final String key,
			final Iterable<Resolver> resolver) {
		final Future<VersionValuePair> ret = new Future<VersionValuePair>();
		IFuture<List<VersionValuePair>> fut = read(key);
		fut.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

			@Override
			public void resultAvailable(List<VersionValuePair> list) {
				if (list.size() == 0) {
					ret.setResult(null);
					return;
				}

				Iterator<Resolver> it = resolver.iterator();
				while (it.hasNext() && (list.size() > 1)) {
					Resolver res = it.next();
					list = res.resolve(list);
				}
				// if after resolving there are still more than one entry, return the first
				ret.setResult(list.get(0));

			}
		});

		return ret;
	}


}
