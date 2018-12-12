package jadex.bdi.examples.cleanerworld.truck;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;


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
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(ISpaceAction.OBJECT_ID, wastebins[i]);
			Future<Void> fut = new Future<Void>();
			env.performSpaceAction("empty_wastebin", params, new DelegationResultListener<Void>(fut));
			fut.get();
			
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
