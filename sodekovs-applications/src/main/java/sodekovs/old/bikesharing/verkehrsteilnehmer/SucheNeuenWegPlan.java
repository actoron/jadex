package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sodekovs.bikesharing.simulation.BahnStationen;
import sodekovs.old.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.old.bikesharing.container.VerkehrsteilnehmerWeg;
import sodekovs.old.bikesharing.container.Weg;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;

/**
 * Ein Plan, mittels dem der Verkehrsteilnehmer einen neuen Weg suchen soll
 * @author dagere
 *
 */
public class SucheNeuenWegPlan extends Plan
{
	public static final String SUCHENEUENWEG = "sucheneuenweg";
	public static final String MAXIMALERFUSSWEG = "maximalerFussweg";
	public static final String FAHRRADPRAEFERENZ = "fahrradpraeferenz";
	public static final String LAUFPRAEFERENZ = "laufpraeferenz";
	public static final String NAEHEPRAEFERENZ = "naehepraeferenz";
	
	private final double _laufPraeferenz;
	private final double _maximalerFussweg;
	private final double _fahrradPraeferenz;
	private final double _naehePraeferenz;
	private int maxStationen = 4;
	
	private ISpaceObject _avatar;
	private IVector2 _position;
	private IVector2 _ziel;
	private List<String> _genutzteLinien = new ArrayList<String>();
	private List<BahnStation> _besuchteStationen = new ArrayList<BahnStation>();
	private long _startzeit;
	
	public SucheNeuenWegPlan()
	{
		_maximalerFussweg = (Double) getBeliefbase().getBelief(MAXIMALERFUSSWEG).getFact();
		_fahrradPraeferenz = (Double) getBeliefbase().getBelief(FAHRRADPRAEFERENZ).getFact();
		_laufPraeferenz = (Double) getBeliefbase().getBelief(LAUFPRAEFERENZ).getFact();
		_naehePraeferenz = (Double) getBeliefbase().getBelief(NAEHEPRAEFERENZ).getFact();
//		System.out.println("Radpräferenz: " + _fahrradPraeferenz);
	}
	
	@Override
	public void body()
	{
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getScope().getParent();
//		Grid2D space = (Grid2D) app.getSpace(StartSimulationProzess.SPACE);
//		_avatar = space.getAvatar(getComponentIdentifier());
//		_avatar = SelbstBewegPlan.gibAvatar(getScope().getParent(), this);
		_avatar = _avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
		_position = (IVector2) _avatar.getProperty( Space2D.PROPERTY_POSITION );
		_ziel = (IVector2) getParameter( ZielWaehlPlan.ZIEL).getValue();
		
		// TODO Auto-generated method stub
//		System.out.println("Suche neuen Weg! " + Zeitverwaltung.gibInstanz().gibTageszeit() + " Ziel: " + _ziel);
		
		Weg w = sucheNeuenWeg();
//		System.out.println("Weg!" + Zeitverwaltung.gibInstanz().gibTageszeit());
//		System.out.println("Gefundener Weg: " + w);
		if ( !getBeliefbase().getBeliefSet("wege").containsFact( w ))
		{
			getBeliefbase().getBeliefSet("wege").addFact(w); //Test sollte eigentlich überflüssig sein..
			getBeliefbase().getBelief("weganzahl").setFact( getBeliefbase().getBeliefSet("wege").size() );
		}
	}
	
