package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

@Reference
public interface IDebugRingNode extends IRingNode
{

//	public static int TIMEOUT=20000;
	
	public IFuture<Boolean> join(IRingNode other);

	public IFuture<String> getFingerTableString();

	IFuture<Void> fixFingers();
	
//	@Timeout(TIMEOUT)
	IFuture<Void> stabilize();
	
	void disableSchedules();
}
