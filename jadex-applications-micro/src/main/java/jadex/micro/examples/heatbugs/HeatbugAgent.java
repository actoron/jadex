package jadex.micro.examples.heatbugs;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.space2d.action.GetPosition;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IArgument;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The heatbug agent.
 */
public class HeatbugAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The wanted temperature. */
//	protected double ideal_temp;
	
	/** The heat emitted at each step. */
//	protected double output_heat;

	/** The random move chance. */
//	protected double randomchance;
	
	/** The degree of unhappiness (difference between ideal temp and current temp). */
//	protected double unhappiness;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationContext app = getApplicationContext();
		final Grid2D grid = (Grid2D)app.getSpace("mygc2dspace");
		ISpaceObject avatar = grid.getAvatar(getAgentIdentifier());
		
//		unhappiness = Math.abs(ideal_temp - temp);
		final double randomchance = ((Double)avatar.getProperty("randomchance")).doubleValue();
		final double ideal_temp = ((Double)avatar.getProperty("ideal_temp")).doubleValue();
	
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				ISpaceObject avatar = grid.getAvatar(getAgentIdentifier());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				ISpaceObject patch = (ISpaceObject)grid.getNearObjects(mypos, Vector1Int.ZERO, "patch").iterator().next();
				double mytemp = ((Double)patch.getProperty("heat")).doubleValue();

				double unhappiness = ((Double)avatar.getProperty("unhappiness")).doubleValue();
				if(unhappiness>0)
				{
					Set tmp = grid.getNearObjects((IVector2)avatar.getProperty(
						Space2D.PROPERTY_POSITION), new Vector1Int(0), "patch");
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
						if(mytemp<ideal_temp)
						{
							ISpaceObject min = patch;
							double minheat = mytemp;
							for(int i=0; i<neighbors.length; i++)
							{
								double heat = ((Double)neighbors[i].getProperty("heat")).doubleValue();
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
								double heat = ((Double)neighbors[i].getProperty("heat")).doubleValue();
								if(heat>maxheat)
								{
									max = neighbors[i];
									maxheat = heat;
								}
							}
							target = (IVector2)max.getProperty(Space2D.PROPERTY_POSITION);
						}
					}
					
					if(!target.equals(mypos))
					{
						System.out.println("res: "+avatar.getProperty(ISpaceObject.PROPERTY_OWNER)+" "+target);
						Map params = new HashMap();
						params.put(GetPosition.OBJECT_ID, avatar);
						params.put(GetPosition.PARAMETER_POSITION, target);
						grid.performSpaceAction("setpos", params, null);
					}
				}
				
				waitFor(100, this);
			}
		};
		
		waitFor(100, runnable);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
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
		});
	}
}
