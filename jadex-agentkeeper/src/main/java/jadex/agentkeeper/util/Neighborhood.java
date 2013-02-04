package jadex.agentkeeper.util;

import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


/**
 * 
 * Class that implements the Neighborhood calculation
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 *
 */
public class Neighborhood
{

	public static final int				pos1				= 1;

	public static final int				pos2				= 2;

	public static final int				pos3				= 4;

	public static final int				pos4				= 8;

	public static final int				pos5				= 16;

	public static final int				pos6				= 32;

	public static final int				pos7				= 64;

	public static final int				pos8				= 128;

	public static final Vector2Int		case1				= new Vector2Int(-1, -1);

	public static final Vector2Int		case2				= new Vector2Int(0, -1);

	public static final Vector2Int		case3				= new Vector2Int(1, -1);

	public static final Vector2Int		case4				= new Vector2Int(1, 0);

	public static final Vector2Int		case5				= new Vector2Int(1, 1);

	public static final Vector2Int		case6				= new Vector2Int(0, 1);

	public static final Vector2Int		case7				= new Vector2Int(-1, 1);

	public static final Vector2Int		case8				= new Vector2Int(-1, 0);


	public static final Vector2Int		bigcase1			= new Vector2Int(-2, -2);

	public static final Vector2Int		bigcase2			= new Vector2Int(-2, -1);

	public static final Vector2Int		bigcase3			= new Vector2Int(-2, 0);

	public static final Vector2Int		bigcase4			= new Vector2Int(-2, 1);

	public static final Vector2Int		bigcase5			= new Vector2Int(-2, 2);

	public static final Vector2Int		bigcase6			= new Vector2Int(-1, -2);

	public static final Vector2Int		bigcase7			= new Vector2Int(-1, 2);

	public static final Vector2Int		bigcase8			= new Vector2Int(0, -2);

	public static final Vector2Int		bigcase9			= new Vector2Int(0, 2);

	public static final Vector2Int		bigcase10			= new Vector2Int(1, -2);

	public static final Vector2Int		bigcase11			= new Vector2Int(1, 2);

	public static final Vector2Int		bigcase12			= new Vector2Int(2, -2);

	public static final Vector2Int		bigcase13			= new Vector2Int(2, -1);

	public static final Vector2Int		bigcase14			= new Vector2Int(2, 0);

	public static final Vector2Int		bigcase15			= new Vector2Int(2, 1);

	public static final Vector2Int		bigcase16			= new Vector2Int(2, 2);

	public static final Vector2Int[]	cases				= {case1, case2, case3, case4, case5, case6, case7, case8};

	public static final Vector2Int[]	simpleDirections	= {case2, case4, case6, case8};

	public static final Vector2Int[]	complexDirections	= {case1, case2, case3, case4, case5, case6, case7, case8, bigcase1, bigcase2, bigcase3, bigcase4,
			bigcase5, bigcase6, bigcase7, bigcase8, bigcase9, bigcase10, bigcase11, bigcase12, bigcase13, bigcase14, bigcase15, bigcase16};



	public static boolean isReachable(IVector2 zielpos, Grid2D grid)
	{
		boolean ret = false;
		for(int i = 0; i < simpleDirections.length; i++)
		{
			IVector2 ziel = zielpos.copy().add(simpleDirections[i]);
			SpaceObject thatsme = InitMapProcess.getSolidTypeAtPos(ziel, grid);
			if(InitMapProcess.MOVEABLES.contains(thatsme.getType()))
			{
				return true;
			}
		}
		return ret;
	}
	

	public static boolean isReachableForDestroy(IVector2 zielpos, Grid2D grid)
	{
		boolean ret = false;
		for(int i = 0; i < simpleDirections.length; i++)
		{
			IVector2 ziel = zielpos.copy().add(simpleDirections[i]);
			SpaceObject thatsme = InitMapProcess.getSolidTypeAtPos(ziel, grid);
			if(InitMapProcess.MOVEABLES.contains(thatsme.getType()))
			{
				return true;
			}
			else if(thatsme.getProperty("clicked").equals(true))
			{
				return true;
			}
		}
		return ret;
	}

	public static void updateMyNeighborsSimpleField(IVector2 zielpos, Grid2D grid)
	{
		for(int i = 0; i < simpleDirections.length; i++)
		{

			IVector2 ziel = zielpos.copy().add(simpleDirections[i]);
			SpaceObject thatsme = InitMapProcess.getFieldTypeAtPos(ziel, grid);
			updateMyNeighborvalueBasedOnMyNeighborhood(ziel, thatsme, grid);
		}

	}

	public static void updateMyNeighborsComplexField(IVector2 zielpos, Grid2D grid)
	{
		for(int i = 0; i < cases.length; i++)
		{
			IVector2 ziel = zielpos.copy().add(cases[i]);
			SpaceObject thatsme = InitMapProcess.getFieldTypeAtPos(ziel, grid);
			updateMyNeighborvalueBasedOnMyNeighborhood(ziel, thatsme, grid);

		}
	}


	public static void updateMyNeighborsSimpleBuilding(IVector2 zielpos, Grid2D grid)
	{
		for(int i = 0; i < simpleDirections.length; i++)
		{

			IVector2 ziel = zielpos.copy().add(simpleDirections[i]);
			SpaceObject thatsme = InitMapProcess.getBuildingTypeAtPos(ziel, grid);
			updateMyNeighborvalueBasedOnMyNeighborhood(ziel, thatsme, grid);
		}

	}


	public static void updateMyNeighborsComplexBuilding(IVector2 zielpos, Grid2D grid)
	{
		for(int i = 0; i < cases.length; i++)
		{
			IVector2 ziel = zielpos.copy().add(cases[i]);
			SpaceObject thatsme = InitMapProcess.getBuildingTypeAtPos(ziel, grid);
			updateMyNeighborvalueBasedOnMyNeighborhood(ziel, thatsme, grid);

		}
	}

