package jadex.micro.examples.heatbugs;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IArgument;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

import java.util.Set;

/**
 *  The heatbug agent.
 */
public class HeatbugAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The wanted temperature. */
	protected double ideal_temp;
	
	/** The heat emitted at each step. */
	protected double output_heat;

	/** The random move chance. */
	protected double randomchance;
	
	/** The degree of unhappiness (difference between ideal temp and current temp). */
	protected double unhappiness;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		IApplicationContext app = getApplicationContext();
		Grid2D grid = (Grid2D)app.getSpace("mygc2dspace");
		ISpaceObject avatar = grid.getAvatar(getAgentIdentifier());
		ISpaceObject patch = (ISpaceObject)grid.getNearObjects((IVector2)avatar.getProperty(
			Space2D.PROPERTY_POSITION), Vector1Int.ZERO, "patch").iterator().next();
		double myheat = ((Double)patch.getProperty("heat")).doubleValue();
		double temp = ((Double)patch.getProperty("heat")).doubleValue();
		
		unhappiness = Math.abs(ideal_temp - temp);
		
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
				if(temp<ideal_temp)
				{
					ISpaceObject min = patch;
					double minheat = myheat;
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
					double maxheat = myheat;
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
		}
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
