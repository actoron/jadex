package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Environment;
import jadex.bdi.runtime.Plan;

/**
 *  Handle eat requests by the environment.
 */
public class  EnvironmentGuiPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  EnvironmentGuiPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		if (env == null)
		{
			fail();
		}
		
		
		EnvironmentGui gui;
		try {
			gui = new EnvironmentGui(getExternalAccess());
			getBeliefbase().getBelief("gui").setFact(gui);
			passed();
		} catch (Exception e) {
			fail(e);
		}

	}

}
