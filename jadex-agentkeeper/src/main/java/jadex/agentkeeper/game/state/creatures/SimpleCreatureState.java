package jadex.agentkeeper.game.state.creatures;

import jadex.agentkeeper.init.map.process.InitMapProcess;

import java.util.HashMap;


/**
 * Just a first pre-implementation of the Creature State, mainly for the GUI
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class SimpleCreatureState
{
	
	private HashMap<String, Integer> creatures = new HashMap<String, Integer>();
	
	
	
	public SimpleCreatureState()
	{

		for(int i = 0; i<InitMapProcess.CREATURE_TYPES.length; i++ )
		{
			this.creatures.put(InitMapProcess.CREATURE_TYPES[i], 0);
		}

	}

	public void addCreature(String type)
	{
		int counter = creatures.get(type);
		counter++;
		creatures.put(type, counter);
	}
	
	public void removeCreature(String type)
	{
		int counter = creatures.get(type);
		counter--;
		creatures.put(type, counter);
	}
	
	public int getCreatureCount(String type)
	{
		return creatures.get(type);
	}

	
	

}
