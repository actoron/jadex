package jadex.adapter.base.contextservice;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  The base context provides a simple grouping mechanism for agents.
 */
public class ApplicationContext	extends BaseContext
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform	platform;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more agents can be added). */
	protected boolean	terminating;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ApplicationContext(String name, IContext parent, Map properties, IPlatform platform)
	{
		super(name, parent, properties);
		this.platform	= platform;
	}

	//-------- IContext interface --------
		
	/**
	 *  Add an agent to a context.
	 */
	public synchronized void	addAgent(IAgentIdentifier agent)
	{
		if(isTerminating())
			throw new RuntimeException("Cannot add agent to terminating context: "+agent+", "+this);

		super.addAgent(agent);
	}
		
	//-------- methods --------
	
	/**
	 *  Get the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	public boolean	isTerminating()
	{
		return this.terminating;
	}

	/**
	 *  Set the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	public void setTerminating(boolean terminating)
	{
		if(!terminating || this.terminating)
			throw new RuntimeException("Cannot terminate; illegal state: "+this.terminating+", "+terminating);
			
		this.terminating	= terminating;
	}

	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Application context behavior is to destroy
	 *  contained agents.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(final IResultListener listener)
	{
		this.setTerminating(true);
		final IAgentIdentifier[]	agents	= getAgents();
		if(agents!=null && agents.length>0)
		{
			// Create AMS result listener (l2), when listener is used.
			// -> notifies listener, when last agent is killed.
			IResultListener	l2	= listener!=null ? new IResultListener()
			{
				int tokill	= agents.length;
				Exception	exception;
				
				public void resultAvailable(Object result)
				{
					result();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(this.exception==null)	// Only return first exception.
						this.exception	= exception;
					result();
				}
				
				/**
				 *  Called for each killed agent.
				 *  Decrease counter and notify listener, when last agent is killed.
				 */
				protected void	result()
				{
					tokill--;
					if(tokill==0)
					{
						if(exception!=null)
							listener.exceptionOccurred(exception);
						else
							listener.resultAvailable(ApplicationContext.this);
					}
				}
			} : null;
			
			// Kill all agents in the context. 
			IAMS	ams	= (IAMS) platform.getService(IAMS.class);
			for(int i=0; i<agents.length; i++)
			{
				ams.destroyAgent(agents[i], l2);
			}
		}
		else
		{
			if(listener!=null)
				listener.resultAvailable(this);
		}
	}
}
