package jadex.android.application.demo;

import java.util.Map;

import jadex.base.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import android.os.Bundle;
import android.os.Message;

/**
 *  Simple example agent that shows messages
 *  when it is started, stopped and when it receives a message. 
 */
public class AndroidAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Called when the agent is started.
	 */
	public IFuture<Void> executeBody()
	{
		showAndroidMessage("This is Agent <<" + this.getAgentName() + ">> saying hello!");
		return new Future<Void>();
	}

	/**
	 *  Called when the agent is killed.
	 */
	public IFuture<Void> agentKilled()
	{
		showAndroidMessage("This is Agent <<" + this.getAgentName() + ">> saying goodbye!");
		return IFuture.DONE;
	}

	/**
	 *  Called when the agent receives a message.
	 */
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		showAndroidMessage(msg.get(SFipa.PERFORMATIVE)+"("+msg.get(SFipa.CONTENT)+")");
	}
	
	//-------- helper methods --------

	/**
	 *	Show a message on the device.  
	 *  @param msg The message to be shown.
	 */
	protected void showAndroidMessage(String msg)
	{
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("text", msg);
		message.setData(bundle);
		JadexAndroidHelloWorldActivity.getHandler().sendMessage(message);
	}
}
