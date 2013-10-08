package jadex.android.exampleproject.extended.agent;

import jadex.android.AndroidContextManager;
import jadex.android.exampleproject.extended.MyEvent;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

/**
 *  Simple example agent that shows messages
 *  when it is started, stopped and when it receives a message. 
 */
@Description("Sample Android Agent.")
@RequiredServices({
		@RequiredService(name="androidcontext", type=IContextService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
})
@ProvidedServices({
	@ProvidedService(name="guiproxy", type=IAgentInterface.class)
})
@Service
public class AndroidAgent extends MicroAgent implements IAgentInterface
{
	@AgentArgument
	public Context androidContext;
	
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
		Intent intent = new Intent("agentMessage");
		intent.putExtra("message", txt);
		Object argument = getArgument("androidContext");
		Map<String, Object> arguments = getArguments();
		Context ctx = (Context) argument;
//		ctx.sendBroadcast(intent);
		MyEvent myEvent = new MyEvent();
		myEvent.data = txt;
		try
		{
			AndroidContextManager.getInstance().dispatchEvent(myEvent);
		}
		catch (WrongEventClassException e)
		{
			e.printStackTrace();
		}
	}



	@Override
	public void callAgent(String message)
	{
		System.out.println("callAgent()");
		showAndroidMessage("I was called with: " + message);
	}
	
}
