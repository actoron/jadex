package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Go to a specified position.
 */
public class GoPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("env").getFact();

		int size = env.getGridSize();
		Position target = (Position)getParameter("pos").getValue();

		while(!target.equals(env.getPosition(getComponentName())))
		{
			Position mypos = env.getPosition(getComponentName());
			String dir = null;
			int mx = mypos.getX();
			int tx = target.getX();
			int my = mypos.getY();
			int ty = target.getY();

			assert mx!=tx || my!=ty;

			if(mx!=tx)
			{
				dir = Environment.RIGHT;
				int dx = Math.abs(mx-tx);
				if(mx>tx && dx<=size/2)
					dir = Environment.LEFT;
			}
			else
			{
				dir = Environment.DOWN;
				int dy = Math.abs(my-ty);
				if(my>ty && dy<=size/2)
					dir = Environment.UP;
			}

			//System.out.println("Wants to go: "+dir);
			waitFor(100);
			//System.out.println(getAgentName()+" "+getName());
			env.go(getComponentName(), dir);
		}
	}
}
