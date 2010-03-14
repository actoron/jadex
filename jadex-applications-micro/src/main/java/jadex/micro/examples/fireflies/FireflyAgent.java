package jadex.micro.examples.fireflies;

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
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The firefly agent.
 */
public class FireflyAgent extends MicroAgent
{
	//-------- attributes --------
	
//	/** The probability of a random move. */
//	protected double randomchance;
//	
//	/** The desired temperature. */
//	protected double ideal_temp;
//	
//	/** The current temperature. */
//	protected double mytemp;
//	
//	/** The current unhappiness. */
//	protected double unhappiness;


	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationExternalAccess	app	= (IApplicationExternalAccess)getParent();
		final ContinuousSpace2D space = (ContinuousSpace2D)app.getSpace("mygc2dspace");
		ISpaceObject avatar = space.getAvatar(getComponentIdentifier());
		
		Runnable runnable = new Runnable()
		{
			public void run()
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
				double factor = 1;
				double rotchange = Math.random()*Math.PI/factor-Math.PI/2/factor;
				double newdir = dir+rotchange;
				if(newdir<0)
					newdir+=Math.PI*2;
				else if(newdir>Math.PI*2)
					newdir-=Math.PI*2;
				// convert to vector
				// normally x=cos(dir) and y=sin(dir)
				// here 0 degree is 12 o'clock and the rotation right
//				double x = Math.sin(newdir);
//				double y = -Math.cos(newdir);
				double x = Math.sin(newdir);
				double y = Math.cos(newdir);
				double stepwidth = 1;
				IVector2 newdirvec = new Vector2Double(x*stepwidth, y*stepwidth);
				
				IVector2 newpos = mypos.copy().add(newdirvec);
				SetPosition setpos = new SetPosition();
				Map params = new HashMap();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(GetPosition.PARAMETER_POSITION, newpos);
//				space.performSpaceAction("move", params, null);
				space.performSpaceAction("move", params);
				
				// Hack!!! use action
				avatar.setProperty("direction", new Double(newdir));
				
				// Increment internal counter
				clock++;
				if(clock == cyclelength)
					clock = 0;
				
				if(clock>window && clock>=threshold)
				{
					// Look
					// if count turtles in-radius 1 with [color = yellow] >= flashes-to-reset
				    // [ set clock reset-level ]
				    Set tmp = space.getNearObjects((IVector2)avatar.getProperty(
						Space2D.PROPERTY_POSITION), new Vector1Int(2), "firefly");
					tmp.remove(avatar);
					ISpaceObject[] neighbors = (ISpaceObject[])tmp.toArray(new ISpaceObject[tmp.size()]); 
					for(int i=0; i<neighbors.length; i++)
					{
						if(!((Boolean)neighbors[i].getProperty("glow")).booleanValue())
						{
							tmp.remove(neighbors[i]);
						}
					}
					if(tmp.size()>=flashestoreset)
					{
						clock = resetlevel;
					}
				}
	
				// Hack!!! use action
				avatar.setProperty("clock", new Integer(clock));
				
				waitForTick(this);
			}
			
			public String toString()
			{
				return "firebug.body()";
			}
		};
		
		waitForTick(runnable);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		// todo: remove arguments, the values are directly taken 
		// from the avatar.
		return new MicroAgentMetaInfo("A heat bug emits heat and " +
			"moves towards a point with ideal temperature.", 
			new String[0], new IArgument[]{
			new IArgument()
			{
				public Object getDefaultValue(String configname)
				{
					return new Double(0.5);
				}
				public String getDescription()
				{
					return "Ideal temperature.";
				}
				public String getName()
				{
					return "ideal_temp";
				}
				public String getTypename()
				{
					return "double";
				}
				public boolean validate(String input)
				{
					return true;
				}
			},
			new IArgument()
			{
				public Object getDefaultValue(String configname)
				{
					return new Double(0.1);
				}
				public String getDescription()
				{
					return "Output heat.";
				}
				public String getName()
				{
					return "output_heat";
				}
				public String getTypename()
				{
					return "double";
				}
				public boolean validate(String input)
				{
					return true;
				}
			}
		}, null, null);
	}
}
