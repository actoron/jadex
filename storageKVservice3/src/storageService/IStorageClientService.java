package storageService;

import jadex.commons.future.IFuture;

import java.util.List;

import storageService.resolve.Resolver;


public interface IStorageClientService {

	/**
	 * Write an object. Node will propagate update asynchronously
	 * to other nodes. Return true guarantees successful write on at least one node.
	 * 
	 * @param String key
	 * @param Object value must comply with java beans standard
	 * @return IFuture<Boolean> signals whether write was successful
	 */
	public IFuture<Boolean> writeAny(final String key, final Object value);

	/**
	 * Write an object on all available nodes at once.
	 * Return true guarantees successful completion on all available nodes. Return false signifies
	 * unknown state, i.e. write may have succeeded on some nodes and failed on others.
	 * 
	 * @param String key
	 * @param Object value must comply with java beans standard
	 * @return IFuture<Boolean> signals whether write was successful
	 */
	public IFuture<Boolean> writeAll(final String key, final Object value);
	
	/**
	 * Get list of stored objects (version and value) for a key.
	 * 
	 * @param String key
	 * @return IFuture<VersionValuePair>
	 */
	public IFuture<List<VersionValuePair>> read(final String key);
	
	public IFuture<VersionValuePair> readValue(final String key, Iterable<Resolver> resolver);
	
	/**
	 * Get list of stored objects (version and value) for a key, that are stored on all nodes.
	 * 
	 * @param String key
	 * @return IFuture<VersionValuePair>
	 */
	public IFuture<List<VersionValuePair>> readAll(final String key);
	
	/**
	 * Update an object. Node will propagate update asynchronously
	 * to other nodes. Version must be specified.
	 * @param String key
	 * @param Object value must comply with java beans standard
	 * 
	 * @return IFuture<Boolean> signals whether write was successful
	 */
	public IFuture<Boolean> updateAny(final String key, final Object newValue, final Version versionToUpdate);


	/**
	 * get a list containing all keys
	 * 
	 */
	public IFuture<List<String>> getKeys();

	/**
	 * get a list containing complete DB
	 * 
	 */
	public IFuture<List<DBEntry>> getDB();

//	//
//	// test 
//	public void test_getKeyVersionMap();
//	public void test_getKeyVersionMapNonFut();
//	public void test_getDB();
}
