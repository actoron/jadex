package jadex.android.applications.chat;

import jadex.bridge.IComponentIdentifier;

public class ChatUser
{

	private String nickName;
	private IComponentIdentifier cid;
	private String status = DEFAULT_STATUS;
	
	private static final String DEFAULT_STATUS="<loading status>";
	
	public ChatUser(String nickName, IComponentIdentifier cid)
	{
		this(nickName, null, cid);
	}
	
	public ChatUser(String nickName, String status, IComponentIdentifier cid) {
		this.nickName = nickName;
		this.cid = cid;
		this.status = status;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public IComponentIdentifier getCid()
	{
		return cid;
	}

	public void setCid(IComponentIdentifier cid)
	{
		this.cid = cid;
	}

	@Override
	public String toString()
	{
		return nickName + "[" + cid + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
