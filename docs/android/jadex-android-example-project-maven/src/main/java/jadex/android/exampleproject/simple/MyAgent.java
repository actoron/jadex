package jadex.android.exampleproject.simple;

import jadex.android.exampleproject.MyEvent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.AndroidMicroAgent;
import jadex.micro.annotation.Description;

/**
 *  Simple example agent that shows messages
 *  when it is started or stopped.
 */
@Description("Sample Android Agent.")
public class MyAgent extends AndroidMicroAgent
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

	//-------- helper methods --------

	/**
	 *	Show a message on the device.  
	 *  @param msg The message to be shown.
	 */
	protected void showAndroidMessage(String txt)
	{
		MyEvent myEvent = new MyEvent();
		myEvent.setMessage(txt);
		dispatchEvent(myEvent);
	}
}
