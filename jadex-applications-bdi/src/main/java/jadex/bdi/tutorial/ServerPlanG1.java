package jadex.bdi.tutorial;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAParameter;
import jadex.bdi.runtime.Plan;
import jadex.commons.ThreadSuspendable;

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

		getScope().addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				close();
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
		
		// Keep the plan alive and the server is active.
		// When the agent dies the plan will shut down
		// the server in the aborted method.
		// The waitFor will never come back, because the
		// filter matches no event.
		//waitFor(IFilter.NEVER);
	}

	/**
	 *  Close server when plan is exited.
	 * /
	public void aborted()
	{
		close();
	}*/
	
	/**
	 *  The server code.
	 *  This method runs on the separate thread,
	 *  and repeatedly blocks until a client connects.
	 *  @see Runnable
	 */
	public void	run()
	{
		ThreadSuspendable sus = new ThreadSuspendable(new Object());
		logger.info("Created: "+Thread.currentThread());

		// Repeatedly listen for connections, until the server has been closed.
		try
		{
			// Accept connections while server is active.
			while(true)
			{
				Socket	client	= server.accept();
				IEAGoal goal = (IEAGoal)getExternalAccess().createGoal("translate").get(sus);
				((IEAParameter)goal.getParameter("client").get(sus)).setValue(client);
				getExternalAccess().dispatchTopLevelGoal(goal);
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			// Server has been closed.
			logger.info("Exited: "+Thread.currentThread());
		}
		catch(Exception e)
//		catch(AgentDeathException e)
		{
			// Agent has died: close server.
			close();
		}
	}
}
