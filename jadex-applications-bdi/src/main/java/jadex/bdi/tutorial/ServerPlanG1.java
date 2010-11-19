package jadex.bdi.tutorial;

import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


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

	/**
	 *  Create a new plan.
	 */
	public ServerPlanG1()	//throws IOException
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
	}
	
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
		// Start the conmnection listener thread.
		new Thread(this).start();

		// When the agent dies the listener will shut down the server.
		getScope().addComponentListener(new IComponentListener()
		{
			public void componentTerminating(ChangeEvent ae)
			{
				close();
			}
			
			public void componentTerminated(ChangeEvent ae)
			{
			}
		});
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
				getExternalAccess().scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
						IGoal goal = scope.getGoalbase().createGoal("translate");
						goal.getParameter("client").setValue(client);
						scope.getGoalbase().dispatchTopLevelGoal(goal);
						return null;
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
