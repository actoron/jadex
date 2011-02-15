package jadex.micro.examples.dungeonkeeper;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.environment.space2d.action.GetPosition;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.Map;

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
		
		IComponentStep com = new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				final ISpaceObject avatar = space.getAvatar(getComponentIdentifier());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				double dir = ((Number)avatar.getProperty("direction")).doubleValue();

				// move
				// change direction slightly
				double factor = 10;
				double rotchange = Math.random()*Math.PI/factor-Math.PI/2/factor;
				
				double newdir = dir+rotchange;
				if(newdir<0)
					newdir+=Math.PI*2;
				else if(newdir>Math.PI*2)
					newdir-=Math.PI*2;
				
				// convert to vector
				// normally x=cos(dir) and y=sin(dir)
				// here 0 degree is 12 o'clock and the rotation right
				double x = Math.sin(newdir);
				double y = -Math.cos(newdir);
//				double x = Math.sin(newdir);
//				double y = Math.cos(newdir);
				double stepwidth = 0.2;
				IVector2 newdirvec = new Vector2Double(x*stepwidth, y*stepwidth);
				IVector2 newpos = mypos.copy().add(newdirvec);
				
				// hack
				avatar.setProperty("direction", new Double(newdir));
				
				Map params = new HashMap();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(GetPosition.PARAMETER_POSITION, newpos);
				space.performSpaceAction("move", params, createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// change direction
						double dir = ((Number)avatar.getProperty("direction")).doubleValue();
						dir = dir + Math.PI/2;
						if(dir>=Math.PI*2)
							dir -= Math.PI*2;
//						System.out.println("newdir: "+dir);
						avatar.setProperty("direction", new Double(dir));
					}
				}));
				
				waitForTick(this);
				
				return null;
			}
			
			public String toString()
			{
				return "imp.body()";
			}
		};
		
		waitForTick(com);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 * /
	public static Object getMetaInfo()
	{
	}*/
}
