package jadex.bdi.examples.hunterprey.ldahunter;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bdi.examples.hunterprey.ldahunter.potentialfield.JointField;
import jadex.bdi.examples.hunterprey.ldahunter.potentialfield.PotentialFrame;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;


/**
 * A plan skeleton. Custom code goes into the body() method.
 */
public class HuntPlan extends Plan
{
	ISpaceObject	myself;

	IVector2		myLoc;

	JointField		jf;

	PotentialFrame	pf;

	ISpaceObject[]	vis;

	ISpaceObject	prey;

	Grid2D			env;

	/**
	 * The plan body. The plan is finished when this method returns.
	 */
	public void body()
	{
		// set variables
		env = (Grid2D)getBeliefbase().getBelief("env").getFact();
		vis = (ISpaceObject[])getBeliefbase().getBeliefSet("vision").getFacts();
		myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		myLoc = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

		jf = (JointField)getBeliefbase().getBelief("potential_field").getFact();
		pf = (PotentialFrame)getBeliefbase().getBelief("potential_window").getFact();

		prey = (ISpaceObject)getBeliefbase().getBelief("next_sheep").getFact();

		// if(prey!=null)
		// {
		// IVector2 ppos =
		// (IVector2)prey.getProperty(Space2D.PROPERTY_POSITION);
		// if(env.getDistance(myLoc, ppos).getAsInteger()>2)
		// throw new
		// RuntimeException("Wurks0: "+getScope().getAgentName()+", "+prey);
		//
		//
		// try
		// {
		// env.getSpaceObject(prey.getId());
		// }
		// catch(Exception e)
		// {
		// throw new
		// RuntimeException("Wurks1: "+getScope().getAgentName()+", "+prey);
		// }
		// }

		jf.update(vis, myself);

		if(!eating())
			foolAround();
	}

	/**
	 * @return true if it was eating
	 */
	protected boolean eating()
	{
		if(prey != null)
		{
			if(prey.getProperty(Space2D.PROPERTY_POSITION).equals(myLoc))
			{ // eat it
				jf.eaten(prey);
				pf.update(jf, myLoc, myLoc.getXAsInteger(), myLoc.getYAsInteger());

				try
				{
					// System.out.println("Eating: "+getScope().getAgentName()+", "+prey);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(ISpaceAction.OBJECT_ID, prey);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
					fut.get();
					// System.out.println("Ate: "+getScope().getAgentName()+", "+prey);
				}
				catch(RuntimeException e)
				{
					// System.out.println("Eating failed: "+getScope().getAgentName()+", "+prey);
					// Ignore when eating fails.
					fail();
				}

				return true;
			}
		}
		return false;
	}

	protected void foolAround()
	{
		IVector2 to;
		String dir = null;

		to = (prey != null ? (IVector2)prey.getProperty(Space2D.PROPERTY_POSITION) : jf.getBestLocation()).copy();
		pf.update(jf, myLoc, to.getXAsInteger(), to.getYAsInteger());

		while(env.getDistance(myLoc, to).getAsInteger() > 1 && jf.getNearerLocation((IVector2)to))
		{/**/
		}

		if(env.getDistance(myLoc, to).getAsInteger() == 1)
		{
			dir = MoveAction.getDirection(env, myLoc, to);
		}

		if(dir == null || MoveAction.DIRECTION_NONE.equals(dir))
		{
			String posDirs[] = MoveAction.getPossibleDirections(env, myLoc);
			String lastDir = (String)getBeliefbase().getBelief("last_direction").getFact();
			dir = posDirs[randomInt(posDirs.length)];
			for(int i = 0; i < posDirs.length; i++)
			{
				if(SUtil.equals(lastDir, posDirs[i]) && Math.random() > 0.2)
				{
					dir = lastDir;
					break;
				}
			}
		}

		// getLogger().info("Moving " + dir + " to " + to);

		getBeliefbase().getBelief("last_direction").setFact(dir);

		// System.out.println("Moving: "+myself);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		params.put(MoveAction.PARAMETER_DIRECTION, dir);
		Future<Void> fut = new Future<Void>();
		env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
		fut.get();
		// System.out.println("Moved: "+myself);
	}

	/**
	 * @param max
	 * @return integer less than max
	 */
	int randomInt(final int max)
	{
		int rnd = (int)Math.floor(Math.random() * max);
		return (rnd < max) ? rnd : max - 1;
	}

}
