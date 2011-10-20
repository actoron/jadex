package jadex.micro.tutorial;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Chat service implementation.
 */
@Service
public class ChatServiceD1 implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The clock service. */
	protected IClockService clock;
	
	/** The time format. */
	protected DateFormat format;
	
	//-------- attributes --------
	
	/**
	 *  Init the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		final Future ret = new Future();
		this.format = new SimpleDateFormat("hh:mm:ss");
		IFuture<IClockService>	fut	= agent.getServiceContainer().getRequiredService("clockservice");
		fut.addResultListener(new DelegationResultListener<IClockService>(ret)
		{
			public void customResultAvailable(IClockService result)
			{
				clock = result;
				super.customResultAvailable(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Receives a chat message.
	 *  @param sender The sender's name.
	 *  @param text The message text.
	 */
	public void message(final String sender, final String text)
	{
		System.out.println(agent.getComponentIdentifier().getLocalName()+" received at "
			+format.format(new Date(clock.getTime()))+" from: "+sender+" message: "+text);
	}
}
