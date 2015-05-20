package jadex.bdiv3x.runtime;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.WaitAbstraction;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;
import java.util.logging.Logger;

/**
 *  Dummy class for loading v2 examples using v3x.
 */
public abstract class Plan
{
	/** The internal access. */
	protected IInternalAccess agent;
	
	/** The rplan. */
	protected RPlan rplan;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public abstract void body();

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
	}
	
	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(int timeout)
	{
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout).get();
	}
	
	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 */
	public IMessageEvent waitForMessageEvent(String type)
	{
		return waitForMessageEvent(type, -1);
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 *  @param timeout The timeout.
	 */
	public IMessageEvent waitForMessageEvent(String type, long timeout)
	{
		final Future<IMessageEvent> ret = new Future<IMessageEvent>();

		IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class);
		MMessageEvent mevent = bdif.getBDIModel().getCapability().getMessageEvent(type);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addMessageEvent(mevent);

		IMessageEvent res = (IMessageEvent)getRPlan().getFromWaitqueue(wa);
		if(res!=null)
		{
			return res;
		}
		else
		{
			// todo: add scope name if is in capa
			getRPlan().setWaitAbstraction(wa);
			
			// todo: timeout
			
	//		final ResumeCommand<IMessageEvent> rescom = getRPlan().new ResumeCommand<IMessageEvent>(ret, false);
	//
	//		if(timeout>-1)
	//		{
	//			IFuture<ITimer> cont = getRPlan().createTimer(timeout, agent, rescom);
	//			cont.addResultListener(new DefaultResultListener<ITimer>()
	//			{
	//				public void resultAvailable(final ITimer timer)
	//				{
	//					if(timer!=null)
	//						rescom.setTimer(timer);
	//				}
	//			});
	//		}
			
	//		rplan.addResumeCommand(rescom);
			
			return ret.get();
		}
	}
	
	/**
	 *  Kill this agent.
	 */
	public void	killAgent()
	{
		agent.killComponent();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The beliefbase.
	 */
	public IBeliefbase getBeliefbase()
	{
		return ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapability().getBeliefbase();
	}

	/**
	 *  Get the rplan.
	 *  @return The rplan
	 */
	public RPlan getRPlan()
	{
		return rplan;
	}
	
	/**
	 *  Get the reason this plan was created for.
	 *  @return The reason.
	 */
	public Object getReason()
	{
		return rplan.getReason();
	}
	
	/**
	 *  Get the reason this plan was created for.
	 *  @return The reason.
	 */
	public Object getDispatchedElement()
	{
		return rplan.getDispatchedElement();
	}
	
	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 */
	public IParameter getParameter(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get an expression by name.
	 *  @name The expression name.
	 *  @return The expression.
	 */
	public IExpression getExpression(String name)
	{
		return ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class))
			.getCapability().getExpressionbase().getExpression(name);
	}
	
	/**
	 *  Get an expression by name.
	 *  @name The expression name.
	 *  @return The expression.
	 */
	public IExpression createExpression(String exp)
	{
		return ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class))
			.getCapability().getExpressionbase().createExpression(exp);
	}
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture<Void> sendMessage(IMessageEvent me)
	{	
		return agent.getComponentFeature(IMessageFeature.class).sendMessage((Map<String, Object>)me.getMessage(), me.getMessageType());
	}
	
	/**
	 *  Let the plan fail.
	 */
	public void fail()
	{
		throw new PlanFailureException();
	}
}
