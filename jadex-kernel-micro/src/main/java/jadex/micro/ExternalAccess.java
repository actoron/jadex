package jadex.micro;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.BasicServiceContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * External access interface.
 */
public class ExternalAccess extends BasicServiceContainer implements IMicroExternalAccess 
{
	// -------- attributes --------

	/** The agent. */
	protected MicroAgent agent;

	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The agent adapter. */
	protected IComponentAdapter adapter;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(MicroAgent agent, MicroAgentInterpreter interpreter)
	{
		super(interpreter.getAgentAdapter().getComponentIdentifier().getLocalName());
		this.agent = agent;
		this.interpreter = interpreter;
		this.adapter = interpreter.getAgentAdapter();
	}

	// -------- eventbase shortcut methods --------

	/**
	 *  Send a message.
	 * 
	 *  @param me	The message.
	 *  @param mt	The message type.
	 */
	public void sendMessage(final Map me, final MessageType mt)
	{
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				agent.sendMessage(me, mt);
				// System.out.println("Send message: "+rme);
			}
		});
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public void	scheduleStep(Runnable step)
	{
		interpreter.scheduleStep(step);
	}

	/**
	 *  Get the agent implementation.
	 *  Operations on the agent object
	 *  should be properly synchronized with invokeLater()!
	 */
	public IFuture getAgent()
	{
		final Future ret = new Future();
		adapter.invokeLater(new Runnable() 
		{
			public void run() 
			{
				ret.setResult(agent);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the model of the component.
	 */
	public ILoadableComponentModel	getModel()
	{
		return interpreter.getAgentModel();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}
	
	/**
	 *  Get the parent component.
	 *  @return The parent component.
	 */
	public IExternalAccess	getParent()
	{
		return interpreter.getParent();
	}

	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public MicroAgentInterpreter getInterpreter()
	{
		return this.interpreter;
	}

	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		final Future ret = new Future();
		
		getService(IComponentManagementService.class).addResultListener(new ComponentResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentIdentifier[] childs = cms.getChildren(getComponentIdentifier());
				List res = new ArrayList();
				for(int i=0; i<childs.length; i++)
				{
					IExternalAccess ex = (IExternalAccess)cms.getExternalAccess(childs[i]);
					res.add(ex);
				}
				ret.setResult(res);
			}
		}, adapter));
		
		return ret;
	}
}
