package jadex.micro.tutorial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Chat service implementation.
 */
@Service
public class ChatServiceD2 implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;

	/** The required services feature **/
	@ServiceComponent
	private IRequiredServicesFeature requiredServicesFeature;
	
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
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();
		
		this.format = new SimpleDateFormat("hh:mm:ss");
		final IExternalAccess exta = agent.getExternalAccess();
		IFuture<IClockService>	clockservice	= requiredServicesFeature.getService("clockservice");
		clockservice.addResultListener(new SwingExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(IClockService result)
			{
				clock = result;
				gui = createGui(exta);
				ret.setResult(null);
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
		gui.addMessage(agent.getIdentifier().getLocalName()+" received at "
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
