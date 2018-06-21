package jadex.bdi.tutorial;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;


/**
 *  The server plan starts a thread to accept
 *  connections from clients.
 */
public class ServerPlanG1 extends Plan	implements Runnable
{
	//-------- attributes --------

	/** The server socket. */
	protected ServerSocket	server;
	
	/** The logger. */
	protected Logger logger;
	
	//-------- constructors --------

//	/**
//	 *  Create a new plan.
//	 */
//	public ServerPlanG1()	//throws IOException
//	{
//		int port = ((Integer)getParameter("port").getValue()).intValue();
//		this.logger = getLogger();
//		
//		try
//		{
//			this.server	= new ServerSocket(port);
//		}
//		catch(IOException e)
//		{
//			throw new RuntimeException(e.getMessage());
//		}
//		
//		getLogger().info("Created: "+server);
//	}
	
	/**
	 *  Close the server.
	 */
	public void close()
	{
		try
		{
			logger.info("Closing: "+server);
			server.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	//-------- methods --------
	
	/**
	 *  The plan body.
	 *  This method runs on the plan thread.
	 */
	public void body()
	{
		int port = ((Integer)getParameter("port").getValue()).intValue();
		this.logger = getLogger();
		
		try
		{
			this.server	= new ServerSocket(port);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		
		getLogger().info("Created: "+server);
		
		// Start the conmnection listener thread.
		new Thread(this).start();

		// When the agent dies the listener will shut down the server.
//		getScope().addComponentListener(new TerminationAdapter()
//		{
//			public void componentTerminated()
//			{
//				close();
//			}
//		});
		
//		getScope().subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
		getAgent().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
			.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent result)
			{
				close();
			}
		}));
	}
	
	/**
	 *  The server code.
	 *  This method runs on the separate thread,
	 *  and repeatedly blocks until a client connects.
	 *  @see Runnable
	 */
	public void	run()
	{
		logger.info("Created: "+Thread.currentThread());

		// Repeatedly listen for connections, until the server has been closed.
		try
		{
			// Accept connections while server is active.
			while(true)
			{
				final Socket	client	= server.accept();
				getExternalAccess().scheduleStep(new IComponentStep<Void>()
				{
					@Classname("translate")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						IGoal goal = bia.getGoalbase().createGoal("translate");
						goal.getParameter("client").setValue(client);
						bia.getGoalbase().dispatchTopLevelGoal(goal);
						return IFuture.DONE;
					}
				});
			}
		}
		catch(IOException e)
		{
			// Server has been closed.
			logger.info("Exited: "+Thread.currentThread());
		}
		catch(ComponentTerminatedException e)
		{
			// Agent has died: close server.
			close();
		}
	}
}
