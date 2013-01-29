package jadex.agentkeeper.init.map.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.agentkeeper.ai.oldai.heroes.GegnerVerwalter;
import jadex.agentkeeper.game.state.buildings.SimpleBuildingState;
import jadex.agentkeeper.game.state.creatures.SimpleCreatureState;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.game.state.missions.Gebaudeverwalter;
import jadex.agentkeeper.game.state.missions.MissionsVerwalter;
import jadex.agentkeeper.game.state.player.SimplePlayerState;
import jadex.agentkeeper.game.userinput.UserEingabenManager;
import jadex.agentkeeper.util.ISpaceObjectStrings;
import jadex.agentkeeper.util.ISpaceStrings;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

public abstract class AInitMapProcess extends SimplePropertyObject implements ISpaceProcess, IMap, ISpaceObjectStrings {

	public static final String GEBAEUDELISTE = "gebaeudeliste";

	public static Map<String, String> imagenames;

	public static Gebaudeverwalter gebaeuedeverwalter;

	public static int monsteressverbrauch;

	public static Vector2Double portalort;
	
	public SimpleCreatureState creatureState;
	
	public SimpleBuildingState buildingState;
	
	public SimplePlayerState playerState;
	
	public UserEingabenManager uem;
	public MissionsVerwalter mv;

	static {
		gebaeuedeverwalter = new Gebaudeverwalter();
		portalort = new Vector2Double(12, 19);
		monsteressverbrauch = 4;

		imagenames = new HashMap<String, String>();

		imagenames.put("Ob", IMPENETRABLE_ROCK);
		imagenames.put("Oc", ROCK);
		imagenames.put("1B", REINFORCED_WALL);
		imagenames.put("Og", GOLD);
		imagenames.put("Oh", GEMS);

		imagenames.put("Od", DIRT_PATH);
		imagenames.put("1A", CLAIMED_PATH);
		imagenames.put("Oe", WATER);
		imagenames.put("Of", LAVA);
		imagenames.put("Oh", HERO);

		imagenames.put("1G", DUNGEONHEART);
		imagenames.put("1Z", DUNGEONHEARTCENTER);
		imagenames.put("1C", TREASURY);
		imagenames.put("1F", HATCHERY);
		imagenames.put("ZF", HATCHERYCENTER);
		imagenames.put("1D", LAIR);
		imagenames.put("1P", PORTAL);
		imagenames.put("1I", TRAININGROOM);

		imagenames.put("1L", LIBRARY);
		imagenames.put("1X", TORTURE);
		
		NEIGHBOR_RELATIONS.put(ROCK, ROCK_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(GOLD, GOLD_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(REINFORCED_WALL, REINFORCED_WALL_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(IMPENETRABLE_ROCK, IMPENETRABLE_ROCK_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(WATER, WATER_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(LAVA, LAVA_NEIGHBORS);
		NEIGHBOR_RELATIONS.put(LAIR, BUILDING_TYPES);
		NEIGHBOR_RELATIONS.put(TRAININGROOM, BUILDING_TYPES);
		NEIGHBOR_RELATIONS.put(LIBRARY, BUILDING_TYPES);
		NEIGHBOR_RELATIONS.put(TORTURE, BUILDING_TYPES);
		NEIGHBOR_RELATIONS.put(HATCHERY, BUILDING_TYPES);
		NEIGHBOR_RELATIONS.put(TREASURY, BUILDING_TYPES);
		
		
		
		for(int i = 0; i<FIELD_TYPES.length; i++ )
		{
			FIELD_SET.add(FIELD_TYPES[i]);
		}
		
		for(int i = 0; i<BUILDING_TYPES.length; i++ )
		{
			BUILDING_SET.add(BUILDING_TYPES[i]);
		}
		
		for(int i = 0; i<BREAKABLE_FIELD.length; i++ )
		{
			BREAKABLE_FIELD_TYPES.add(BREAKABLE_FIELD[i]);
		}
		
		for(int i = 0; i<MOVE_TYPES.length; i++ )
		{
			MOVEABLES.add(MOVE_TYPES[i]);
		}
		
		CENTER_TYPES.put(HATCHERY, HATCHERYCENTER);
		CENTER_TYPES.put(PORTAL, PORTALCENTER);
		CENTER_TYPES.put(TRAININGROOM, TRAININGROOMCENTER);
		CENTER_TYPES.put(LIBRARY, LIBRARYCENTER);

	}

	// -------- attributes --------

	/** The last tick. */
	protected double lasttick;
	
	
	protected void loadAndSetupMissions(Grid2D grid) {
		// grid.getBorderMode();
		// Initialize the field.
		try {
			Auftragsverwalter auftragsverwalter = new Auftragsverwalter(grid);



			grid.setProperty("auftraege", auftragsverwalter);
			

			
			uem = new UserEingabenManager(grid);
			mv = new MissionsVerwalter();

			grid.setProperty("missionsverwalter", mv);
			grid.setProperty("uem", uem);
			
			this.creatureState = new SimpleCreatureState();
			this.playerState = new SimplePlayerState(1);
			grid.setProperty(ISpaceStrings.CREATURE_STATE, this.creatureState);
			grid.setProperty(ISpaceStrings.BUILDING_STATE, this.buildingState);
			grid.setProperty(ISpaceStrings.PLAYER_STATE, this.playerState);

		}
		 catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space) {
		 System.out.println("- - - Init Map Process done - - -");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space) {
		// System.out.println("process called: " + space);
	}

	public static Vector2Int convertToIntPos(IVector2 pos )
	{
		int xrund = (int)Math.round( pos.getXAsDouble() );
		int yrund = (int)Math.round( pos.getYAsDouble() );
		Vector2Int temp = new Vector2Int( xrund, yrund );
		return temp;
	}

	public static Set<SpaceObject> getNeighborBlocksInRange(IVector2 ziel,
			int range, Grid2D grid, String types[]) {
			if(types!=null)
			{
				return grid.getNearGridObjects(ziel, range, types);
			}
			return null;
				

	}
	
	public static SpaceObject getSolidTypeAtPos(IVector2 pos, Grid2D gridext)
	{
		SpaceObject ret = null;
		ret = getFieldTypeAtPos(pos, gridext);
		if(ret == null)
		{
			ret = getBuildingTypeAtPos(pos, gridext);
		}
		return ret;
	}
	
	public static SpaceObject getFieldTypeAtPos(IVector2 pos, Grid2D gridext)
	{
		for(int i = 0; i<FIELD_TYPES.length; i++ )
		{
			Collection sobjs = gridext.getSpaceObjectsByGridPosition(pos, FIELD_TYPES[i]);
			if(sobjs!=null)
			{
				return (SpaceObject) sobjs.iterator().next();
			}
				
			}
		return null;
	}
	
	
	public static SpaceObject getBuildingTypeAtPos(IVector2 pos, Grid2D gridext)
	{
		for(int i = 0; i<BUILDING_TYPES.length; i++ )
		{
			Collection sobjs = gridext.getSpaceObjectsByGridPosition(pos, BUILDING_TYPES[i]);
			if(sobjs!=null)
			{
				return (SpaceObject) sobjs.iterator().next();
			}
				
			}
		return null;
	}
	
	public static boolean isMoveable(IVector2 pos, Grid2D gridext)
	{
		for(int i = 0; i<MOVE_TYPES.length; i++ )
		{
			Collection sobjs = gridext.getSpaceObjectsByGridPosition(pos, MOVE_TYPES[i]);
			if(sobjs!=null)
			{
				return true;
			}
				
			}
		return false;
	}

}
