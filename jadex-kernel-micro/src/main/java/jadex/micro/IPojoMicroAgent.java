package jadex.micro;

/**
 *  Interface for pojo micro agent.
 *  Allows fetching the user pojo agent object
 *  from the micro agent.
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
