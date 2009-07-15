package jadex.bdi.examples.antworld.depricated;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.Plan;

/**
 * Check the grid for garbage.
 */
public class TestPlan extends Plan {
	/**
	 * The plan body.
	 */
	public void body() {
		System.out.println("Calling the Test plan!");

		Space2D env = (Space2D) getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 target = (IVector2) getBeliefbase().getBelief("pos").getFact();
		ISpaceObject myself = (ISpaceObject) getBeliefbase()
				.getBelief("myself").getFact();
//		HashMap prop = (HashMap) myself.getProperties();

//		System.out.println("size: " + prop.size());

		for (int i = 0; i < 10; i++) {
			waitFor(7000);
			System.out.println("###Current Position: "  + target.toString());
		}

		// myself.setProperty("CountrySize", new Integer(25));
		// waitFor(7000);
		// myself.setProperty("CountrySize", new Integer(35));

		// for ( String elem : prop.keySet() )
		// System.out.println( elem );
		
//		Vector2Double vec = new Vector2Double( Math.max(0.1,
//				(1/10)),
//				Math.max(0.1,
//				(1/10.0) ));

	}
}
