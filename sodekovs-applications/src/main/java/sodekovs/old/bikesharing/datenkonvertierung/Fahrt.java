package sodekovs.old.bikesharing.datenkonvertierung;

import jadex.extension.envsupport.math.IVector2;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;

/**
 * Klasse, um Fahrten zu speichern und zu vergleichen
 * @author dagere
 *
 */
public class Fahrt
{
	int zeit;
	IVector2 start, ziel;

	public Fahrt(int zeitU, IVector2 startU, IVector2 zielU)
	{
		zeit = zeitU;
		start = startU;
		ziel = zielU;
	}
	
	/**
	 * Equals überschreiben, so dass Fahrten gleich sind, bei denen Start und Ziel gleich ist
	 */
	public boolean equals( Object o )
	{
		if ( o instanceof Fahrt )
		{
			Fahrt f = (Fahrt) o;
			if ( f.start.equals( start ) && f.ziel.equals( ziel ) )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		String ret = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( start ).gibName() + " zu: " + FahrradVerleihStationen.gibInstanz().gibNaechsteStation( ziel ).gibName();
		return ret;
	}
}
