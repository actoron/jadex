package jadex.platform.service.dht;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.DefaultResultListener;


public class Finger implements IFinger
{
	/** DEBUG field **/
	public static IID	killedId;
	
	private IServiceIdentifier sid;
	private IID nodeId;
	private IID start;
	
	public Finger() {
	}
	
	public Finger(IServiceIdentifier sid, IID start, IID nodeId)
	{
		this.sid = sid;
		this.start = start;
		this.nodeId = nodeId;
	}
	
	public Finger(IRingNode ringNode, IID start)
	{
		this.start = start;
		this.sid = ((IService) ringNode).getServiceIdentifier();
		this.nodeId = ringNode.getId().get();
	}
	
	public IID getStart()
	{
		return start;
	}
	
	public void setStart(IID start)
	{
		this.start = start;
	}
	
	public void setSid(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	public IServiceIdentifier getSid()
	{
		return sid;
	}
	
	public IID getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(IID nodeId)
	{
		this.nodeId = nodeId;
	}

//	public void set(IID nodeId, IServiceIdentifier sid) // TODO pass exception when node is down
//	{
//		this.nodeId = nodeId;
//		this.sid = sid;
//	}
	
	public void set(IFinger other) {
//		System.out.println("Setting " + this.getNodeId() + " to : " + other.getNodeId());
		this.nodeId = other.getNodeId();
		this.sid = other.getSid();
//		if (killedId != null && killedId.equals(nodeId)) {
//			throw new RuntimeException("re-setting killed id to table :(");
//		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Finger other = (Finger)obj;
		if(sid == null)
		{
			if(other.sid != null)
				return false;
		}
		else if(!sid.equals(other.sid))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "start: " + start + ", node: " + nodeId + ", sid: " + sid;
	}
	
}