	/**
	 * Sucht einen neuen Weg, führt ihn aus und gibt den entstandenen Weg
	 * zurück. Der Weg führt dabei von der aktuellen Position des Agenten über
	 * zwischenStationen z.B. an Bahnlinien zum Ziel des Agenten
	 * 
	 * @return Entstandener Weg
	 */
	private Weg sucheNeuenWeg()
	{
		IVector2 startStation = BahnStationen.gibInstanz().gibNaechsteStation(_position).gibPosition();
		IVector2 zielStation = BahnStationen.gibInstanz().gibNaechsteStation(_ziel).gibPosition();
				
		double distanz = _position.getDistance(_ziel).getAsDouble();
		double zufall = Math.random();
		double bahnWeg = startStation.getDistance( zielStation ).getAsDouble();
		
		VerkehrsteilnehmerWeg w = new VerkehrsteilnehmerWeg();
		w.addStation(new Vector2Int(_position.getXAsInteger(), _position.getYAsInteger()));
		_startzeit = getTime();
		if (distanz < zufall * _maximalerFussweg * _laufPraeferenz || bahnWeg == 0 )
		{
//			long startzeit = getTime();
			IGoal hinLaufen = createGoal(GehZuFuss.FUSSWEG);
			hinLaufen.getParameter(ZielWaehlPlan.ZIEL).setValue(_ziel);
			dispatchSubgoalAndWait(hinLaufen);

			w.addStation(_ziel, GehZuFuss.FUSSWEG);
			
			long realzeit = getTime() - _startzeit;
			w.addWeglaenge(realzeit);
			return w;
		}
		else
		{			
			
			
			int zaehler = 5;
			BahnStation zielNaechste = BahnStationen.gibInstanz().gibNaechsteStation( _ziel );

			FahrradVerleihStation zielNaechsteFVS = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( _ziel );
			
			double heutigeFahrradlust = Math.random();
//			if ( heutigeFahrradlust < 0.5 )
//			{
//				heutigeFahrradlust += 0.5;
//			}
			
			boolean ende = false;
			while ( !ende )
			{
				BahnStation bs = waehleNaechsteStation( _position ); 
				
				FahrradVerleihStation fvs = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( _position );
				
				if ( _fahrradPraeferenz * heutigeFahrradlust > 0.5 )
				{
//					System.out.println("Naechste Fahrradstation " + fvs.gibPosition().getDistance( _position).getAsDouble() );
//					System.out.println("Naechste Bahnstation: " + bs.gibPosition().getDistance(_position).getAsDouble());
				}
				
				if ( zielNaechsteFVS.gibPosition().getDistance( _ziel ).getAsDouble() / (1 + _fahrradPraeferenz * heutigeFahrradlust) < zielNaechste.gibPosition().getDistance( _ziel).getAsDouble() 
					&& fvs.gibPosition().getDistance( _position).getAsDouble() < bs.gibPosition().getDistance(_position).getAsDouble() * 2 &&
					_fahrradPraeferenz * heutigeFahrradlust > 0.5)
				{		
					fussWegZu( fvs.gibPosition(), w );
					
					try
					{
						radWegZu( zielNaechsteFVS.gibPosition(), w);
						fussWegZu( _ziel, w );
						ende = true;
					}
					catch (GoalFailureException e)
					{
						System.out.println("War kein Rad mehr da.." + fvs.gibName());
						heutigeFahrradlust = 0;
//						w.loescheLetzenFussweg();
					}
				}
				else
				{
					//TODO: prüfen, dass sie noch nicht besucht ist, esseidenn, es ist die, auf der man gerade steht
//					System.out.println("Zwischenfussweg von " + _position + " zu " + bs);
					if ( !_besuchteStationen.contains( bs ))
					{
						besucheStation(bs, w, zielNaechste);
						ende = pruefeAbbruch( zielNaechste, zufall );
					}
					
					zaehler--;
					if ( zaehler < 1 )
					{
						ende = true;
					}
					
				}
			}
			
			if ( _ziel.getDistance( _position).getAsDouble() != 0 )
			{
				_genutzteLinien.clear();
				_besuchteStationen.clear();
				//Schleifeninvariante: am Ende jeder Ausführung befindet man sich auf einer Station -> man kann einfach loslaufen..
				BahnStation letzteZielstation = waehleZielnaechsteStation( BahnStationen.gibInstanz().gibNaechsteStation( _position ) );
				//Weitere Schleifeinvariante: er war noch nie an einer Station, die nah genug ist zum laufen -> er findet hier eine optimale Station, esseidenn die nächste
				//						      Station am Ziel ist nicht nah genug zum Laufen
				fahrBahnZu( letzteZielstation.gibPosition(), w );
				fussWegZu(_ziel, w);
			}
			
//			System.out.println("Weg: " + w);
			
			long realzeit = getTime() - _startzeit;
			w.addWeglaenge(realzeit);
			w.bereinigen();
			return w;
		}
	}
	
	private void besucheStation( BahnStation bs, VerkehrsteilnehmerWeg w, BahnStation zielNaechste )
	{
		BahnStation aktuelleStation = BahnStationen.gibInstanz().gibStation(_position);
		if ( aktuelleStation == null || !bs.gibLinien().equals( aktuelleStation.gibLinien() ))
		{
			fussWegZu( bs.gibPosition(), w );
		}
		
		_besuchteStationen.add( bs );
		
		BahnStation naechstesZiel = (Math.random() < _naehePraeferenz ) ? waehleZielnaechsteStation( bs ) : waehleVerbindungsstaerksteStation(bs);
		
		if ( pruefEnthalten( zielNaechste.gibLinien(), bs.gibLinien() ) )
		{
			naechstesZiel = waehleZielnaechsteStation( bs );
		}
		
		if ( ! naechstesZiel.equals( bs ) )
		{
			fahrBahnZu( naechstesZiel.gibPosition(), w );
			_besuchteStationen.add( naechstesZiel );		
		}
	}
	
