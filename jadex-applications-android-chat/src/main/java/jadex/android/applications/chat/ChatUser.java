package jadex.android.applications.chat;

import jadex.bridge.service.IServiceIdentifier;

public class ChatUser
{

	private String nickName;
	private IServiceIdentifier sid;

	public ChatUser(String nickName, IServiceIdentifier sid)
	{
		this.nickName = nickName;
		this.sid = sid;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public IServiceIdentifier getSid()
	{
		return sid;
	}

	public void setSid(IServiceIdentifier sid)
	{
		this.sid = sid;
	}

	@Override
	public String toString()
	{
		return nickName + "[" + sid + "]";
	}
}
