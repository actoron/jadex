package jadex.android.exampleproject.extended.agent;

import jadex.commons.future.IFuture;

public interface IAgentInterface
{
	void callAgent(String message);
	
	IFuture<String> getString();
}
