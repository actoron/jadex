package jadex.bdi.examples.gameoflife;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 *  Environment process for creating wastes.
 */
public class GameOfLifeProcess extends SimplePropertyObject implements ISpaceProcess
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
		
		// Initialize the field.
		Space2D grid = (Space2D)space;
		int sizex = grid.getAreaSize().getXAsInteger();
		int sizey = grid.getAreaSize().getYAsInteger();
		
		for(int x=0; x<sizex; x++)
		{
			for(int y=0; y<sizey; y++)
			{
				Map props = new HashMap();
				Boolean alive = new Boolean(Math.random()>0.7);
				props.put("alive", alive);
				props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
				grid.createSpaceObject("cell", props, null);
			}
		}
//		System.out.println("create waste process started.");
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
		
		if(lasttick+rate<clock.getTick())
		{
			lasttick += rate;
			int sizex = grid.getAreaSize().getXAsInteger();
			int sizey = grid.getAreaSize().getYAsInteger();
			
			boolean[][] alive = new boolean[sizex][sizey];
			for(int x=0; x<sizex; x++)
			{
				for(int y=0; y<sizey; y++)
				{
					ISpaceObject cell = (ISpaceObject)grid.getSpaceObjectsByGridPosition(new Vector2Int(x,y), "cell").iterator().next();
					ISpaceObject[] neighbors = grid.getNearObjects(new Vector2Int(x,y), new Vector1Int(1), "cell");
					int nbcnt = 0;
					for(int i=0; i<neighbors.length; i++)
					{
						if(((Boolean)neighbors[i].getProperty("alive")).booleanValue()
							&& !cell.equals(neighbors[i]))
							nbcnt++;
					}
					
					if(!((Boolean)cell.getProperty("alive")).booleanValue())
					{
						// A dead cell with 3 living neighbors is reborn.
						if(nbcnt==3)
							alive[x][y] = true;
					}
					else
					{
						// Living cell with less than 2 neighbors dies
						// Living cell with more than 3 neighbors dies
						
						// Living cell with 2 or 3 neighbors lives on
						if(nbcnt==2 || nbcnt==3)
							alive[x][y] = true;
					}
				}
			}
			
			for(int x=0; x<sizex; x++)
			{
				for(int y=0; y<sizey; y++)
				{
					ISpaceObject cell = (ISpaceObject)grid.getSpaceObjectsByGridPosition(new Vector2Int(x,y), "cell").iterator().next();
					cell.setProperty("alive", alive[x][y]? Boolean.TRUE: Boolean.FALSE);
				}
			}			
		}
	}
}
