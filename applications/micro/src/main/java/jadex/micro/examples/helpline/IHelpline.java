package jadex.micro.examples.helpline;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Basic interface for helpline.
 *  Allows to get local information about a person and
 *  add information about a person.
 */
@Security(roles=Security.UNRESTRICTED)
public interface IHelpline
{
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains all information records as collection.
	 */
	public IIntermediateFuture<InformationEntry> getInformation(String name);
	
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(String name, String info);
}
