package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.PlanFailureException;
import jadex.extension.envsupport.math.IVector2;
import sodekovs.old.bikesharing.datenkonvertierung.Datensammler;
import sodekovs.old.bikesharing.fahrrad.Fahrrad;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

/**
 * Eine Klasse für den Plan, mit dem der Verkehrsteilnehmer Fahrrad fährt
 * @author dagere
 *
 */
public class FahreRad extends SelbstBewegPlan
{
	private static String ausgabedatei = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/fahrten.csv";
	private Fahrrad f;
	private FahrradVerleihStation _startFVS, _zielFVS;
	IVector2 _ziel;
	public static final String RADWEG = "radweg";

	public FahreRad()
	{
		super();
		geschwindigkeit = 0.4;
	}
	
	/**
	 * Setzt die Ausgabedatei auf die übergebene Datei
	 * @param datei
	 */
	public static void setzAusgabe( String datei )
	{
		ausgabedatei = datei;
	}
	
	@Override
	public void body()
	{
		_ziel = (IVector2) getParameter(ZielWaehlPlan.ZIEL).getValue();
		_zielFVS = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( _ziel );
		
		if ( radAusleihen() )
		{	
//			Datensammler.gibDatensammler( ausgabedatei ).speichereWert(1, Zeitverwaltung.gibInstanz().gibZeitString() + " " +
//					_avatar.getId() + ", " + _startFVS.gibName() + " - " + _zielFVS.gibName());
			speichereFahrt( ausgabedatei, _avatar.getId(), Zeitverwaltung.gibInstanz().gibZeitString(), _startFVS.gibName(), _zielFVS.gibName());
			bewegen(_ziel);
			if ( ! (_ziel.equals( _position ) ) )
			{
				System.out.println("Schwerer Fehler, Ziel!=Position in FahreRad: " + _ziel + " Position: " + _position);
			}
			radZurueckgeben();
		}
		else
		{
			System.out.println("Kein Rad da!");
			Datensammler.gibDatensammler( ausgabedatei ).speichereWert(1, Zeitverwaltung.gibInstanz().gibZeitString() + ", " +
					_avatar.getId() + ", Fehlschlag " + _startFVS.gibName());
		}
	}
	
	public static void speichereFahrt( String datei, Object id, String zeitstring, String start, String ende )
	{
		Datensammler.gibDatensammler( datei ).speichereWert(1, zeitstring + ", " +
				id + ", " + start + ", " + ende);
	}
	
	/**
	 * Leiht ein Rad aus
	 * @return	Ob das Rad aufgrund der vorhandenen Fahrradanzahl ausgeliehen werden kann
	 */
	private boolean radAusleihen() throws PlanFailureException
	{
		_startFVS = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( _position );
		if ( !_startFVS.gibPosition().equals( _position ) )
		{ 
			return false;
		}
		f = _startFVS.leihFahrrad();
		if ( f != null )
		{
			return true;
		}
		else
		{
			System.out.println("Rad ausleihen fehlgeschlagen");
			throw new PlanFailureException();
		}
	}
	
	/**
	 * Gibt ein Rad zurück
	 * @return	Ob das Rad aufgrund der Kapazitätsrestriktion zurückgegeben werden kann
	 */
	private boolean radZurueckgeben()
	{
		
		if ( !_zielFVS.gibPosition().equals( _position ) )
		{ 
			return false;
		}
		if ( _zielFVS.gibRadZurueck( f ) )
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	

}
