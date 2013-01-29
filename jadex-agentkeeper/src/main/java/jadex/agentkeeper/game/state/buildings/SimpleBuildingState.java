package jadex.agentkeeper.game.state.buildings;

import jadex.agentkeeper.init.map.process.InitMapProcess;

import java.util.HashMap;


/**
 * Just a first pre-implementation of the Building State, mainly for the GUI
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class SimpleBuildingState
{
	
	private HashMap<String, Integer> buildings = new HashMap<String, Integer>();
	
	
	
	public SimpleBuildingState()
	{

		for(int i = 0; i<InitMapProcess.CREATURE_TYPES.length; i++ )
		{
			this.buildings.put(InitMapProcess.CREATURE_TYPES[i], 0);
		}

	}

	public void addBuilding(String type)
	{
		int counter = buildings.get(type);
		counter++;
		buildings.put(type, counter);
	}
	
	public void removeBuilding(String type)
	{
		int counter = buildings.get(type);
		counter--;
		buildings.put(type, counter);
	}
	
	public int getBuildingCount(String type)
	{
		return buildings.get(type);
	}

	
	

}
