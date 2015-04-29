package jadex.bridge.service.types.dht;

import jadex.bridge.service.IServiceIdentifier;

public interface IFinger
{
	public IID getStart();
	public IID getNodeId();
	public IServiceIdentifier getSid();
}
