package jadex.bdi.examples.garbagecollector;

import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.Plan;

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
		Position target = (Position)getParameter("pos").getValue();

		while(!target.equals(env.getPosition(getAgentIdentifier())))
		{
			IVector2 mypos = env.getPosition(getAgentIdentifier());
			String dir = null;
			int mx = mypos.getXAsInteger();
			int tx = target.getX();
			int my = mypos.getYAsInteger();
			int ty = target.getY();

			assert mx!=tx || my!=ty;

			if(mx!=tx)
			{
				dir = Environment.RIGHT;
				int dx = Math.abs(mx-tx);
				if(mx>tx && dx<=size.getXAsInteger()/2)
					dir = Environment.LEFT;
			}
			else
			{
				dir = Environment.DOWN;
				int dy = Math.abs(my-ty);
				if(my>ty && dy<=size.getYAsInteger()/2)
					dir = Environment.UP;
			}

			//System.out.println("Wants to go: "+dir);
			waitFor(100);
			//System.out.println(getAgentName()+" "+getName());
			
//			env.go(getAgentName(), dir);
			
			Map params = new HashMap();
			params.put("dir", dir);
			env.performAction("go", params);
		}
	}
}
