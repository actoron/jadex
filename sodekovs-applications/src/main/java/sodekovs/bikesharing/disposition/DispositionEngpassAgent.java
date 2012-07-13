package sodekovs.bikesharing.disposition;

import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;

public class DispositionEngpassAgent extends DispositionsAgent
{
	@Override
	public void verschieben()
	{
//		System.out.println("Beginne verschieben");
		_vonStation = FahrradVerleihStationen.gibInstanz()
				.gibStationMitMeistenRaedern();
		_zuStation = FahrradVerleihStationen.gibInstanz()
				.gibStationMitWenigstenRaedern();
//		System.out.println("Verschieben!\n" + _vonStation + " \n zu \n "
//				+ _zuStation);
		if (_vonStation.gibFahrradAnzahl() >= _vonStation.gibKapazitaet()
				|| _zuStation.gibFahrradAnzahl() == 0)
		{
			Vector2Double ziel = (Vector2Double) _vonStation.gibPosition()
					.copy();
			zuZielBewegen(ziel);
		}
		else
		{
//			System.out
//					.println("Verschieben verschoben, zu wenig Fahrraddifferenz");
			int wartezeit;
			if (_zuStation.gibFahrradAnzahl() < 5)
			{
				wartezeit = 10;
			}
			else
			{
				wartezeit = 50;
			}
			_vonStation = null;
			_zuStation = null;
			warten(wartezeit);
		}
	}

	@Override
	protected void dispositionsProzess()
	{
		if (_ziel.equals(_vonStation.gibPosition()))
		{
			int leihAnzahl = _vonStation.gibFahrradAnzahl() / 2;
			wegNehmen(_vonStation, leihAnzahl);

			zuZielBewegen((Vector2Double) _zuStation.gibPosition().copy());
		}
		else if (_ziel.equals(_zuStation.gibPosition()))
		{
			// System.out.println("An Ziel f. Disposition angekommen");
			zurueckGeben(_zuStation);

			verschieben();
		}
		else
		{
			System.out
					.println("DispositionEngpassAgent.dispositionsProzess(): Unerwarteter Fehler, Disposition an nicht-Ziel");
		}
	}
}
