package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Exclude;

import java.util.Collection;
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
	public IFuture<IID> put(String key, Object value);
	
	/**
	 * Add a key/value pair to the collection addressed by the given key.
	 * 
	 * @param key The Key.
	 * @param value The Value to add.
	 * @return The ID of the node this key was saved in.
	 */
	public IFuture<IID> add(String key, Object value);
	
	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param key The key
	 * @param value The value
	 * @return the ID of the local node.
	 */
//	public IFuture<IID> putLocal(String key, Object value);
	
	/**
	 * Store a key/value pair in the local map.
	 * 
	 * @param key The key
	 * @param value The value
	 * @return the ID of the local node.
	 */
//	public IFuture<IID> addLocal(String key, Object value);
	
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
	public IFuture<?> lookup(String key);
	
	/**
	 * Lookup a key in the ring and return the saved value, if any.
	 * 
	 * @param key Requested key.
	 * @param idHash The hashed key to find the corresponding node.
	 * @return The retrieved value or null, if none.
	 */
	public IFuture<?> lookup(String key, IID idHash);

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
//	public IFuture<IRingApplicationService> getRingService();

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
	public IFuture<Collection<StoreEntry>> pullEntries(IID targetNodeId);
	
	/**
	 * Receive entries from another node and insert them in the local store
	 * or in the store of a predecessor, if key is matching.
	 * @param entries the entries
	 * @return void
	 */
	public IFuture<Void> pushEntries(Collection<StoreEntry> entries);

	/**
	 * Sets the initialized flag.
	 */
	@Excluded
	public void setInitialized(boolean b);
	
	/**
	 * Gets the initialized flag.
	 */
	public boolean isInitialized();
	
}
