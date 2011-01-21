package jadex.micro.examples.fireflies;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Int;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;
import jadex.micro.MicroAgent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The firefly agent.
 */
public class FireflyAgent extends MicroAgent
{
	//-------- attributes --------
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationExternalAccess	app	= (IApplicationExternalAccess)getParent();
		final ContinuousSpace2D space = (ContinuousSpace2D)app.getSpace("mygc2dspace");
		IComponentStep step = new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ISpaceObject avatar = space.getAvatar(getComponentIdentifier());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				double dir = ((Number)avatar.getProperty("direction")).doubleValue();
				int clock = ((Number)avatar.getProperty("clock")).intValue();
				int threshold = ((Number)avatar.getProperty("threshold")).intValue();
				int window = ((Number)avatar.getProperty("window")).intValue();
				int resetlevel = ((Number)avatar.getProperty("reset_level")).intValue();

				int flashestoreset = ((Number)space.getProperty("flashes_to_reset")).intValue();
				int cyclelength = ((Number)space.getProperty("cycle_length")).intValue();
				
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
//						double x = Math.sin(newdir);
//						double y = Math.cos(newdir);
				double stepwidth = 0.1;
				IVector2 newdirvec = new Vector2Double(x*stepwidth, y*stepwidth);
				IVector2 newpos = mypos.copy().add(newdirvec);
				
				// Increment clock (internal counter)
				clock++;
				if(clock == cyclelength)
					clock = 0;
				
				if(clock>window && clock>=threshold)
				{
					// Look
					// if count turtles in-radius 1 with [color = yellow] >= flashes-to-reset
				    // [ set clock reset-level ]
				    Set tmp = Collections.EMPTY_SET;
				    
				    space.getNearObjects((IVector2)avatar.getProperty(
						Space2D.PROPERTY_POSITION), new Vector1Int(1), new IFilter()
						{
							public boolean filter(Object obj)
							{
								ISpaceObject fly = (ISpaceObject)obj;
								return ((Boolean)fly.getProperty("flashing")).booleanValue();
							}
						});
					tmp.remove(avatar);
					if(tmp.size()>=flashestoreset)
					{
						clock = resetlevel;
//								System.out.println("Reset: "+avatar.getId());
					}
				}
	
				Map params = new HashMap();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(MoveAction.PARAMETER_POSITION, newpos);
				params.put(MoveAction.PARAMETER_DIRECTION, new Double(newdir));
				params.put(MoveAction.PARAMETER_CLOCK, new Integer(clock));
				space.performSpaceAction("move", params, null);
				
				waitForTick(this);
				return null;
			}
			
			public String toString()
			{
				return "firebug.body()";
			}
		};
		
		waitForTick(step);
	}
}
