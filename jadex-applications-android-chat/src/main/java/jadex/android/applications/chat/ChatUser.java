package jadex.android.applications.chat;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.DefaultResultListener;
import jadex.platform.service.chat.ChatService;

public class ChatUser
{

	private String nickName;
	private IServiceIdentifier sid;
	
	private static final String DEFAULT_NICK="<loading nickname>";
	
//	public ChatUser(IChatService chatService, IServiceIdentifier sid) {
//		this.nickName = DEFAULT_NICK;
//		this.sid = sid;
//		chatService.getNickName().addResultListener(new DefaultResultListener<String>()
//		{
//
//			@Override
//			public void resultAvailable(String result)
//			{
//				nickName= result;
//			}
//		});
//	}

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
