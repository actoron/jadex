package sodekovs.bikesharing.disposition;

import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.LinkedList;
import java.util.List;

import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;


public class DispositionVergangenheitAgent extends DispositionsAgent
{
	private List<Engpass> _engpassListeVortag, _engpassListeAktuell;
	
	public DispositionVergangenheitAgent()
	{
		_engpassListeAktuell = new LinkedList<Engpass>();
		_engpassListeVortag = new LinkedList<Engpass>();
	}

	@Override
	public void verschieben()
	{
		merkeEngpässe();
		boolean zuTun = false;
		FahrradVerleihStation zielStation = null;
		schleife: for ( Engpass e : _engpassListeVortag )
		{
			if ( Math.abs( e.gibZeit() - Zeitverwaltung.gibInstanz().gibTageszeit() ) < 15 )
			{
				_vonStation = FahrradVerleihStationen.gibInstanz().gibStationMitMeistenRaedern();
				_zuStation = e.gibFVS();
				System.out.println(Zeitverwaltung.gibInstanz().gibZeitString() + "Disponiere von: " + _vonStation.gibName() + " zu " + _zuStation.gibName());
				if ( _vonStation != _zuStation )
				{
					zielStation = _vonStation;
					zuTun = true;
					_engpassListeVortag.remove( e );
					break schleife;
				}
				else
				{
					_engpassListeVortag.remove( e );
					break schleife;
				}
			}
		}
		if ( !zuTun )
		{
			warten( 50 );
		}
		else
		{
//			_engpassListeVortag.remove( _vonStation );
			System.out.println("Eigene Position: " + _avatar
					.getProperty(Space2D.PROPERTY_POSITION) + " Ziel: " + _vonStation.gibPosition());
			zuZielBewegen( (Vector2Double) _vonStation.gibPosition().copy() );
		}
	}
	
	private void merkeEngpässe()
	{
		for ( FahrradVerleihStation fvs : FahrradVerleihStationen.gibInstanz().gibStationen() )
		{
			if ( fvs.gibFahrradAnzahl() == 0)
			{
				_engpassListeAktuell.add( new Engpass( fvs, Zeitverwaltung.gibInstanz().gibTageszeit() ) );
			}
		}
	}

	@Override
	public void dispositionsProzess()
	{
		if (_ziel.equals(_vonStation.gibPosition()) && !(_ziel.equals(_zuStation.gibPosition())) )
		{
			int leihAnzahl = _vonStation.gibFahrradAnzahl() / 3;
			wegNehmen(_vonStation, leihAnzahl);

			zuZielBewegen((Vector2Double) _zuStation.gibPosition().copy());
		}
		else if (_ziel.equals(_zuStation.gibPosition()))
		{
			 System.out.println(Zeitverwaltung.gibInstanz().gibZeitString() + "An Ziel f. Disposition angekommen");
			zurueckGeben(_zuStation);
			
			waitForTick( verschiebSchritt );
//			verschieben();
		}
		else
		{
			System.out
					.println("DispositionEngpassAgent.dispositionsProzess(): Unerwarteter Fehler, Disposition an nicht-Ziel");
		}
	}
	
	@Override
	protected void warten( int zeit)
	{
		long wartezeit = zeit;
		if (Zeitverwaltung.gibInstanz().gibTageszeit() < 8 * 60)
		{
			wartezeit = 8 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
			_engpassListeVortag = _engpassListeAktuell;
			_engpassListeAktuell = new LinkedList<Engpass>();
			
		}
		if (Zeitverwaltung.gibInstanz().gibTageszeit() > 19 * 60)
		{
			wartezeit = 24 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
		}
		System.out.println("Wartezeit Dispo: " + wartezeit);
		if ( _engpassListeAktuell == _engpassListeVortag )
		{
			System.out.println("Engpasslisten gleich!!!");
		}

		waitFor(wartezeit * 100, verschiebSchritt);
	}

}
