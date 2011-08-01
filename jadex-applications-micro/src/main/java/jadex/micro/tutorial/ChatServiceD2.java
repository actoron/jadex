package jadex.micro.tutorial;

import jadex.base.gui.SwingDelegationResultListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

/**
 *  Chat service implementation.
 */
public class ChatServiceD2 implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The clock service. */
	protected IClockService clock;
	
	/** The time format. */
	protected DateFormat format;
	
	/** The user interface. */
	protected ChatGuiD2 gui;
	
	//-------- attributes --------
	
	/**
	 *  Init the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		final Future ret = new Future();
		
		this.format = new SimpleDateFormat("hh:mm:ss");
		final IExternalAccess exta = agent.getExternalAccess();
		agent.getServiceContainer().getRequiredService("clockservice")
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				clock = (IClockService)result;
				gui = createGui(agent.getExternalAccess());
				super.customResultAvailable(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Init the service.
	 */
	@ServiceShutdown
	public void shutdownService()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
			}
		});
//		return IFuture.DONE;
	}
	
	/**
	 *  Receives a chat message.
	 *  @param sender The sender's name.
	 *  @param text The message text.
	 */
	public void message(final String sender, final String text)
	{
		gui.addMessage(agent.getComponentIdentifier().getLocalName()+" received at "
			+format.format(new Date(clock.getTime()))+" from: "+sender+" message: "+text);
	}
	
	/**
	 *  Create the gui.
	 */
	protected ChatGuiD2 createGui(IExternalAccess agent)
	{
		return new ChatGuiD2(agent);
	}
}
