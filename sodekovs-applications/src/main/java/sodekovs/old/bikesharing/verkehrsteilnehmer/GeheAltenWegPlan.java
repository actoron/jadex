package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.old.bikesharing.container.VerkehrsteilnehmerWeg;

/**
 * Ein Plan, bei dem der Verkehrsteilnehmer einen alten Weg
 * zu dem übergebenen Ziel zurücklegt; dabei wird stets der
 * kürzeste Weg gewählt
 * @author dagere
 *
 */
public class GeheAltenWegPlan extends Plan
{

	private Grid2D _space;
	private ISpaceObject _avatar;
	private IVector2 _position;
	private IVector2 _ziel;
	private double _fahrradPraeferenz;
	
	public GeheAltenWegPlan()
	{
		_fahrradPraeferenz = (Double) getBeliefbase().getBelief(SucheNeuenWegPlan.FAHRRADPRAEFERENZ).getFact();
	}
	
	@Override
	public void body()
	{
//		getScope().getParent().
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getScope().getParent();
//		_space = (Grid2D) app.getSpace(StartSimulationProzess.SPACE);
//		_avatar = _space.getAvatar(getComponentIdentifier());
//		_avatar = SelbstBewegPlan.gibAvatar(getScope().getParent(), this);
		_avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
		_position = (IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION);

		_ziel = (IVector2) getParameter(ZielWaehlPlan.ZIEL).getValue();

		VerkehrsteilnehmerWeg wege[] = (VerkehrsteilnehmerWeg[]) getBeliefbase().getBeliefSet("wege").getFacts();

		if (wege == null || wege.length == 0)
		{
			fail();
		}
		else
		{
			VerkehrsteilnehmerWeg besterWeg = sucheBestenWeg( wege );
			if ( besterWeg == null )
			{
				fail();
			}
			geheWeg(besterWeg);

			if (!((IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION)).equals(_ziel))
			{
				IGoal neuesZiel = createGoal(ZielWaehlPlan.ERREICHEZIEL);
				neuesZiel.getParameter(ZielWaehlPlan.ZIEL).setValue(_ziel);
				dispatchSubgoalAndWait(neuesZiel);
			}
		}
		
	}
	/**
	 * Sucht den Besten Weg aus der gegebenen Wegmenge
	 * @param wege
	 * @return
	 */
	private VerkehrsteilnehmerWeg sucheBestenWeg( VerkehrsteilnehmerWeg[] wege )
	{
		IVector2 positionInt = new Vector2Double(_position.getXAsDouble(), _position.getYAsDouble() );
		
		VerkehrsteilnehmerWeg besterWeg = null;
		for (VerkehrsteilnehmerWeg w : wege)
		{
			if (w.gibStart().equals(positionInt) && w.gibEnde().equals(_ziel))
			{
				if (besterWeg == null || istWegBesser(w, besterWeg ) ) //besterWeg.gibWeglaenge() > w.gibWeglaenge() )
				{
					besterWeg = w;
				}
				// TODO: Vergleichskriterium um Unbequemlichkeit und
				// Abwägung
				// Unbequemlichkeit / Weglänge / Fahrradaffinität erweitern
			}
			else if ( w.gibEnde().equals(positionInt) && w.gibStart().equals(_ziel) )
			{
				VerkehrsteilnehmerWeg inverser = w.gibInversion();
				boolean existiert = getBeliefbase().getBeliefSet("wege").containsFact( inverser );
				if ( ! existiert )
				{
					if ( besterWeg == null || besterWeg.gibWeglaenge() > inverser.gibWeglaenge() )
					{
						besterWeg = inverser;
					}
					getBeliefbase().getBeliefSet("wege").addFact( inverser );
					getBeliefbase().getBelief("weganzahl").setFact( getBeliefbase().getBeliefSet("wege").size() );
				}
			}
		}
		return besterWeg;
	}
	
	/**
	 * Prüft, ob Weg 1 besser ist als Weg 2, nach den Vergleichskriterien des Agenten
	 * @param w1 Weg 1
	 * @param w2 Weg 2
	 * @return	true, wenn Weg 1 besser ist, sonst false
	 */
	private boolean istWegBesser( VerkehrsteilnehmerWeg w1, VerkehrsteilnehmerWeg w2 )
	{
		if ( w1.gibWeglaenge() < w2.gibWeglaenge() || ( w1.gibWeglaenge() < w2.gibWeglaenge() * (1 + _fahrradPraeferenz ) && w1.gibRadanteil() > w2.gibRadanteil() ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Geht den angegebenen Weg, indem die Etappenziele einzeln besucht werden
	 * 
	 * @param w
	 *            Weg, der gegangen werden soll
	 */
	private void geheWeg(VerkehrsteilnehmerWeg w)
	{
		// if (w instanceof VerkehrsteilnehmerWeg)
		// {
		VerkehrsteilnehmerWeg vw = (VerkehrsteilnehmerWeg) w;
		// System.out.println("Starte weg: " + vw.toString() );

		long startZeit = getTime();
		for (int i = 1; i < vw.gibStationen().size(); i++)
		{
			String typ = vw.gibNaechstenWegtyp(i);
			IVector2 etappenziel = vw.gibNaechsteStation(i);
			IGoal goal = createGoal(typ);
			goal.getParameter(ZielWaehlPlan.ZIEL).setValue(etappenziel);
			try
			{
//				System.out.println("Zeit vor " + typ + "| " + etappenziel + ": " + Zeitverwaltung.gibInstanz().gibTageszeit());
				dispatchSubgoalAndWait(goal);
//				System.out.println("Zeit nach " + typ + "| " + etappenziel + ": " + Zeitverwaltung.gibInstanz().gibTageszeit());
			} catch (GoalFailureException g)
			{
				System.out.println("Weg fehlgeschlagen");
				vw.addWeglaenge(vw.gibWeglaenge() * 2); // Schlechte Bewertung
														// für Weg einfügen
				return;
			}
		}

		long zeit = getTime() - startZeit;
		vw.addWeglaenge(zeit);
	}
	
	public void failed()
	{
//		System.out.println("Fail!");
	}

}
