package agentkeeper.monster;

import jadex.bdi.runtime.IGoal;

import java.util.List;

import agentkeeper.ai.bdi.basic.GehHinUndArbeit;
import agentkeeper.auftragsverwaltung.Auftrag;
import agentkeeper.auftragsverwaltung.Auftragsverwalter;
import agentkeeper.auftragsverwaltung.Gebaeude;
import agentkeeper.auftragsverwaltung.Gebaudeverwalter;
import agentkeeper.map.InitMapProcess;


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
