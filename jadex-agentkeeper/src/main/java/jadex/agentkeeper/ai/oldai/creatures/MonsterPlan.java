package jadex.agentkeeper.ai.oldai.creatures;

import jadex.agentkeeper.ai.oldai.basic.GehHinUndArbeit;
import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.game.state.missions.Gebaudeverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.bdi.runtime.IGoal;

import java.util.List;



@SuppressWarnings("serial")
public abstract class MonsterPlan extends GehHinUndArbeit{

	@Override
	protected	abstract void aktion();
	
	@Override
	protected void gegnerNaehe( long id )
	{
		System.out.println("Werde geschlagen!");
		IGoal ziel = createGoal( Auftragsverwalter.ANGREIFEN );
		Auftrag a = new Auftrag(Auftragsverwalter.ANGREIFEN , id );
		ziel.getParameter("auftrag").setValue( a );
		dispatchSubgoalAndWait( ziel );
		System.out.println("Kampf beendet");
	}
	
	/**
	 * Sucht das naechste Gebaeude vom Typ typ
	 * 
	 * @return
	 */
	protected List<Gebaeude> gibNaechstesGebaeude( String typ ) {
		Gebaudeverwalter g = (Gebaudeverwalter)grid.getProperty( InitMapProcess.GEBAEUDELISTE );
		List<Gebaeude> gebaeude = g.gibGebaeude( typ );
		
		return gebaeude;
	}

}
