package sodekovs.bikesharing.disposition;

import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;

public class DispositionSchwellwertAgent extends DispositionsAgent
{

	@Override
	public void verschieben()
	{
		System.out.println("Beginne verschieben");
		_vonStation = FahrradVerleihStationen.gibInstanz().gibStationMitMeistenRaedern();
		_zuStation = FahrradVerleihStationen.gibInstanz().gibStationMitWenigstenRaedern();
		 System.out.println("Verschieben!\n" + _vonStation + " \n zu \n " +
		 _zuStation);
		if (_zuStation.gibFahrradAnzahl() * 2.5 > _vonStation.gibFahrradAnzahl())
		{
			 System.out.println("Verschieben verschoben, zu wenig Fahrraddifferenz");
			_vonStation = null;
			_zuStation = null;
			warten();
		}
		else
		{
			Vector2Double ziel = (Vector2Double) _vonStation.gibPosition().copy();
//			System.out.println("Bewege zu Ziel: "+ ziel);
			zuZielBewegen( ziel);
		}
	}
	
	@Override
	protected void dispositionsProzess()
	{
		if (_ziel.equals(_vonStation.gibPosition()))
		{
//			System.out.println("An Start f. Disposition angekommen");
			int leihAnzahl = _vonStation.gibFahrradAnzahl() / 2;
			wegNehmen(_vonStation, leihAnzahl);

//			_ziel = (Vector2Double) _zuStation.gibPosition().copy();

			zuZielBewegen( (Vector2Double) _zuStation.gibPosition().copy() );
		}
		else if (_ziel.equals(_zuStation.gibPosition()))
		{
//			System.out.println("An Ziel f. Disposition angekommen");
			zurueckGeben( _zuStation );

			verschieben();
		}
		else
		{
			System.out.println("DispositionEngpassAgent.dispositionsProzess(): Unerwarteter Fehler, Disposition an nicht-Ziel");		}
	}

}
