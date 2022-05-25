package jadex.bridge;

import jadex.bridge.component.impl.ExecutionComponentFeature.StepInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadLocalTransferHelper;

/**
 *  Interface for a component step.
 *  
 *  For making steps in anonymous inner classes properly serializable
 *  the @XMLClassname annotation has to be provided or
 *  a static field for the name has to be declared:<br>
 *  public static final String XML_CLASSNAME = ...; 
 */
public interface IComponentStep<T>
{
	/** The current service calls mapped to threads. */
	public static final ThreadLocal<StepInfo> CURRENT_STEP  = new ThreadLocal<StepInfo>();
	public static final Class<MyInit> __myinit = MyInit.class;
		
	class MyInit 
	{
		static
		{
			ThreadLocalTransferHelper.addThreadLocal(CURRENT_STEP);
		}
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<T> execute(IInternalAccess ia);
	
	/**
	 *  Set the current step.
	 *  @param step The step.
	 */
	public static void setCurrentStep(StepInfo step)
	{
		CURRENT_STEP.set(step);
	}
	
	/**
	 *  Get the current step.
	 *  @return The step.
	 */
	public static StepInfo getCurrentStep()
	{
		return CURRENT_STEP.get();
	}
}
