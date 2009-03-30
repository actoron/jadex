package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.Plan.SyncResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 *  Go to a specified position.
 */
public class GoPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 target = (IVector2)getParameter("pos").getValue();

		while(!target.equals(env.getPosition(getAgentIdentifier())))
		{
			IVector2 mypos = env.getPosition(getAgentIdentifier());
			String dir = null;
			int mx = mypos.getXAsInteger();
			int tx = target.getXAsInteger();
			int my = mypos.getYAsInteger();
			int ty = target.getYAsInteger();

			assert mx!=tx || my!=ty;

			if(mx!=tx)
			{
				dir = GoAction.RIGHT;
				int dx = Math.abs(mx-tx);
				if(mx>tx && dx<=size.getXAsInteger()/2)
					dir = GoAction.LEFT;
			}
			else
			{
				dir = GoAction.DOWN;
				int dy = Math.abs(my-ty);
				if(my>ty && dy<=size.getYAsInteger()/2)
					dir = GoAction.UP;
			}

			//System.out.println("Wants to go: "+dir);
			waitFor(100);
			//System.out.println(getAgentName()+" "+getName());
			
//			env.go(getAgentName(), dir);
			
			Map params = new HashMap();
			params.put(GoAction.DIRECTION, dir);
			params.put(ISpaceObject.ACTOR_ID, getAgentIdentifier());
			SyncResultListener srl	= new SyncResultListener();
			env.performAction("go", params, srl); // todo: garbage as parameter?
			srl.waitForResult();
		}
	}
}
