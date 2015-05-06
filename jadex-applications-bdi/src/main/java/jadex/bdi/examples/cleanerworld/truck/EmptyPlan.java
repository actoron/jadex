package jadex.bdi.examples.cleanerworld.truck;

import jadex.bdi.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.HashMap;
import java.util.Map;


/**
 *  Empty all full wastebins.
 */
public class EmptyPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject[] wastebins = (ISpaceObject[])getBeliefbase().getBeliefSet("wastebins").getFacts();

		for(int i=0; i<wastebins.length; i++)
		{
			IGoal moveto = createGoal("achievemoveto");
			IVector2 pos = (IVector2)wastebins[i].getProperty(Space2D.PROPERTY_POSITION);
			moveto.getParameter("location").setValue(pos);
//			System.out.println("Created: "+loci[i]+" "+this);
			dispatchSubgoalAndWait(moveto);
			wastebins[i].setProperty("wastes", Integer.valueOf(0));
			
			Map params = new HashMap();
			params.put(ISpaceAction.OBJECT_ID, wastebins[i]);
			SyncResultListener srl	= new SyncResultListener();
			env.performSpaceAction("empty_wastebin", params, srl);
			srl.waitForResult();
			
//			System.out.println("Reached: "+loci[i]+" "+this);
		}
		
		IGoal moveto = createGoal("achievemoveto");
		moveto.getParameter("location").setValue(new Vector2Double(1, 0.5));
		dispatchSubgoalAndWait(moveto);
		
		// Not needed due to 'killavatar' in avatar mappings.
//		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		env.destroySpaceObject(myself.getId());
		
		killAgent();
	}
}
