package jadex.bridge.service.types.dht;

import java.util.List;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

/**
 * Service that provides functionality for a ring-structured overlay network. 
 */
@Reference
public interface IRingNodeService extends IRingApplicationService
{

//	public static int TIMEOUT=2112;
	
	/**
	 * Return the finger that preceeds the given ID and is closest to it in the
	 * local finger table.
	 * 
	 * @param key the ID
	 * @return {@link IFinger} The finger that is closest preceeding the given
	 *         key.
	 */
//	@Timeout(TIMEOUT)
	IFuture<IFinger> getClosestPrecedingFinger(IID id);

	/**
	 * Return the CID of the provider of this Service.
	 * 
	 * @return CID.
	 */
//	@Timeout(TIMEOUT)
//	IFuture<IComponentIdentifier> getCID();

	/**
	 * Return the predecessor of this node.
	 * 
	 * @return finger entry of the predecessor.
	 */
	@Timeout(TIMEOUT)
	IFuture<IFinger> getPredecessor();

	/**
	 * Set the predecessor of this node.
	 * 
	 * @param predecessor Finger entry of the new predecessor.
	 */
	@Timeout(TIMEOUT)
	IFuture<Void> setPredecessor(IFinger predecessor);

	/**
	 * Notifies this node about a possible new predecessor.
	 * 
	 * @param nDash possible new predecessor
	 */
//	@Timeout(TIMEOUT)
	IFuture<Void> notify(IFinger ringNode);

	/**
	 * Notifies this node about a possible new predecessor.
	 * 
	 * @param nDash possible new predecessor
	 */
	@Timeout(TIMEOUT)
	IFuture<Void> notifyBad(IFinger x);
	
	/**
	 * Returns a List of all fingers.
	 */
	@Timeout(TIMEOUT)
	IFuture<List<IFinger>> getFingers();

	/**
	 * Returns the overlay id this node operates in.
	 * @return String
	 */
	String getOverlayId();

}
