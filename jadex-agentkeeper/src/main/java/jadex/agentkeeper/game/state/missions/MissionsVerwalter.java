package jadex.agentkeeper.game.state.missions;

import java.util.LinkedList;
import java.util.List;
import jadex.extension.envsupport.environment.RoundBasedExecutor;
import jadex.extension.envsupport.environment.space2d.*;
import jadex.extension.envsupport.environment.RoundBasedExecutor;
import jadex.extension.envsupport.math.*;
import jadex.extension.envsupport.dataview.*;
import jadex.extension.envsupport.observer.perspective.*;
/**
 * Verwaltet die Missionen des Nutzers
 * Singleton, da es nur einen Nutzer gibt
 * @author 8reichel
 *
 */
public class MissionsVerwalter 
{
	List<Mission> _missionen;
	
	public MissionsVerwalter(  )
	{
		_missionen = new LinkedList<Mission>();
	}
	
	
	public boolean istZuende()
	{
		for ( Mission m : _missionen )
		{
			m.teste();
			if ( !m.istErfuellt() )
			{
				return false;
			}
		}
		return true;
	}

	public void neueMission(Mission mission) {
		_missionen.add( mission );
	}
}
