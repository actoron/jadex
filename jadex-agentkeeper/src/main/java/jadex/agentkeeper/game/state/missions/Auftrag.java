package jadex.agentkeeper.game.state.missions;

import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;

/**
 * Eine Klasse fuer die Datenhaltung der Auftraege
 * 
 * @author 8reichel
 * 
 */
public class Auftrag {

	private static HashMap<String, Integer> Bearbeiteranzahl;

	static {
		Bearbeiteranzahl = new HashMap<String, Integer>();
		Bearbeiteranzahl.put(Auftragsverwalter.BESETZEN, new Integer(1));
		Bearbeiteranzahl.put(Auftragsverwalter.GOLDSAMMELN, new Integer(1));
		Bearbeiteranzahl.put(Auftragsverwalter.VERSTAERKEWAND, new Integer(1));
		Bearbeiteranzahl.put(Auftragsverwalter.WANDABBAU, new Integer(1));
		Bearbeiteranzahl.put(Auftragsverwalter.KAPUTTMACHEN, new Integer(1));
		Bearbeiteranzahl.put( Auftragsverwalter.ANGREIFEN, new Integer(1));
	}

	public String _typ;
	public String _zieltyp;
	private int _imps;
	private Vector2Int _zielPosition; //Int, weil es nur ganzzahlige Positionen sind!
	private long _id;

	/**
	 * Erstellt einen Auftrag, der sich auf ein unbewegliches Object bezieht
	 * @param typ  Typ des Auftrages
	 * @param ziel Zielposition des Auftrages
	 */
	public Auftrag(String typ, Vector2Int ziel) {
		_typ = typ;
		_zielPosition = ziel;
		_imps = 0;
	}

	/**
	 * Erstellt einen Auftrag, der sich auf ein bewegliches Object bezieht
	 * @param typ  		Typ des Auftrages
	 * @param id		ID des Zielobjektes
	 * @param position Aktuelle Position des Zielobjektes
	 */
	public Auftrag(String typ, Long id) {
		_typ = typ;
		_id = id;
	}

	public void setzZieltyp(String zieltyp) {
		_zieltyp = zieltyp;
	}

	public String gibZieltyp() {
		return _zieltyp;
	}

	public String gibTyp() {
		return _typ;
	}
	
	public long gibId()
	{
		return _id;
	}

	public boolean braucheBearbeiter() {

		if ((Bearbeiteranzahl.get(_typ)).intValue() > _imps) {
			return true;
		}
		else {
			return false;
		}
	}

	public void neuerBearbeiter() {
		++_imps;
	}

	/**
	 * Gibt die Zielposition des Auftrags zur�ck.
	 * 
	 * @return ziel
	 */
	public Vector2Int gibZiel() {
		return _zielPosition;
	}

	public String toString() {
		String ret = _typ + ", Hashcode: " + hashCode();
		ret = ret + " Id: " + _id;
		if ( _zielPosition != null )
		{
			ret = ret + ", Position: " + _zielPosition.getXAsInteger() + "," + _zielPosition.getYAsInteger();
		}
		return ret;
	}

}
