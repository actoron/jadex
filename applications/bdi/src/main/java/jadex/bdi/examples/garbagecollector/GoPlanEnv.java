package jadex.bdi.examples.garbagecollector;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Go to a specified position.
 */
public class GoPlanEnv extends Plan
{
	/** The go action id (or -1 if not set). */
	protected int	action	= -1;

	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Grid2D env = (Grid2D)getBeliefbase().getBelief("env").getFact();
		IVector2 target = (IVector2)getParameter("pos").getValue();
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		
		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
		{
			String dir = null;
			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			IVector1 md = env.getShortestDirection(mypos.getX(), target.getX(), true);
			if(md.getAsInteger()==1)
			{
				dir = GoAction.RIGHT;
			}
			else if(md.getAsInteger()==-1)
			{
				dir = GoAction.LEFT;
			}
			else
			{
				md = env.getShortestDirection(mypos.getY(), target.getY(), false);
				if(md.getAsInteger()==1)
				{
					dir = GoAction.DOWN;
				}
				else if(md.getAsInteger()==-1)
				{
					dir = GoAction.UP;
				}
			}

//			System.out.println("Wants to go: "+dir+" "+mypos+" "+target);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(GoAction.DIRECTION, dir);
			params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getComponentDescription()).getId());
			Future<Void> fut = new Future<Void>();
			action	= env.performSpaceAction("go", params, new DelegationResultListener<Void>(fut));
			fut.get();
			action	= -1;
		}
	}

	public void	aborted()
	{
//		System.out.println("go aborted "+this+", "+action);
		if(action!=-1)
		{
//			System.out.println("canceling action: "+action);
			Grid2D env = (Grid2D)getBeliefbase().getBelief("env").getFact();
			env.cancelSpaceAction(action);
		}
	}
}

