package jadex.micro.examples.helpline;

import jadex.commons.IFuture;

/**
 * 
 */
public interface IHelpline
{
	/**
	 *  Get all information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IFuture getInformation(String name);
	
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(String name, String info);
	
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IFuture getLocalInformation(String name);
}
