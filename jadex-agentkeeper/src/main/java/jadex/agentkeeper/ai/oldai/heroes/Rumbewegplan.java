package jadex.agentkeeper.ai.oldai.heroes;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.agentkeeper.ai.oldai.creatures.AbstractRumbewegplan;
import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;

/**
 * Der rumbewegplan besch�ftigt sich mit den IMPs die gerade keinen Auftrag
 * haben, welche rnd-m��ig runlaufen und nebenbei die Umgebung Scannen (dabei
 * Auftrage generien sofern es etwas zu tun gibt und st�ndig nachfragen ob es
 * neue Auftrage gibt.
 * 
 * @author 7willuwe
 * @author 8reichel
 * 
 */
@SuppressWarnings("serial")
public class Rumbewegplan extends AbstractRumbewegplan {

	private GegnerVerwalter _gegnerverwalter;

	public Rumbewegplan() { 
	
		_gegnerverwalter = (GegnerVerwalter) _avatar.getProperty("auftragsverwalter");
//		System.out.println(_avatar.getId().toString() );
		_gegnerverwalter.registriereGegner( _avatar.getId().toString() );
		
		_verbrauchsgrad = 0;
	}

	protected void testAuftraege( ) {
		Auftrag auftrag = _gegnerverwalter.gibDichtestenAuftrag( InitMapProcess.convertToIntPos(_mypos));
		if (auftrag != null) {
			_gegnerverwalter.setzBearbeitet( auftrag );
			String typ = auftrag.gibTyp();
			try {
				if (typ.equals(Auftragsverwalter.KAPUTTMACHEN)) {
					IGoal neu = createGoal(Auftragsverwalter.KAPUTTMACHEN);
					neu.getParameter("auftrag").setValue(auftrag);
					dispatchSubgoalAndWait(neu);
				}
				
				if ( typ.equals( Auftragsverwalter.ANGREIFEN ) )
				{
					IGoal ziel = createGoal( Auftragsverwalter.ANGREIFEN );
					ziel.getParameter("auftrag").setValue( auftrag );
					dispatchSubgoalAndWait( ziel );
				}
			}
			catch (GoalFailureException e) {
//				System.out.println("Plan fehlgeschlagen, weiter gehts!!!!!");
			}
		}
		
//		System.out.println("Zielpos: "+_mypos );
		// Wie sieht meine Umgebung aus? Muss etwas getan werden?
		testUmgebung( InitMapProcess.convertToIntPos(_mypos));

		Vector2Int richtungen[] = best4Richtungen(_mypos.copy().getXAsInteger(), _mypos.copy().getYAsInteger());

		for (Vector2Int vector : richtungen) {
			testUmgebung( vector);
		}
	}

	public void testUmgebung( Vector2Int zielpos ) {
		
		for (Object o : grid.getSpaceObjectsByGridPosition(zielpos, "field")) {
			if (o instanceof ISpaceObject) {
				ISpaceObject feld = (ISpaceObject) o;
				Object property = feld.getProperty("type");
				if (property instanceof String) {
					String propstring = (String) property;
					if (propstring.equals(InitMapProcess.CLAIMED_PATH) ||
					    propstring.equals(InitMapProcess.DUNGEONHEART ) ||
					    propstring.equals(InitMapProcess.DUNGEONHEARTCENTER) ||
					    propstring.equals(InitMapProcess.HATCHERY) ||
					    propstring.equals(InitMapProcess.LAIR) ||
					    propstring.equals(InitMapProcess.LIBRARY) ) {
						_gegnerverwalter.neuerAuftrag(Auftragsverwalter.KAPUTTMACHEN, zielpos, propstring);
//						System.out.println("Kaputtmachen: "+propstring);
					}

				}
			}
			else {
				// System.out.println("Kein Spaceobject...");
			}
		}
		
		for ( Object o : grid.getNearObjects(zielpos, new Vector1Int(2)) )
		{
			if ( o instanceof ISpaceObject )
			{
				ISpaceObject einheit = (ISpaceObject) o;
				if ( !einheit.getType().equals( "field" ) )
				{

					Integer spieler = (Integer) einheit.getProperty( "spieler");
					if ( spieler == 1 )
					{
//						System.out.println("Will totschlagen: " + einheit.getId() );
						_gegnerverwalter.neuerAuftrag(Auftragsverwalter.ANGREIFEN, (Long)einheit.getId() );
					}
				}
			}
		}
	}

	private Vector2Int[] best4Richtungen(int vect_x, int vect_y) {
		Vector2Int n = new Vector2Int(vect_x, vect_y - 1);
		Vector2Int o = new Vector2Int(vect_x + 1, vect_y);
		Vector2Int s = new Vector2Int(vect_x, vect_y + 1);
		Vector2Int w = new Vector2Int(vect_x - 1, vect_y);

		Vector2Int richtungen[] = { n, o, s, w };

		return richtungen;
	}

	@Override
	protected void gegnerNaehe(long id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void aborted()
	{
		_gegnerverwalter.istTod( _avatar.getId().toString() );
	}
//	@Override
//	public void 
//	{
//		
//	}
}
