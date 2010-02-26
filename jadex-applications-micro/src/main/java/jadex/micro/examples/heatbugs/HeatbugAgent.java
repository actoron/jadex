package jadex.micro.examples.heatbugs;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Int;
import jadex.bridge.IArgument;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The heatbug agent.
 */
public class HeatbugAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationExternalAccess	app	= (IApplicationExternalAccess)getParent();
		final Grid2D grid = (Grid2D)app.getSpace("mygc2dspace");
		ISpaceObject avatar = grid.getAvatar(getComponentIdentifier());
		
//		unhappiness = Math.abs(ideal_temp - temp);
		final double randomchance = ((Number)avatar.getProperty("random_move_chance")).doubleValue();
		final double ideal_temp = ((Number)avatar.getProperty("ideal_temp")).doubleValue();
//		System.out.println("ideal_temp: "+ideal_temp+" "+getArgument("ideal_temp"));
		
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				ISpaceObject avatar = grid.getAvatar(getComponentIdentifier());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				ISpaceObject patch = (ISpaceObject)grid.getSpaceObjectsByGridPosition(mypos, "patch").iterator().next();
				double mytemp = ((Number)patch.getProperty("heat")).doubleValue();

				double unhappiness = ((Number)avatar.getProperty("unhappiness")).doubleValue();
				if(unhappiness>0)
				{
					Set tmp = grid.getNearObjects((IVector2)avatar.getProperty(
						Space2D.PROPERTY_POSITION), new Vector1Int(1), "patch");
					tmp.remove(patch);
					ISpaceObject[] neighbors = (ISpaceObject[])tmp.toArray(new ISpaceObject[tmp.size()]); 
					
					IVector2 target = null;
					if(Math.random()<randomchance)
					{
		//				for(int tries=0; target==null && tries<10; tries++)
		//				{
							int choice = (int)(Math.random()*neighbors.length);
							IVector2 choicepos = (IVector2)neighbors[choice].getProperty(Space2D.PROPERTY_POSITION);
		//					if(grid.getSpaceObjectsByGridPosition(choicepos, "heatbug")==null)
							target = choicepos;
		//				}
					}
					else
					{
						if(mytemp>ideal_temp)
						{
							ISpaceObject min = patch;
							double minheat = mytemp;
							for(int i=0; i<neighbors.length; i++)
							{
								double heat = ((Number)neighbors[i].getProperty("heat")).doubleValue();
								if(heat<minheat)
								{
									min = neighbors[i];
									minheat = heat;
								}
							}
							target = (IVector2)min.getProperty(Space2D.PROPERTY_POSITION);
						}
						else
						{
							ISpaceObject max = patch;
							double maxheat = mytemp;
							for(int i=0; i<neighbors.length; i++)
							{
								double heat = ((Number)neighbors[i].getProperty("heat")).doubleValue();
								if(heat>maxheat)
								{
									max = neighbors[i];
									maxheat = heat;
								}
							}
							target = (IVector2)max.getProperty(Space2D.PROPERTY_POSITION);
						}
					}
					
//					if(!target.equals(mypos))
					{
//						System.out.println("res: "+avatar.getProperty(ISpaceObject.PROPERTY_OWNER)+" "+target);
						Map params = new HashMap();
						params.put(ISpaceAction.OBJECT_ID, avatar.getId());
						params.put(MoveAction.PARAMETER_POSITION, target);
						grid.performSpaceAction("move", params, null);
					}
				}
				
				waitForTick(this);
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
