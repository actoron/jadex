package jadex.micro.examples.helpline;

import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.service.BasicService;

/**
 *  Helpline service implementation.
 */
public class HelplineService extends BasicService implements IHelpline
{
	//-------- attributes --------
	
	/** The agent. */
//	protected IMicroExternalAccess agent;
	protected HelplineAgent agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new helpline service.
	 */
	public HelplineService(HelplineAgent agent)
	{
		super(agent.getServiceProvider().getId(), IHelpline.class, null);
		this.agent = agent;
	}
	
	//-------- methods --------
	
//	/**
//	 *  Add an information about a person.
//	 *  @param name The person's name.
//	 *  @param info The information.
//	 */
//	public void addInformation(final String name, final String info)
//	{
//		agent.scheduleStep(new ICommand()
//		{
//			public void execute(Object args)
//			{
//				((HelplineAgent)args).addInformation(name, info);
//			}
//		});
//	}
//	
//	/**
//	 *  Get all locally stored information about a person.
//	 *  @param name The person's name.
//	 *  @return Future that contains the information.
//	 */
//	public IFuture getInformation(final String name)
//	{
//		final Future ret = new Future();
//		agent.scheduleStep(new IResultCommand()
//		{
//			public Object execute(Object args)
//			{
//				return ((HelplineAgent)args).getInformation(name);
//			}
//		}).addResultListener(new DelegationResultListener(ret));
//		return ret;
//	}
	
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(final String name, final String info)
	{
		agent.addInformation(name, info);
	}
	
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IIntermediateFuture getInformation(final String name)
	{
		return new IntermediateFuture(agent.getInformation(name));
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "HelplineService, "+agent.getComponentIdentifier();
	}
}
