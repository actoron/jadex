package jadex.micro.examples.helpline;

import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.micro.IMicroExternalAccess;

/**
 * 
 */
public class HelplineService extends BasicService implements IHelpline
{
	/** The agent. */
	protected IMicroExternalAccess agent;
	
	/**
	 * 
	 */
	public HelplineService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IHelpline.class, null);
		this.agent = (IMicroExternalAccess)agent;
	}
	
	/**
	 *  Get all information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IFuture getInformation(final String name)
	{
		final Future ret = new Future();
		agent.scheduleResultStep(new IResultCommand()
		{
			public Object execute(Object args)
			{
				return ((HelplineAgent)args).getInformation(name);
			}
		}).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
	
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(final String name, final String info)
	{
		agent.scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				((HelplineAgent)args).addInformation(name, info);
			}
		});
	}
	
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IFuture getLocalInformation(final String name)
	{
		final Future ret = new Future();
		agent.scheduleResultStep(new IResultCommand()
		{
			public Object execute(Object args)
			{
				return ((HelplineAgent)args).getLocalInformation(name);
			}
		}).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}
