package sodekovs.bikesharing.disposition;

import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;

public class Engpass
{
	private FahrradVerleihStation _fvs;
	private long _zeit;
	
	public Engpass( FahrradVerleihStation fvs, long zeit)
	{
		_fvs = fvs;
		_zeit = zeit;
	}
	
	public FahrradVerleihStation gibFVS()
	{
		return _fvs;
	}
	
	public long gibZeit()
	{
		return _zeit;
	}
}
