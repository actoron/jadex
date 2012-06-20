package jadex.bdi.examples.showroom3d.rotation;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.math.Vector3Int;

/**
 *  Wander around randomly.
 */
public class RotationPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public RotationPlan()
	{
		//getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		
		ISpaceObject[] ispaceObjects = space.getSpaceObjectsByType("rotationplattform");
		int distance = ispaceObjects.length;
		
		SpaceObject me = (SpaceObject)((Space3D)space).getSpaceObject(myself.getId());
		me.setProperty("rotation", new Vector3Double(0, 0, 0));

		
		Long intme = (Long)me.getId();
		
		int integerme = intme.intValue();
		ISpaceObject avatar = space.getAvatar(getComponentDescription());
		
		int tmp = 360/distance*integerme;
		int clock = (((Number)avatar.getProperty("tick360")).intValue()+tmp)%360;
		
		
		
		double radius=0.5;
		double xcenter = 0.5;
		double ycenter= 0.5;
	


		double x=(double)(Math.cos(clock*Math.PI/180)*radius+xcenter);
		double y=(double)(Math.sin(clock*Math.PI/180)*radius+ycenter);
		
		
		me.setProperty("position", new Vector3Double(x, (Math.sin(clock*Math.PI/360))/2, y));
		
		
	}
}
