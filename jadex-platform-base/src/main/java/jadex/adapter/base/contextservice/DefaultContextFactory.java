package jadex.adapter.base.contextservice;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class DefaultContextFactory implements IContextFactory
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform	platform;
	
	//-------- constructors --------
	
	/**
	 *  Create a new default context factory.
	 *  @param platform	The platform.
	 */
	public DefaultContextFactory(IPlatform platform)
	{
		this.platform	= platform;
	}
	
	//-------- IContextFactory interface --------
	
	/**
	 *  Create a new context.
	 *  @param name	The name of the context.
	 *  @param parent	The parent of the context (if any).
	 *  @param properties	Initialization properties (if any).
	 */
	public IContext createContext(String name, IContext parent, Map properties)
	{
		return new DefaultContext(name, parent, properties);
	}


	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Default context behavior is to destroy
	 *  contained agents.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(final IContext context, final IResultListener listener)
	{
		((DefaultContext)context).setTerminating(true);
		final IAgentIdentifier[]	agents	= context.getAgents();
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
							listener.resultAvailable(context);
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
				listener.resultAvailable(context);
		}
	}
}
