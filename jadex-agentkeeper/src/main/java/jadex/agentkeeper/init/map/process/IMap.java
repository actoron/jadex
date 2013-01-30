package jadex.agentkeeper.init.map.process;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Constants for field and creature types.
 */
public interface IMap {
	public static final String IMPENETRABLE_ROCK = "impenetrable_rock";
	public static final String ROCK = "rock";
	public static final String REINFORCED_WALL = "reinforced_wall";
	public static final String GOLD = "gold";
	public static final String DIRT_PATH = "dirt_path";
	public static final String CLAIMED_PATH = "claimed_path";
	public static final String GOLD2 = "gold2";
	public static final String GEMS = "gems";
	
	public static final String WATER = "water";
	public static final String LAVA = "lava";
	public static final String HERO = "hero";

	public static final String DUNGEONHEART = "dungeonheart";
	public static final String DUNGEONHEARTCENTER = "dungeonheartcenter";
	public static final String TREASURY = "treasury";
	
	public static final String HATCHERY = "hatchery";
	public static final String HATCHERYCENTER = "hatcherycenter";

	public static final String PORTAL = "portal";
	public static final String PORTALCENTER = "portalcenter";
	
	public static final String TRAININGROOM = "trainingroom";
	public static final String TRAININGROOMCENTER = "trainingroomcenter";
	
	public static final String LIBRARY = "library";
	public static final String LIBRARYCENTER = "librarycenter";
	
	public static final String LAIR = "lair";
	public static final String TORTURE = "torture";
	
	public static final String IMP = "imp";
	public static final String GOBLIN = "goblin";
	public static final String WARLOCK = "warlock";
	public static final String TROLL = "troll";
	
	public static final String[] FIELD_TYPES = {DIRT_PATH, ROCK, GOLD, REINFORCED_WALL, GOLD2, CLAIMED_PATH,  GEMS, WATER, LAVA, HERO,  IMPENETRABLE_ROCK};
	
	public static final String[] BUILDING_TYPES = {DUNGEONHEART, DUNGEONHEARTCENTER, TREASURY, HATCHERY, HATCHERYCENTER, LAIR,  PORTAL, TRAININGROOM, TRAININGROOMCENTER, LIBRARY,  TORTURE};
	
	public static final String[] MOVE_TYPES = {DIRT_PATH, CLAIMED_PATH, GOLD2, LAVA, HERO, TREASURY, HATCHERY, LAIR, PORTAL, TRAININGROOM, LIBRARY, TORTURE, DUNGEONHEART};
	
	public static final String[] CREATURE_TYPES ={IMP, GOBLIN, WARLOCK, TROLL};
	
	static final String[] BREAKABLE_FIELD = {ROCK, REINFORCED_WALL};
	
	
	public static final boolean ROCK_COMPLEX_NEIHBORHOOD = true;
	public static final boolean GOLD_COMPLEX_NEIHBORHOOD = true;
	public static final boolean REINFORCED_COMPLEX_NEIHBORHOOD = true;
	public static final boolean IMPENETRABLE_ROCK_COMPLEX_NEIHBORHOOD = true;
	public static final boolean WATER_COMPLEX_NEIHBORHOOD = true;
	
	
	
	public static final String[] CARE_ABOUT_NEIGHBORS = {ROCK, GOLD, REINFORCED_WALL, IMPENETRABLE_ROCK, WATER, LAVA};
	
	public static final String[] ROCK_NEIGHBORS = {ROCK, GOLD, REINFORCED_WALL, IMPENETRABLE_ROCK};
	
	public static final String[] GOLD_NEIGHBORS = {ROCK, GOLD, REINFORCED_WALL, IMPENETRABLE_ROCK};
	
	public static final String[] REINFORCED_WALL_NEIGHBORS = {ROCK, GOLD, REINFORCED_WALL, IMPENETRABLE_ROCK};
	
	public static final String[] IMPENETRABLE_ROCK_NEIGHBORS = {ROCK, GOLD, REINFORCED_WALL, IMPENETRABLE_ROCK};
	
	public static final String[] WATER_NEIGHBORS = {WATER};
	
	public static final String[] LAVA_NEIGHBORS = {LAVA};
	
	public static final String[] LAIR_NEIGHBORS = BUILDING_TYPES;
	public static final String[] HATCHERY_NEIGHBORS = BUILDING_TYPES;
	public static final String[] TRAINNGROOM_NEIGHBORS = BUILDING_TYPES;
	public static final String[] LIBRARY_NEIGHBORS = BUILDING_TYPES;
	public static final String[] TORTURE_NEIGHBORS = BUILDING_TYPES;
	
	/**
	 * Hash Maps for easier contains-using inside the Code
	 */
	public static final HashMap<String,String[]> NEIGHBOR_RELATIONS = new HashMap<String,String[]>(); 
	
	public static final HashSet<String> FIELD_SET = new HashSet<String>(); 
	
	public static final HashSet<String> BUILDING_SET = new HashSet<String>(); 
	
	public static final HashSet<String> BREAKABLE_FIELD_TYPES = new HashSet<String>(); 
	
	public static final HashSet<String> MOVEABLES = new HashSet<String>(); 
	
	public static final HashMap<String,String> CENTER_TYPES = new HashMap<String,String>(); 

}
