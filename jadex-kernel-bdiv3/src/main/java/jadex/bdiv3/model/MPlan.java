package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;


/**
 *  Modelelement for a plan.
 */
public class MPlan extends MParameterElement
{
	/** The trigger. */
	protected MTrigger trigger;
	
	/** The waitqueue trigger. */
	protected MTrigger waitqueue;
	
	/** The plan body. */
	protected MBody body;
	
	/** The plan priority. */
	protected int priority;
	
	//-------- additional xml properties --------

//	/** The parameters. */
//	protected List<MParameter> parameters;
		
	/** The precondition. */
	protected UnparsedExpression	precondition;
		
	/**
	 *	Bean Constructor. 
	 */
	public MPlan()
	{
	}
	
	/**
	 *  Create a new belief.
	 */
	public MPlan(String name, MBody body, MTrigger trigger, MTrigger waitqueue, int priority)
	{
		super(name);
		this.body = body;
		this.trigger = trigger;
		this.waitqueue = waitqueue;
		this.priority = priority;
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public MBody getBody()
	{
		return body;
	}

	/**
	 *  Set the body.
	 *  @param body The body to set.
	 */
	public void setBody(MBody body)
	{
		this.body = body;
	}

	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public MTrigger getTrigger()
	{
		return trigger;
	}

	/**
	 *  Set the trigger.
	 *  @param trigger The trigger to set.
	 */
	public void setTrigger(MTrigger trigger)
	{
		this.trigger = trigger;
	}
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public MTrigger getWaitqueue()
	{
		return waitqueue;
	}

	/**
	 *  Set the waitqueue.
	 *  @param waitqueue The waitqueue to set.
	 */
	public void setWaitqueue(MTrigger waitqueue)
	{
		this.waitqueue = waitqueue;
	}

	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 *  Set the priority.
	 *  @param priority The priority to set.
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

//	/**
//	 *  Get the parameters.
//	 *  @return The parameters.
//	 */
//	public List<MParameter> getParameters()
//	{
//		return parameters;
//	}
//	
//	/**
//	 *  Get a parameter by name.
//	 */
//	public MParameter getParameter(String name)
//	{
//		MParameter ret = null;
//		if(parameters!=null && name!=null)
//		{
//			for(MParameter param: parameters)
//			{
//				if(param.getName().equals(name))
//				{
//					ret = param;
//					break;
//				}
//			}
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Test if goal has a parameter.
//	 */
//	public boolean hasParameter(String name)
//	{
//		return getParameter(name)!=null;
//	}
//
//	/**
//	 *  Set the parameters.
//	 *  @param parameters The parameters to set.
//	 */
//	public void setParameters(List<MParameter> parameters)
//	{
//		this.parameters = parameters;
//	}
//	
//	/**
//	 *  Add a parameter.
//	 *  @param parameter The parameter.
//	 */
//	public void addParameter(MParameter parameter)
//	{
//		if(parameters==null)
//			parameters = new ArrayList<MParameter>();
//		this.parameters.add(parameter);
//	}
	
	/**
	 *  Get the precondition.
	 */
	public UnparsedExpression	getPrecondition()
	{
		return precondition;
	}
	
	/**
	 *  Set the precondition.
	 */
	public void	setPrecondition(UnparsedExpression precondition)
	{
		this.precondition	= precondition;
	}
}
