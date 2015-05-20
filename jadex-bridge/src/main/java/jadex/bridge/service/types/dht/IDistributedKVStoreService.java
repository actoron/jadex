package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Exclude;

import java.util.Set;

/**
 * This Service provides Distributed Key-Value storage using the DHT-based Chord protocol. 
 */
@Reference
public interface IDistributedKVStoreService
{
	/**
	 * Publish a key/value pair in the corresponding node.
	 * 
	 * @param key The Key.
	 * @param value The Value.
	 * @return The ID of the node this key was saved in.
	 */
	public IFuture<IID> publish(String key, String value);
	
	/**
	 * Lookup a key and return the responsible Node ID.
	 * 
	 * @param key Requested key.
	 * @return IID of the responsible node.
	 */
	public IFuture<IID> lookupResponsibleStore(String key);

	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<String> lookup(String key);
	
	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @param idHash The hashed key to find the corresponding node.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<String> lookup(String key, IID idHash);

	/**
	 * Set the local ringNode.
	 * 
	 * @param ring the new ringNode
	 */
	@Excluded
	@Exclude
	public void setRingService(IRingApplicationService ring);
	
	/**
	 * Returns the local ring node.
	 * 
	 * @return The local ringnode.
	 */
	public IFuture<IRingApplicationService> getRingService();

	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param key The key
	 * @param value The value
	 * @return the ID of the local node.
	 */
	public IFuture<IID> storeLocal(String key, String value);
	
	/**
	 * Returns all keys stored in this node.
	 * 
	 * @return Set of Keys.
	 */
	public IFuture<Set<String>> getLocalKeySet();

	/**
	 * Returns all entries that belong to the given node Id
	 * and deletes them on this node.
	 * @param targetNodeId
	 * @return Set of all matching entries.
	 */
	public IFuture<Set<StoreEntry>> moveEntries(IID targetNodeId);
	
}
