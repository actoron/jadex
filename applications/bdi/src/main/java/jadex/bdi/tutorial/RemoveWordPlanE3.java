package jadex.bdi.tutorial;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Remove a word from a word table.
 */
public class RemoveWordPlanE3 extends Plan
{
	//-------- attributes --------

	/** The belief name. */
//	protected String beliefsetname;

	//-------- constructor --------

	/**
	 *  Create a new plan.
	 *  @param beliefsetname The beliefset name.
	 * /
	public RemoveWordPlanE3()
	{
		this("egwords");
	}*/
	
	/**
	 *  Create a new plan.
	 *  @param beliefsetname The beliefset name.
	 * /
	public RemoveWordPlanE3(String beliefsetname)
	{
		getLogger().info("Created: "+this);
		this.beliefsetname = beliefsetname;
	}*/

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Object[] facts = getBeliefbase().getBeliefSet("egwords").getFacts();
		getBeliefbase().getBeliefSet("egwords").removeFact(facts[0]);
		getLogger().info("Success, removed: "+facts[0]);
	}

}
