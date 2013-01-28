package jadex.agentkeeper.ai.oldai.heroes;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.agentkeeper.ai.oldai.creatures.KreaturenPlan;
import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.game.state.missions.IAuftragsverwalter;
import jadex.bdi.runtime.GoalFailureException;

@SuppressWarnings("serial")
public class AngreifPlan extends KreaturenPlan {
	public static String ABKLINGZEIT = "abklingzeit";

	protected IAuftragsverwalter _auftragsverwalter;
	protected Auftrag _auf;
	private GegnerVerwalter _gegnerverwalter;

	public AngreifPlan() {
		super();
		_auftragsverwalter = (Auftragsverwalter) grid
				.getProperty("auftraege");

		ladAuftrag();
		_gegnerverwalter = (GegnerVerwalter) _avatar.getProperty("auftragsverwalter");
	}

	public void ladAuftrag() {
		_auf = (Auftrag) getParameter("auftrag").getValue();
		System.out.println( "Auftrag: "+_auf );
	}

	@Override
	protected void aktion() {
		try {
			ISpaceObject o = grid.getSpaceObject(_auf.gibId());
			zuschlagen(o);
		} catch (RuntimeException e) {
			// Tritt auf wenn es das Objekt nicht mehr gibt
			_ausfuehr = false;
			System.out.println("Totgeschlagen!" + e.getMessage());
		}
	}
	
	protected void gegnerNaehe( long id )
	{
		
	}

	/**
	 * Schlaegt auf o drauf, wenn man in der Naehe ist
	 * 
	 * @param o
	 */
	private void zuschlagen(ISpaceObject o) {
		_mypos = (Vector2Double) _avatar.getProperty("position");
		IVector2 pos = (IVector2) o.getProperty("position");


		Integer abklingzeit = (Integer) _avatar.getProperty(ABKLINGZEIT);
		if (abklingzeit > 0) {
			abklingzeit -= 1;
			_avatar.setProperty(ABKLINGZEIT, abklingzeit);
		}
		

		if (istAnPos(pos, false) && abklingzeit == 0) {

			int leben = (Integer) o.getProperty("leben");

			o.setProperty("leben", new Integer(leben - 1));

			o.setProperty("geschlagen", true);

			waitForTick();

			o.setProperty("geschlagen", false);

			_avatar.setProperty(ABKLINGZEIT, new Integer(2));
		} else {
			try {
				erreicheEinheit(o);
			} catch (GoalFailureException g) {
				_ausfuehr = false;
				System.out.println("Angreifplan beendet");
			}
			// System.out.println("Bewegungsplan beendet, ID: "+_avatar.getId()
			// );
		}
	}
	
	@Override
	public void aborted()
	{
		_gegnerverwalter.istTod( (String)_avatar.getId().toString() );
	}

}
