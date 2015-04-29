package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Exclude;

@Reference
public interface IKVStore
{
	public IFuture<IID> publish(String key, String value);
	
	public IFuture<String> lookup(String key);

	@Excluded
	@Exclude
	public void setRing(IRingNode ring);

	public IFuture<IID> storeLocal(String key, String value);
	
	public IFuture<IRingNode> getRingNode();
	
//	public IFuture<IKVStore> join();
}
