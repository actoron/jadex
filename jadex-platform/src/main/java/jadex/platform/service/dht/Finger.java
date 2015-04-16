package jadex.platform.service.dht;

import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.DefaultResultListener;


public class Finger implements IFinger
{
	
	private IRingNode node;
	private IID nodeId;
	private IID start;
	
	public Finger() {
		
	}
	
	public Finger(IRingNode ringNode, IID start)
	{
		this.node = ringNode;
		this.start = start;
	}
	
	@Override
	public IID getStart()
	{
		return start;
	}

	@Override
	public synchronized IID getNodeID()
	{
		if (nodeId != null) {
			return nodeId;
		} else {
			IID iid = node.getId().get();
			nodeId = iid;
			return iid;
		}
	}
	
	public IRingNode getNode()
	{
		return node;
	}

	public synchronized void setNode(IRingNode node)
	{
		this.node = node;
		this.nodeId = null;
		node.getId().addResultListener(new DefaultResultListener<IID>()
		{

			@Override
			public void resultAvailable(IID result)
			{
				synchronized(this) {
					nodeId = result;
				}
			}
		});
	}
	
	public synchronized void setNodeAndId(IRingNode node, IID nodeId) {
		this.node = node;
		this.nodeId = nodeId;
	}

	public void setStart(IID start)
	{
		this.start = start;
	}

	@Override
	public String toString()
	{
		return "start: " + start + ", node: " + node.getId().get();
	}
	
}
