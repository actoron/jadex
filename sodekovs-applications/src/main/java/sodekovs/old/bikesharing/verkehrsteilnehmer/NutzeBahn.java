package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.List;
import java.util.Set;

import sodekovs.bikesharing.simulation.BahnStationen;
import sodekovs.old.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.old.bikesharing.container.Weg;
import sodekovs.old.bikesharing.standard.VerkehrsmittelAgent;

/**
 * Plan, bei dem der Agent zu einem Ziel mit der Bahn kommen will Bedingung:
 * Agent ist an Bahnstation Zusicherung: Agent ist dannach an Zielbahnstation
 * (ev. nicht verlässlich... -> fail?) TODO!
 * 
 * @author David Georg Reichelt
 */
public class NutzeBahn extends Plan
{
	private static final long serialVersionUID = 6729796862361056639L;

	public static String LINIE = "linie";
	public static String EINSTEIGEN = "einsteigen";
	public static String AUSSTEIGEN = "aussteigen";

	private ISpaceObject _avatar;
	private Grid2D _space;
	Vector2Double _position;
	private IVector2 _ziel;

	public static String BAHNFAHRT = "bahnfahrt";

	public NutzeBahn()
	{
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getScope().getParent();
//		_space = (Grid2D) app.getSpace(StartSimulationProzess.SPACE);
//		_avatar = _space.getAvatar(getComponentIdentifier());
//		_avatar = SelbstBewegPlan.gibAvatar(getScope().getParent(), this);
		_space = (Grid2D) getBeliefbase().getBelief("space").getFact();
		_avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
	}

	@Override
	public void body()
	{
		_ziel = (IVector2) getParameter(ZielWaehlPlan.ZIEL).getValue();
		_position = (Vector2Double) _avatar.getProperty(Space2D.PROPERTY_POSITION);

		sucheWegUndFahre();
	}

	/**
	 * Sucht einen Weg zum Ziel und fährt dorthin, über einen bestimmten
	 * Umsteigeplan; sollte es zu Fehlern kommen (z.B. Verkehrsmittel bedient
	 * die gewünschte Station nicht mehr), dann wird ein anderer Weg gesucht,
	 * gibt es keinen Weg mit öffentlichen Verkehrsmitteln mehr, schlägt der
	 * Plan fehl
	 */
	private void sucheWegUndFahre()
	{
		if (_position.equals(_ziel))
			return;

		Weg umsteigeplan = BahnStationen.gibInstanz().gibUmsteigePlan(_position, _ziel);
		// System.out.println("Umsteigeplan: " + umsteigeplan);

		if (umsteigeplan != null)
		{
			for (int i = 1; i < umsteigeplan.gibStationen().size(); i++)
			{
				fahren(umsteigeplan.gibStationen().get(i - 1), umsteigeplan.gibStationen().get(i));
			}
		}
		else
		{
			System.out.println("NutzeBahn.sucheWegUndFahre: Umsteigeplan == null -> FAIL, von: " + _position + " zu " + _ziel);
			fail();
		}

	}

	/**
	 * Methode, die den Agenten auf die Ankunft des Öffentliche Verkehrsmittels
	 * warten lässt Es wird geprüft, ob sich der Agent an einer Bahnstation
	 * befindet, falls nicht schlägt der Plan fehl
	 * 
	 * @return Referenz auf das Öffentliche Verkehrsmittel
	 */
	private ISpaceObject warteAufBahn(IVector2 ziel)
	{
		ISpaceObject sbahn = null;

		Vector2Double position = (Vector2Double) _avatar.getProperty(Space2D.PROPERTY_POSITION);
//		Vector2Int positionInt = new Vector2Int((int) (position.getXAsDouble() + 0.5),
//				(int) (position.getYAsDouble() + 0.5));
		// System.out.println("Warte auf Bahn: " + position + " " + positionInt
		// );
		BahnStation bs = BahnStationen.gibInstanz().gibStation( position );
		
		if (bs == null || !(position.getDistance(bs.gibPosition()).getAsDouble() < 0.5))
		{
			System.out.println("Nicht an position! " + bs + " " + position);
			fail(); // Ein Aufruf dieser Funktion, wenn man nicht auf
		}

		boolean da = false;
		// System.out.println("Warte auf bahn...");
		loop: while (!da)
		{
			Set<ISpaceObject> objects = _space.getNearObjects(position, new Vector1Double(0.5));
			for (ISpaceObject object : objects)
			{

				if (object.getType().equals("sbahn"))
				{
					Weg w = (Weg) object.getProperty(LINIE);

					List<IVector2> stationsliste = w.gibStationen();
					if (stationsliste.indexOf(ziel) != -1)
					{
						Boolean offen = (Boolean) object.getProperty(VerkehrsmittelAgent.OFFEN);
						Boolean richtung = (Boolean) object.getProperty(VerkehrsmittelAgent.RICHTUNG);
						if ( ( (stationsliste.indexOf(ziel) < stationsliste.indexOf(_position) && !richtung) || 
								(stationsliste.indexOf(ziel) > stationsliste.indexOf(_position) && richtung) )
								&& offen)
						{
							sbahn = object;
							da = true;
							break loop;
						}

					}
				}
			}
			waitForTick();
		}

		betretVerkehrsmittel(sbahn, true);

		return sbahn;
	}

	/**
	 * Lässt den Verkehrsteilnehmer in das übergebene Verkehrsmittel einsteigen
	 * oder aussteigen, und setzt dabei die Anzahl der Mitfahrer neu
	 * Synchronized, um zu verhindern, dass während des Vorgangs ein Dirty-Read
	 * entsteht
	 * 
	 * @param sbahn
	 *            Verkehrsmittel, in das eingestiegen werden soll
	 * @param betreten
	 *            Ob das Verkehrsmittel Betreten oder verlassen werden soll
	 */
	private synchronized void betretVerkehrsmittel(ISpaceObject sbahn, boolean betreten)
	{
		IMessageEvent nachricht = createMessageEvent(EINSTEIGEN);
		nachricht.getParameterSet(SFipa.RECEIVERS).addValue(_space.getOwner(sbahn.getId()));
		nachricht.getParameter(SFipa.PERFORMATIVE).setValue(SFipa.INFORM);
		if (betreten)
		{
			nachricht.getParameter(SFipa.CONTENT).setValue(EINSTEIGEN);
		}
		else
		{
			nachricht.getParameter(SFipa.CONTENT).setValue(AUSSTEIGEN);
		}

		sendMessage(nachricht);
	}

	/**
	 * Methode, die den Agenten warten lässt, bis die S-Bahn sein gewünschtes
	 * Ziel erreicht
	 */
	private void fahren(IVector2 start, IVector2 ziel)
	{
		// System.out.println("Bahnfahrt: " + start + " " + ziel);
		if (start.equals(ziel))
		{
			return;
		}
		ISpaceObject sbahn = warteAufBahn(ziel);

		// ab hier fahren wir mit der Bimmelbahn ;)
		boolean zielerreicht = false;
		loop: while (!zielerreicht)
		{
			Vector2Double bahnPosition = (Vector2Double) sbahn.getProperty(Space2D.PROPERTY_POSITION);
			_avatar.setProperty(Space2D.PROPERTY_POSITION, bahnPosition.copy());

			Boolean offen = (Boolean) sbahn.getProperty(VerkehrsmittelAgent.OFFEN);
			if (offen)
			{
				if (((IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION)).getDistance(ziel).getAsDouble() < 0.1)
				{
					zielerreicht = true;
					betretVerkehrsmittel(sbahn, false);
					break loop;
				}
			}
			waitForTick();
		}
	}
}
