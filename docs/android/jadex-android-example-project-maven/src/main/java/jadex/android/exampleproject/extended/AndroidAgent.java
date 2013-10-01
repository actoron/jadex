package jadex.android.exampleproject.extended;

import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
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
	@ProvidedService(name="guiproxy", type=IAgentInterface.class, implementation=@Implementation(AgentInterface.class))
})
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

	//-------- helper methods --------

	/**
	 *	Show a message on the device.  
	 *  @param msg The message to be shown.
	 */
	protected void showAndroidMessage(String txt)
	{
		Intent intent = new Intent("agentMessage");
		intent.putExtra("message", txt);
		Context ctx = (Context) getArgument("androidContext");
		ctx.sendBroadcast(intent);
	}
}
