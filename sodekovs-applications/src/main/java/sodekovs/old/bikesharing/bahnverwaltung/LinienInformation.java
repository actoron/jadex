package sodekovs.old.bikesharing.bahnverwaltung;

import java.util.List;

/**
 * Eine Klasse, die Informationen über eine Verkehrsmittel-Linie (beispielsweise U1) enthält
 * @author dagere
 *
 */
public class LinienInformation
{
	private final String _linie;
	private final int _fahrzeuge;
	private final long _start, _ende, _takt;
	private final List<Integer> _zeiten;
	
	public LinienInformation( String linie, long start, long ende, long takt, int fahrzeuge, List<Integer> zeiten )
	{
		_linie = linie;
		_start = start;
		_ende = ende;
		_takt = takt;
		_fahrzeuge = fahrzeuge;
		_zeiten = zeiten;
	}
	
	public String gibLinie()
	{
		return _linie;
	}
	
	public long gibStart()
	{
		return _start;
	}

	public long gibEnde()
	{
		return _ende;
	}

	public long gibTakt()
	{
		return _takt;
	}
	
	public int gibFahrzeuge()
	{
		return _fahrzeuge;
	}
	
	public Integer gibZeit( int station )
	{
		return _zeiten.get( station );
	}
}
