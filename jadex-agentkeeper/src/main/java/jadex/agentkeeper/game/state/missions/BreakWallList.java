package jadex.agentkeeper.game.state.missions;

import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.agentkeeper.util.Neighborhood;
import jadex.agentkeeper.view.selection.SelectionArea;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;





public class BreakWallList
{
	private HashMap<Vector2Int, Auftrag> tiles_notreachable;
	
	private ArrayList<Auftrag> reachable_list;

	private Grid2D	grid;
	
	public BreakWallList(Grid2D grid)
	{
		this.grid = grid;
		this.tiles_notreachable = new HashMap<Vector2Int, Auftrag>();

		this.reachable_list = new ArrayList<Auftrag>();
	}
	
	public synchronized Auftrag getClosest( Vector2Int position )
	{
		Auftrag tmpauftrag = null;
		if ( reachable_list.size() > 0 )
		{
			Vector1Double dist = new Vector1Double(200000.0);
			for (Auftrag auf : reachable_list ) {
				Vector2Int ziel = auf.gibZiel();
				Vector1Double tmpdist = (Vector1Double) ziel.getDistance(position);
				if ( tmpdist.less(dist) )
				{
					dist = tmpdist;
					tmpauftrag = auf;
				}
			}
			reachable_list.remove(tmpauftrag);
		}
		else
		{

		}

		return tmpauftrag;
			
	}

	public boolean newBreakWalls(SelectionArea area)
	{
		boolean ret = false;
		Vector2Int endvector = area.getWorldend();
		Vector2Int startvector = area.getWorldstart();
		
		ArrayList<Auftrag> tmplist = new ArrayList<Auftrag>();
		
		
		ArrayList<SpaceObject> tmpobjects = new ArrayList<SpaceObject>();
		
		HashMap<Vector2Int, Auftrag> tmptiles_notreachable = new HashMap<Vector2Int, Auftrag>();
		
		for(int x = startvector.getXAsInteger() ; x<=endvector.getXAsInteger(); x++)
		{
			for(int y = startvector.getYAsInteger() ; y<=endvector.getYAsInteger(); y++)
			{

				Vector2Int aktpos = new Vector2Int(x,y);
				boolean reach = Neighborhood.isReachableForDestroy(aktpos, grid);
				if(reach)
				{
					ret = true;
					
				}
				SpaceObject fieldtype = InitMapProcess.getFieldTypeAtPos(aktpos, grid);
				if(fieldtype!=null&&InitMapProcess.BREAKABLE_FIELD_TYPES.contains(fieldtype.getType()))
				{
					tmpobjects.add(fieldtype);
					Auftrag auf = new Auftrag(Auftragsverwalter.WANDABBAU, aktpos);
					if(Neighborhood.isReachable(aktpos, grid))
					{
						tmplist.add(auf);
						
					}
					else
					{
						tmptiles_notreachable.put(aktpos, auf);
						
					}
				}
			}
		}
		
		if(ret)
		{
			for(SpaceObject ob : tmpobjects)
			{
				Map props = ob.getProperties();
				props.put("clicked", true);
				String type = ob.getType();
				grid.createSpaceObject(type, props, null);
				grid.destroySpaceObject(ob.getId());
			}
			reachable_list.addAll(tmplist);
			tiles_notreachable.putAll(tmptiles_notreachable);
		}
		
		return ret;

	}

	public synchronized void updatePosition(IVector2 position)
	{
		for(int i = 0; i < Neighborhood.simpleDirections.length; i++)
		{
			Vector2Int ziel = (Vector2Int)position.copy().add(Neighborhood.simpleDirections[i]);
			if(tiles_notreachable.containsKey(ziel)&&Neighborhood.isReachable(ziel, grid))
			{
				Auftrag auf = tiles_notreachable.get(ziel);
				reachable_list.add(auf);
				tiles_notreachable.remove(ziel);
				
			}

			
		}
		

		
	}

}
