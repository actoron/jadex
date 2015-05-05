package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MBelief;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.IResultCommand;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.EventType;
import jadex.rules.eca.RuleSystem;

/**
 * 
 */
public class EventPublisher
{
	/** The agent interpreter. */
	protected IInternalAccess agent;
	
	/** The add event name. */
	protected EventType addevent;
	
	/** The remove event name. */
	protected EventType remevent;
	
	/** The change event name. */
	protected EventType changeevent;
	
	/** The belief model. */
	protected MBelief mbel;

	/**
	 *  Create a new list wrapper.
	 */
	public EventPublisher(IInternalAccess agent, 
		String addevent, String remevent, String changeevent, MBelief mbel)
	{
		this(agent, new EventType(addevent), new EventType(remevent), new EventType(changeevent), mbel);
	}
	
	/**
	 *  Create a new list wrapper.
	 */
	public EventPublisher(IInternalAccess agent, 
		EventType addevent, EventType remevent, EventType changeevent, MBelief mbel)
	{
		this.agent = agent;
		this.addevent = addevent;
		this.remevent = remevent;
		this.changeevent = changeevent;
		this.mbel = mbel;
	}
	
//	/**
//	 *  Get the interpreter.
//	 *  @return The interpreter.
//	 */
//	public BDIAgentInterpreter getInterpreter()
//	{
//		return interpreter;
//	}
	
	/**
	 *  Get the rule system.
	 *  @return The rule system.
	 */
	public RuleSystem getRuleSystem()
	{
		return ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getRuleSystem();
	}

	/**
	 * 
	 */
	public void observeValue(final Object val)
	{
		if(val!=null)
		{
			getRuleSystem().observeObject(val, true, false, new IResultCommand<IFuture<Void>, PropertyChangeEvent>()
			{
				public IFuture<Void> execute(final PropertyChangeEvent event)
				{
					final Future<Void> ret = new Future<Void>();
					try
					{
						IFuture<Void> fut = agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								publishToolBeliefEvent();
								Event ev = new Event(changeevent, new ChangeInfo<Object>(event.getNewValue(), event.getOldValue(), null));
								getRuleSystem().addEvent(ev);
								return IFuture.DONE;
//								return new Future<IEvent>(ev);
							}
						});
						fut.addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ComponentTerminatedException)
								{
//									System.out.println("Ex in observe: "+exception.getMessage());
									getRuleSystem().unobserveObject(val);
									ret.setResult(null);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						});
					}
					catch(Exception e)
					{
						if(!(e instanceof ComponentTerminatedException))
							System.out.println("Ex in observe: "+e.getMessage());
						getRuleSystem().unobserveObject(val);
						ret.setResult(null);
					}
					return ret;
				}
			});
		}
	}

	/**
	 * 
	 */
	public void unobserveValue(Object val)
	{
		getRuleSystem().unobserveObject(val);
	}
	
	/**
	 * 
	 */
	public void publishToolBeliefEvent()//String evtype)
	{
		((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).publishToolBeliefEvent(agent, mbel);//, evtype);
	}

	/**
	 *  Get the addevent.
	 *  @return The addevent
	 */
	public EventType getAddEvent()
	{
		return addevent;
	}

	/**
	 *  Get the remevent.
	 *  @return The remevent
	 */
	public EventType getRemEvent()
	{
		return remevent;
	}

	/**
	 *  Get the changeevent.
	 *  @return The changeevent
	 */
	public EventType getChangeEvent()
	{
		return changeevent;
	}
	
	
}
