package jadex.micro;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Automatically reinvokes send method in intervals
 *  determined by the delay (in state).
 *  
 *  Subclasses should override send to perform
 *  specific actions.
 */
public class IntervalBehavior<T>
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;

	/** The component step. */
	protected IComponentStep<T> step;
	
	/** The delay. */
	protected long delay;
	
	/** The current id. */
	protected String id;
	
	/** The realtime flag. */
	protected boolean realtime;
	
	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public IntervalBehavior(IInternalAccess component, long delay, IComponentStep<T> step, boolean realtime)
	{
		this.component = component;
		this.step = step;
		this.delay = delay;
		this.realtime	= realtime;
//		startSendBehavior();
	}
	
	//-------- methods --------
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	public IFuture<Void> startBehavior()
	{
		final Future<Void> ret = new Future<Void>();
		
		final String id = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
		this.id = id;	
		
		component.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dostep")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> self = this;
//				final Future<Void> iret = new Future<Void>();
//				if(!component.isKilled() && id.equals(getId()))
				
				// schedule the real step
				component.getExternalAccess().scheduleStep(getStep())
					.addResultListener(new StepResultListener<T, Void>(ret)
				{
					public void customResultAvailable(T result) 
					{
						// if still ok wait and reschedule
						if("end".equals(getId()))
						{
							ret.setResult(null);
						}
						else if(id.equals(getId()) && getDelay()>0)
						{
							component.getComponentFeature(IExecutionFeature.class).waitForDelay(getDelay(), self, realtime)
								.addResultListener(new StepResultListener<Void, Void>(ret)
							{
								public void customResultAvailable(Void result) 
								{
								}
							});
						}	
					}
				});

				return IFuture.DONE;
			}
		});//.addResultListener(new StepResultListener<Void, Void>(ret)
//		{
//			public void customResultAvailable(Void result) 
//			{
//				ret.setResult(null);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  (Ends automatically when a new send behaviour is started).
	 */
	public IFuture<Void> stopBehavior()
	{
		id = "end";
		return IFuture.DONE;
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String sendid)
	{
		this.id = sendid;
	}

	/**
	 *  Get the step.
	 *  @return The step.
	 */
	public IComponentStep<T> getStep()
	{
		return step;
	}

	/**
	 *  Set the step.
	 *  @param step The step to set.
	 */
	public void setStep(IComponentStep<T> step)
	{
		this.step = step;
	}

	/**
	 *  Get the delay.
	 *  @return The delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}
	
	/**
	 * 
	 */
	public static abstract class StepResultListener<E, F> extends ExceptionDelegationResultListener<E, F>
	{
		/**
		 * 
		 */
		public StepResultListener(Future<F> ret)
		{
			super(ret);
		}
		
		/**
		 * 
		 */
		public void exceptionOccurred(Exception exception)
		{
			if(exception instanceof ComponentTerminatedException)
			{
				future.setResult(null);
			}
			else
			{
				future.setException(exception);
			}
		}
	}
}
