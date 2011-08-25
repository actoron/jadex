package jadex.micro;

/**
 *  Interface for pojo mirco agent.
 *  Allows fetching the user pojo agent object
 *  from thus mirco agent.
 */
// Todo: implementing IMicroAgent makes no sense!
public interface IPojoMicroAgent extends IMicroAgent
{
	/**
	 *  Get the pojo agent.
	 *  @return Thepojo  agent.
	 */
	public Object getPojoAgent();
}
