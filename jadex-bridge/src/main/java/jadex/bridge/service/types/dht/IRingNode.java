package jadex.bridge.service.types.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

import java.util.List;

@Reference
public interface IRingNode
{

	public static int TIMEOUT=2112;
	
	
	@Timeout(TIMEOUT)
	IFuture<IFinger> getSuccessor();

	@Timeout(TIMEOUT)
	IFuture<IFinger> getClosestPrecedingFinger(IID id);

	@Timeout(TIMEOUT)
	IFuture<IFinger> findSuccessor(IID id);
	
	@Timeout(TIMEOUT)
	IFuture<IComponentIdentifier> getCID();

	@Timeout(TIMEOUT)
	IFuture<IFinger> getPredecessor();

	@Timeout(TIMEOUT)
	IFuture<Void> setPredecessor(IFinger predecessor);

	@Timeout(TIMEOUT)
	IFuture<IID> getId();

	@Timeout(TIMEOUT)
	IFuture<Void> notify(IFinger ringNode);

	@Timeout(TIMEOUT)
	IFuture<Void> notifyBad(IFinger x);
	
	@Timeout(TIMEOUT)
	IFuture<List<IFinger>> getFingers();

	

}
