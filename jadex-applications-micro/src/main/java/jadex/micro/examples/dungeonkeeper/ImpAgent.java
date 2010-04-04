package jadex.micro.examples.dungeonkeeper;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.environment.space2d.action.GetPosition;
import jadex.application.space.envsupport.environment.space2d.action.SetPosition;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Int;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.application.space.envsupport.math.Vector2Int;
import jadex.commons.IFilter;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The imp agent.
 */
public class ImpAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationExternalAccess	app	= (IApplicationExternalAccess)getParent();
		final Grid2D space = (Grid2D)app.getSpace("mygc2dspace");
		
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				final ISpaceObject avatar = space.getAvatar(getComponentIdentifier());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				double dir = ((Number)avatar.getProperty("direction")).doubleValue();

				// Turn per random chance
				if(Math.random()>0.7)
				{
					dir = dir - Math.PI/2;
					if(dir<0)
						dir += Math.PI*2;
				}
				
				IVector2 newpos;
				
				int px = mypos.getXAsInteger();
				int py = mypos.getYAsInteger();
				if(dir==0)
				{
					newpos = new Vector2Int(px, py-1);
				}
				else if(dir==Math.PI/2)
				{
					newpos = new Vector2Int(px, py+1);
				}
				else if(dir==Math.PI)
				{
					newpos = new Vector2Int(px-1, py);
				}
				else //if(dir==Math.PI*3/2)
				{
					newpos = new Vector2Int(px+1, py);
				}
				
				Map params = new HashMap();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(GetPosition.PARAMETER_POSITION, newpos);
				space.performSpaceAction("move", params, createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						// change direction
						double dir = ((Number)avatar.getProperty("direction")).doubleValue();
						dir = dir + Math.PI/2;
						if(dir>=Math.PI*2)
							dir -= Math.PI*2;
						avatar.setProperty("direction", new Double(dir));
					}
				}));
				
				waitForTick(this);
			}
			
			public String toString()
			{
				return "imp.body()";
			}
		};
		
		waitForTick(runnable);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 * /
	public static Object getMetaInfo()
	{
	}*/
}
