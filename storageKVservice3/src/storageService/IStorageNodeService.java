package storageService;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

import java.util.List;
import java.util.Map;

public interface IStorageNodeService {

	/**
	 * Receive update and make agent write locally. The data is "pushed" 
	 * from another node.
	 * 
	 * @param key
	 * @param version
	 * @param value
	 */
	public IFuture<Boolean> receiveUpdate(final String key, final Version version,
			final Object value);

	/**
	 * Another node requests updates. The caller node can store returned data locally.
	 * 
	 * @param list
	 * @return
	 */
	public IFuture<List<DBEntry>> requestUpdates(final List<KeyVersionPair> list);

	/**
	 * Another node requests one update. The caller node can store returned data locally.
	 * 
	 * @param keyVersionPair
	 * @return
	 */
	public IFuture<Object> requestUpdate(final KeyVersionPair keyVersionPair);

	/**
	 * getKeyVersionPairs
	 * 
	 * @param list
	 * @return
	 */
	public IFuture<List<KeyVersionPair>> getKeyVersionPairs();
	
	/**
	 * getKeyVersionsMap
	 * 
	 * @param list
	 * @return
	 */
	public IFuture<Map<String,List<Version>>> getKeyVersionsMap();

	/**
	 * Read from other nodes. Used when the other node doesn't have the data or
	 * to ensure higher consistency.
	 * 
	 * @param key
	 * @return
	 */
	public IFuture<List<VersionValuePair>> get(final String key);
	
	public IComponentIdentifier getId();
	
	/**
	 * Signal change in set of nodes.
	 * @return
	 */
	public IFuture<Boolean> ping();
	
//	//
//	// test 
//	public void test_getKeyVersionMap();
//	public void test_getKeyVersionMapNonFut();
//	public void test_getDB();

}
