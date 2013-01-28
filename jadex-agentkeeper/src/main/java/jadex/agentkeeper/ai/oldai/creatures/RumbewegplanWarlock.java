package jadex.agentkeeper.ai.oldai.creatures;

import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.bdi.runtime.IGoal;

/**
 * 
 * @author 7willuwe
 * @author 8reichel
 * 
 */
@SuppressWarnings("serial")
public class RumbewegplanWarlock extends AbstractRumbewegplan {

	public static double STANDARDVERBRAUCH = 0.01;

	

	public RumbewegplanWarlock() {
		super();
	}


	protected void testAuftraege( ) {
		double loyalitaet = (Double) getBeliefbase().getBelief("loyalitaet").getFact();
		double hunger = (Double) getBeliefbase().getBelief("hunger").getFact();
		double schlaf = (Double) getBeliefbase().getBelief("schlaf").getFact();
		double stimmung = (Double) getBeliefbase().getBelief("stimmung").getFact();

		boolean hungrig = hunger > 5.0;
		boolean muede = schlaf < 2.0;
		boolean wach = schlaf > 5.0;
		boolean froehlich = stimmung > 5.0;
		boolean ausgeglichen = stimmung > 3.0;

		// Unter dieser Bedingung haut das Monster wieder ab:
		if ((loyalitaet < 0.0)) {
			killAgent();
		}

		if (hungrig) {
			IGoal issWas = createGoal("issWas");
			dispatchSubgoalAndWait(issWas);
		}

		if (muede) {
			IGoal schlafen = createGoal("schlafen");
			dispatchSubgoalAndWait(schlafen);
		}
		// Wenn er sehr wach ist geht wird gelernt
		if (wach && !hungrig && ausgeglichen) {
			IGoal lernen = createGoal("lernen");
			dispatchSubgoalAndWait(lernen);
		}
		else
			if (!muede && !hungrig && froehlich) {
				IGoal trainieren = createGoal("trainieren");
				dispatchSubgoalAndWait(trainieren);
			}
		
		testeEinheiten();
	}


	@Override
	protected void gegnerNaehe(long id) {
		IGoal ziel = createGoal( Auftragsverwalter.ANGREIFEN );
		Auftrag a = new Auftrag(Auftragsverwalter.ANGREIFEN , id );
		ziel.getParameter("auftrag").setValue( a );
		dispatchSubgoalAndWait( ziel );
		
	}
	
//	protected void testeEinheiten()
//	{
//		for ( Object o : _space.getNearObjects( _mypos, new Vector1Int(2)) )
//		{
//			if ( o instanceof ISpaceObject )
//			{
//				ISpaceObject einheit = (ISpaceObject) o;
//				if ( !einheit.getType().equals( "field" ) )
//				{
//
//					Integer spieler = (Integer) einheit.getProperty( "spieler");
//					Vector2Double position =  (Vector2Double) einheit.getProperty("position");
//					Vector2Int temp = new Vector2Int( position.getXAsInteger(), position.getYAsInteger() );
//					if ( spieler == 2 )
//					{
//						System.out.println("Will totschlagen: " + einheit.getId() );
//						
//						IGoal ziel = createGoal( Auftragsverwalter.ANGREIFEN );
//						Auftrag a = new Auftrag(Auftragsverwalter.ANGREIFEN , (Long)einheit.getId() );
//						ziel.getParameter("auftrag").setValue( a );
//						dispatchSubgoalAndWait( ziel );
//					}
//				}
//			}
//		}
//	}

}
