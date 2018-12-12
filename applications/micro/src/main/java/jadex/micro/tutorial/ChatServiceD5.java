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
 *  The chat service.
 */
@Service
public class ChatServiceD5 implements IChatService
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
	protected ChatGuiD5 gui;
	
	//-------- attributes --------
	
	/**
	 *  Init the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		final Future<Void> fut = new Future<Void>();
		IFuture<Void> ret = fut;
		
		this.format = new SimpleDateFormat("hh:mm:ss");
		IFuture<IClockService> csfut = agent.getFeature(IRequiredServicesFeature.class).getService("clockservice");
		csfut.addResultListener(new SwingExceptionDelegationResultListener<IClockService, Void>(fut)
		{
			public void customResultAvailable(IClockService result)
			{
				clock = result;
				gui = createGui(agent.getExternalAccess());
				fut.setResult(null);
			}
		});
		
//		ISimulationService	simserv	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class).setMultiplicity(Multiplicity.ZERO_ONE));
//		if(simserv!=null)
//		{
//			FutureBarrier<Void>	fubar	= new FutureBarrier<>();
//			fubar.addFuture(ret);
//			fubar.addFuture(simserv.addAdvanceBlocker(ret));
//			ret	= fubar.waitFor();
//		}

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
		gui.addMessage(agent.getId().getLocalName()+" received at "
			+format.format(new Date(clock.getTime()))+" from: "+sender+" message: "+text);
	}
	
	/**
	 *  Create the gui.
	 */
	protected ChatGuiD5 createGui(IExternalAccess agent)
	{
		return new ChatGuiD5(agent);
	}
}
