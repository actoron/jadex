package jadex.micro.examples.heatbugs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Diffusion process.
 */
public class DiffusionProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last tick. */
	protected double lasttick;
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space)
	{
		this.lasttick	= clock.getTick();

		boolean random_init  = getProperty("random_init")!=null? 
			((Boolean)getProperty("random_init")).booleanValue(): false;

		// Initialize the field.
		
		Space2D grid = (Space2D)space;
		int sizex = grid.getAreaSize().getXAsInteger();
		int sizey = grid.getAreaSize().getYAsInteger();
		
		for(int x=0; x<sizex; x++)
		{
			for(int y=0; y<sizey; y++)
			{
				Map props = new HashMap();
				if(random_init)
				{
					double heat = Math.random()*100;
					props.put("heat", Double.valueOf(heat));
				}
				props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
				grid.createSpaceObject("patch", props, null);
			}
		}
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
//		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
//		System.out.println("process called: "+space);
		
		Grid2D grid = (Grid2D)space;
		
		long rate = getProperty("rate")!=null? 
			((Number)getProperty("rate")).longValue(): 1;
		double diffusion = getProperty("diffusion_rate")!=null? 
			((Number)getProperty("diffusion_rate")).doubleValue(): 0.1;
		double cooling = getProperty("evaporation_rate")!=null? 
			((Number)getProperty("evaporation_rate")).doubleValue(): 0.1;
		
		if(lasttick+rate<clock.getTick())
		{
			lasttick += rate;
			int sizex = grid.getAreaSize().getXAsInteger();
			int sizey = grid.getAreaSize().getYAsInteger();
			
			double[][] adds = new double[sizex][sizey];
			for(int x=0; x<sizex; x++)
			{
				for(int y=0; y<sizey; y++)
				{
					ISpaceObject patch = (ISpaceObject)grid.getSpaceObjectsByGridPosition(new Vector2Int(x,y), "patch").iterator().next();
					Set neighbors = grid.getNearObjects(new Vector2Int(x,y), new Vector1Int(1), "patch");
					neighbors.remove(patch);
					
					double myoldheat = ((Double)patch.getProperty("heat")).doubleValue();
					double mysub = myoldheat*diffusion;
					double otheradd = mysub/(neighbors.size());

					adds[x][y] -= mysub;
					
					for(Iterator it=neighbors.iterator(); it.hasNext(); )
					{
						ISpaceObject neighbor = (ISpaceObject)it.next();
						IVector2 otherpos = (IVector2)neighbor.getProperty(Space2D.PROPERTY_POSITION);
						adds[otherpos.getXAsInteger()][otherpos.getYAsInteger()] += otheradd;
					}
//					System.out.println("cells: "+neighbors.length);
				}
			}
			
//			double sum = 0;
			for(int x=0; x<sizex; x++)
			{
				for(int y=0; y<sizey; y++)
				{
					ISpaceObject patch = (ISpaceObject)grid.getSpaceObjectsByGridPosition(new Vector2Int(x,y), "patch").iterator().next();
					double oldheat = ((Double)patch.getProperty("heat")).doubleValue();
					double cool = oldheat*cooling;
					double newheat = Math.max(0, oldheat+adds[x][y]-cool);
					patch.setProperty("heat", Double.valueOf(newheat));
//					sum += newheat;
				}
			}		
//			System.out.println("Sum is: "+sum);
		}
	}
}