	private static void updateMyNeighborvalueBasedOnMyNeighborhood(IVector2 ziel, SpaceObject thatsme, Grid2D grid)
	{
		if(thatsme != null)
		{
			String tmpneighborhood = (String)thatsme.getProperty("neighborhood");
			String relations[] = InitMapProcess.NEIGHBOR_RELATIONS.get(thatsme.getType());
			if(relations != null)
			{
				Set<SpaceObject> nearRocks = InitMapProcess.getNeighborBlocksInRange(ziel, 1, grid, relations);

				String newneighborhood = Neighborhood.reCalculateNeighborhood(ziel, nearRocks);

				if(!newneighborhood.equals(tmpneighborhood))
				{

					thatsme.setProperty("neighborhood", newneighborhood);
					SpaceObject thatsmecopy = thatsme;
					
					
					boolean destroyed = grid.destroyAndVerifySpaceObject(thatsme.getId());

					if(destroyed)
					{
						grid.createSpaceObject(thatsmecopy.getType(), thatsmecopy.getProperties(), null);
					}

				}

			}


		}

	}

	/**
	 * Recalculate the Neighborhood for one Block
	 * 
	 * @param iMyPos my Position
	 * @param nearFields the SpaceoObjects around me
	 */
	public static String reCalculateNeighborhood(IVector2 iMyPos, Set<SpaceObject> nearFields)
	{

		Vector2Int myPos = (Vector2Int)iMyPos;
		boolean alternatives = true;
		int ret = 0;
		SpaceObject thatsme = null;
		ArrayList<Integer> neighborcases = new ArrayList<Integer>();
		Iterator<SpaceObject> it = nearFields.iterator();

		// Calculate all the neihborcases
		while(it.hasNext())
		{
			SpaceObject sobj = it.next();

			Vector2Double sobjpos = (Vector2Double)sobj.getProperty(Space2D.PROPERTY_POSITION);
			if(sobjpos.equals(myPos))
			{
				thatsme = sobj;
				if(sobj.getType().equals(InitMapProcess.WATER) || sobj.getType().equals(InitMapProcess.LAVA)
						|| InitMapProcess.BUILDING_SET.contains(sobj.getType()))
				{
					alternatives = false;
				}
			}
			else
			{
				Vector2Double subtract = (Vector2Double)sobjpos.copy().subtract(myPos);

				// We save all the cases in a List.
				Integer wert = calculateNeighborcase(subtract);
				neighborcases.add(wert);
			}

		}

		// Remove execptions
		if(neighborcases.contains(new Integer(3)))
		{
			if(!neighborcases.contains(new Integer(2)) || !neighborcases.contains(new Integer(4)))
			{
				neighborcases.remove(new Integer(3));

			}

		}

		if(neighborcases.contains(new Integer(5)))
		{
			if(!neighborcases.contains(new Integer(4)) || !neighborcases.contains(new Integer(6)))
			{
				neighborcases.remove(new Integer(5));
			}

		}

		if(neighborcases.contains(new Integer(7)))
		{
			if(!neighborcases.contains(new Integer(6)) || !neighborcases.contains(new Integer(8)))
			{
				neighborcases.remove(new Integer(7));
			}

		}

		if(neighborcases.contains(new Integer(1)))
		{
			if(!(neighborcases.contains(new Integer(2))) || !(neighborcases.contains(new Integer(8))))
			{
				neighborcases.remove(new Integer(1));
			}

		}

		// System.out.println("neighborcases" + neighborcases.toString());

		// Now we adress the concrete Tiles:
		for(Integer ncase : neighborcases)
		{
			ret = ret ^ getNeighborValue(ncase);
		}


		// System.out.println("ret als byte " + Integer.toBinaryString(ret));
		String stringret = Integer.toBinaryString(ret);
		return parseTilesets(stringret, alternatives);

	}

	private static String parseTilesets(String neighborhood, boolean alternatives)
	{

		// String ret = "00000000";

		// Parse to correct Length according to the files
		while(neighborhood.length() < 8)
		{
			neighborhood = "0".concat(neighborhood);
		}
		// Special Random Cases
		if(alternatives
				&& (neighborhood.equals("00111110") || neighborhood.equals("10001111") || neighborhood.equals("11100011") || neighborhood.equals("11111000")))
		{
			int rnd = (int)(4 * Math.random());
			switch(rnd)
			{
				case 0:
					neighborhood = neighborhood.concat("a");
					break;
				case 1:
					neighborhood = neighborhood.concat("b");
					break;
				case 2:
					neighborhood = neighborhood.concat("c");
					break;
				case 3:
					neighborhood = neighborhood.concat("d");
					break;

				default:
					break;
			}
		}

		return neighborhood;
	}

	private static Integer calculateNeighborcase(Vector2Double diff)
	{
		if(diff.equals(case1))
			return 1;
		else if(diff.equals(case2))
			return 2;
		else if(diff.equals(case3))
			return 3;
		else if(diff.equals(case4))
			return 4;
		else if(diff.equals(case5))
			return 5;
		else if(diff.equals(case6))
			return 6;
		else if(diff.equals(case7))
			return 7;
		else if(diff.equals(case8))
			return 8;
		return -1;
	}

	private static int getNeighborValue(int pos)
	{
		switch(pos)
		{
			case 1:
				return pos1;
			case 2:
				return pos2;
			case 3:
				return pos3;
			case 4:
				return pos4;
			case 5:
				return pos5;
			case 6:
				return pos6;
			case 7:
				return pos7;
			case 8:
				return pos8;
			default:
				return 0;

		}

	}


}
