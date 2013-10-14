package jadex.android.exampleproject.extended.agent;

import jadex.android.exampleproject.MyEvent;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.AndroidMicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Simple example agent that shows messages
 *  when it is started or stopped. 
 */
@Description("Sample Android Agent.")
@ProvidedServices({
	@ProvidedService(name="agentinterface", type=IAgentInterface.class)
})
@Service
public class MyAgent extends AndroidMicroAgent implements IAgentInterface
{

	
	//-------- methods --------
	/**
	 *  Called when the agent is started.
	 */
	public IFuture<Void> executeBody()
	{
//		showAndroidMessage("This is Agent <<" + this.getAgentName() + ">> saying hello!");
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



	@Override
	public void callAgent(String message)
	{
		showAndroidMessage("I was called with: " + message);
	}
	
}
