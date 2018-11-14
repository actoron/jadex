package jadex.micro.examples.helpline;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Helpline service implementation.
 */
@Service
public class HelplineService implements IHelpline
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected HelplineAgent agent;
	
	//-------- methods --------
	
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
	public IIntermediateFuture<InformationEntry> getInformation(final String name)
	{
		return new IntermediateFuture<InformationEntry>(agent.getInformation(name));
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "HelplineService, "+agent.getAgent().getId();
	}
}