	private boolean pruefEnthalten( List<String> liste1, List<String> liste2)
	{
		for ( String linie : liste1 )
		{
			if ( liste2.contains( linie ) )
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Prüft, ob die Wegsuche beendet werden soll, weil
	 *  1. die zielnächste Station mit einer Linie von hier aus erreicht werden kann
	 *  2. die aktuelle Station in Fußwegnähe des Zieles ist
	 *  3. die aktuelle Position bereits die Zielnächste Station ist
	 * @param zielNaechste
	 * @param zufall
	 * @return
	 */
	private boolean pruefeAbbruch( BahnStation zielNaechste, double zufall )
	{
		BahnStation aktuelleStation = BahnStationen.gibInstanz().gibStation(_position);
		
		if ( pruefEnthalten(zielNaechste.gibLinien(), aktuelleStation.gibLinien() ) )
		{
//			System.out.println("Abbruch: Zielnächste Linie");
			return true;
		}
		
		if ( _position.getDistance( _ziel ).getAsDouble() < zufall * _maximalerFussweg * _laufPraeferenz )
		{
			System.out.println("Abbruch: Fußweg");
			return true;
		}
		
		if ( _position.equals( zielNaechste.gibPosition() ) )
		{
			System.out.println("Abbruch: Zielnächste");
			System.out.println("Mehr geht nich");
			return true;
		}
		return false;
	}
	
	/**
	 * Wählt eine Bahnstation, die in von der übergenenen Station erreichbar ist, die am nächsten am Ziel ist und die noch nicht besucht wurde
	 * @param bs	BahnStation, in deren Linien gesucht werden soll
	 * @return		Zielnächste Station
	 */
	private BahnStation waehleZielnaechsteStation( BahnStation bs )
	{
		List<String> linien = bs.gibLinien();
		BahnStation naechstesZiel = bs;
		String linie = "";
		for ( String l : linien )
		{
			if ( ! _genutzteLinien.contains( l ) )
			{
//				System.out.println("Prüfe linie " + l + " wirklich");
				BahnStation naechste = bs;
				for ( BahnStation linienStation : BahnStationen.gibInstanz().gibStationen( l ) )
				{
//					System.out.println("Prüfe station: " + linienStation + " Besuchte Stationen: " + _besuchteStationen.isEmpty());
					if ( linienStation.gibPosition().getDistance( _ziel ).getAsDouble() < naechste.gibPosition().getDistance( _ziel ).getAsDouble() &&
							!_besuchteStationen.contains( linienStation ) )
					{
//						System.out.println("Zuweisung: " + linienStation);
						naechste = linienStation;
						linie = l;
					}
				}
				naechstesZiel = naechste;
			}
		}
		_genutzteLinien.add( linie );
		return naechstesZiel;
	}
	
	/**
	 * Wählt eine Bahnstation, die von der übergebenen Station erreichbar ist, die noch nicht besucht wurde und die die größte Anzahl an Linien besitzt
	 * @param bs	BahnStation, in deren Linien gesucht werden soll
	 * @return		Zielnächste Station
	 */
	private BahnStation waehleVerbindungsstaerksteStation( BahnStation bs )
	{
		List<String> linien = bs.gibLinien();
		BahnStation naechstesZiel = bs;
		String linie = "";
		for ( String l : linien )
		{
			if ( ! _genutzteLinien.contains( l ) )
			{
//				System.out.println("Prüfe linie " + l + " wirklich");
				BahnStation naechste = bs;
				for ( BahnStation linienStation : BahnStationen.gibInstanz().gibStationen( l ) )
				{
//					System.out.println("Prüfe station: " + linienStation + " Besuchte Stationen: " + _besuchteStationen.isEmpty());
					if ( linienStation.gibLinien().size() > naechste.gibLinien().size() &&
							!_besuchteStationen.contains( linienStation ) )
					{
//						System.out.println("Zuweisung: " + linienStation);
						naechste = linienStation;
						linie = l;
					}
				}
				naechstesZiel = naechste;
			}
		}
		_genutzteLinien.add( linie );
		return naechstesZiel;
	}
	
	/**
	 * Legt den Weg zu dem gewählten Ziel zu Fuß zurück, und fügt die Etappe an den VerkehrsteilnehmerWeg an
	 * @param etappenziel	Ziel, dass zu Fuß erreicht werden soll
	 * @param w				VerkehrsteilnehmerWeg, der um eine Etappe erweitert werden soll
	 */
	private void fussWegZu( IVector2 etappenziel, VerkehrsteilnehmerWeg w )
	{
		if ( etappenziel.equals( _position) )
		{
			return;
		}
		IGoal goal = createGoal( GehZuFuss.FUSSWEG );
		goal.getParameter(ZielWaehlPlan.ZIEL).setValue(etappenziel);
		dispatchSubgoalAndWait(goal); 
		_position = (IVector2) _avatar.getProperty( Space2D.PROPERTY_POSITION );
		w.addStation( new Vector2Double( _position.getXAsDouble(), _position.getYAsDouble() ), GehZuFuss.FUSSWEG);
	}
	
	/**
	 * Legt den Weg zu dem gewählten Ziel mit einem Leihrad zurück, und fügt die Etappe an den VerkehrsteilnehmerWeg an
	 * @param etappenziel	Ziel, dass mit dem Rad erreicht werden soll
	 * @param w				VerkehrsteilnehmerWeg, der um eine Etappe erweitert werden soll
	 */
	private void radWegZu( IVector2 etappenziel, VerkehrsteilnehmerWeg w ) throws GoalFailureException
	{
		
//		System.out.println("Radweg!");
		if ( etappenziel.equals( _position) )
		{
			return;
		}
		long zeitStart = getTime();
//		System.out.println("Radweg2!");
		IGoal goal = createGoal( FahreRad.RADWEG );
		goal.getParameter(ZielWaehlPlan.ZIEL).setValue(etappenziel);
		dispatchSubgoalAndWait(goal); 
		_position = (IVector2) _avatar.getProperty( Space2D.PROPERTY_POSITION );
		w.addStation( new Vector2Double( _position.getXAsDouble(), _position.getYAsDouble() ), FahreRad.RADWEG);
		long zeitEnde = getTime();
//		System.out.println("Start: " + zeitStart + " Ende: " + zeitEnde);
//		System.out.println("Gesamtstart: " + _startzeit + " " + ( zeitEnde - zeitStart ) / (zeitEnde - _startzeit ));
		double temp = (zeitEnde - zeitStart );
		double temp2 = (zeitEnde - _startzeit );
//		System.out.println(( zeitEnde - zeitStart ) + " " + (zeitEnde - _startzeit ) + " " + temp / temp2);
		w.setzRadanteil( (zeitEnde - zeitStart ) / (zeitEnde - _startzeit ) );
	}
	
	/**
	 * Legt den Weg zu dem gewählten Ziel mittels eines Verkehrsmittels zurück, und fügt die Etappe an den VerkehrsteilnehmerWeg an
	 * @param etappenziel	Ziel, dass mittels Verkehrsmittel erreicht werden soll
	 * @param w				VerkehrsteilnehmerWeg, der um eine Etappe erweitert werden soll
	 */
	private void fahrBahnZu( IVector2 etappenziel, VerkehrsteilnehmerWeg w )
	{
		if ( etappenziel.equals( _position) )
		{
			return;
		}
//		System.out.println("Etappenziel bahn: " + etappenziel);
		IGoal goal = createGoal( NutzeBahn.BAHNFAHRT );
		goal.getParameter(ZielWaehlPlan.ZIEL).setValue(etappenziel);
		dispatchSubgoalAndWait(goal); 
		_position = (IVector2) _avatar.getProperty( Space2D.PROPERTY_POSITION );
		w.addStation( new Vector2Double( _position.getXAsDouble(), _position.getYAsDouble() ), NutzeBahn.BAHNFAHRT);
	}
	
	/**
	 * Wählt eine Station zufällig aus, die in der Nähe der gegebenen Position
	 * ist, und bevorzugt (TODO!) dabei nähere Stationen
	 * 
	 * @param position
	 *            Position, in deren Nähe die nächste Station gewählt werden
	 *            soll
	 * @return BahnStation, die gewählt wurde
	 */
	private BahnStation waehleNaechsteStation(IVector2 position)
	{
		Map<Double, BahnStation> stationsliste = BahnStationen.gibInstanz().gibNaechsteStationen(position);
		
		for ( BahnStation bs : _besuchteStationen )
		{
			stationsliste.remove( bs.gibPosition().getDistance( position ).getAsDouble() ); //TODO: wirklich raussammeln...
		}
		
		ArrayList<Double> distanzliste = new ArrayList<Double>( stationsliste.keySet() );
		
		Collections.sort( distanzliste );

		int zufallsInt = (int) (Math.pow( Math.random(), 4) * ((distanzliste.size() < maxStationen ) ? distanzliste.size() : maxStationen ));
		//Annahme: man nimmt eine Station in der Nähe, aber höchstens eine der 5 nächsten
		
		Double gewaehlteDistanz = distanzliste.get(zufallsInt);
		
		BahnStation gewaehlte = stationsliste.get(gewaehlteDistanz);
		return gewaehlte;
	}

}
