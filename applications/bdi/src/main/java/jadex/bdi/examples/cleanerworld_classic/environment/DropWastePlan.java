package jadex.bdi.examples.cleanerworld_classic.environment;

import jadex.bdi.examples.cleanerworld_classic.Environment;
import jadex.bdi.examples.cleanerworld_classic.RequestDropWaste;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdi.examples.cleanerworld_classic.Wastebin;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.Done;

/**
 *  A cleaner requests to drop waste into a waste-bin.
 *  This can fail, when the wastebin is already full.
 */
public class  DropWastePlan extends Plan
{
	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		RequestDropWaste op = (RequestDropWaste)getParameter("action").getValue();
		Waste waste = op.getWaste();
		String wastebinname = op.getWastebinname();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Wastebin wb = env.getWastebin(wastebinname);
		boolean success = env.dropWasteInWastebin(waste, wb);

		if(!success)
			fail();

		Done done = new Done();
		done.setAction(op);
		getParameter("result").setValue(op);
	}

}
