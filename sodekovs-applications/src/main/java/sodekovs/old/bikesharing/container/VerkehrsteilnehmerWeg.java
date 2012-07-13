package sodekovs.old.bikesharing.container;

import jadex.extension.envsupport.math.IVector2;

import java.util.ArrayList;

import sodekovs.old.bikesharing.verkehrsteilnehmer.GehZuFuss;
import sodekovs.old.bikesharing.verkehrsteilnehmer.NutzeBahn;

/**
 * Eine Klasse, um Wege zu speichern, wie Verkehrsteilnehmer sie benötigen (d.h. mit Anweisungen, mit welchem Verkehrsmittel der Weg zu bewältigen ist) Dabei soll insbesondere darauf R�cksicht
 * genommen werden, dass Vergleichskriterien ben�tigt werden, um zu entscheiden, ob ein Weg gew�hlt wird oder nicht
 * 
 * @author dagere
 * 
 */
public class VerkehrsteilnehmerWeg extends Weg {
	public static final String LEER = "leer";

	private long _wegLaenge;
	private ArrayList<String> _wegInfos;
	private int _gewichtung;
	private double _radAnteil;

	public VerkehrsteilnehmerWeg() {
		_wegInfos = new ArrayList<String>();
		_wegInfos.ensureCapacity(_stationen.size());
		_wegInfos.add(LEER); // Weil es zum 0. keinen Weg gibt
		_wegLaenge = 0;
		_gewichtung = 0;
		_radAnteil = 0;
	}

	/**
	 * Setzt den Wegtyp zwischen 2 Stationen auf den gewünschten Wegtyp
	 * 
	 * @param station
	 *            Stationsnummer, die gesetzt werden soll
	 * @param typ
	 *            Wegtyp zwischen 2 Stationen
	 */
	public void setzWegTyp(int station, String typ) {
		_wegInfos.set(station, typ);
	}

	/**
	 * Fügt eine Station mit dem angegebenen Wegtyp hinzu
	 */
	public void addStation(IVector2 station, String typ) {
		super.addStation(station);
		_wegInfos.add(typ);
	}

	/**
	 * Gibt das nächste Ziel an
	 * 
	 * @param position
	 *            Position, an der das nächste Ziel angegeben werden soll
	 * @return
	 */
	public IVector2 gibNaechsteStation(int position) {
		return _stationen.get(position);
	}

	/**
	 * Gibt den Wegtyp zum nächsten Ziel an
	 * 
	 * @param position
	 *            Position, an dem der nächste Wegtyp angegeben werden soll
	 * @return
	 */
	public String gibNaechstenWegtyp(int position) {
		return _wegInfos.get(position);
	}

	/**
	 * Gibt die Unbequemlichkeit des Weges aus, dabei ist eine möglichst geringe Unbequemlichkeit gut
	 * 
	 * @return
	 */
	public int gibUnbequemlichkeit() {
		return _stationen.size();
	}

	/**
	 * Gibt den Radanteil der Strecke zurück
	 * 
	 * @return Radanteil der Strecke
	 */
	public double gibRadanteil() {
		return _radAnteil;
	}

	/**
	 * Setzt den Radanteil der Strecke auf den gegebenen Wert
	 * 
	 * @param radanteil
	 *            Wert für den Radanteil
	 */
	public void setzRadanteil(double radanteil) {
		_radAnteil = radanteil;
	}

	public String toString() {
		String ret = "";
		for (int i = 0; i < _stationen.size(); i++) {
			ret += "S: " + _stationen.get(i) + " ";
			if (_wegInfos.size() > i + 1 && _wegInfos.get(i + 1) != null) {
				ret += _wegInfos.get(i + 1) + " ";
			}
		}
		ret += " Länge: " + _wegLaenge + " Gewichtung: " + _gewichtung + " Radanteil: " + _radAnteil;
		return ret;
	}

	/**
	 * Fügt die gegebene Weglänge zum Wissen über Weglängen hinzu; dabei wird die Länge mit den bisher ermittelten Längen gewichtet, und daraus die neue durchschnittliche Weglänge berechnet
	 * 
	 * @param zeit
	 *            Weglänge, die dieses mal benötigt wurde
	 */
	public void addWeglaenge(long zeit) {
		_wegLaenge = (_wegLaenge * _gewichtung + zeit) / (_gewichtung + 1);
		_gewichtung++;
	}

	/**
	 * Gibt die durchschnittliche Weglänge zurück
	 * 
	 * @return
	 */
	public long gibWeglaenge() {
		return _wegLaenge;
	}

	/**
	 * Löscht den letzten Fußweg der Strecke (für den Fall, dass ein falscher Fußweg gegangen wurde)
	 */
	public void loescheLetzenFussweg() {
		if (_wegInfos.get(_wegInfos.size() - 1).equals(GehZuFuss.FUSSWEG)) {
			_wegInfos.remove(_wegInfos.size() - 1);
			super.loescheLetzteStation();
		}
	}

	/**
	 * Löscht doppelte, aufeinander folgende Abschnitte im Weg
	 */
	public void bereinigen() {
		for (int i = 0; i < _wegInfos.size() - 1; i++) {
			if (_wegInfos.get(i).equals(_wegInfos.get(i + 1)) && !_wegInfos.get(i).equals(NutzeBahn.BAHNFAHRT)) {
				_wegInfos.remove(i);
				super._stationen.remove(i);
				i--;
			}
		}

	}

	/**
	 * Gibt die Umkehrung des Weges, also den Weg vom Ziel zum Start mit der umgekehrten Reihenfolge der Stationen
	 * 
	 * @return Inversion des Weges
	 */
	public VerkehrsteilnehmerWeg gibInversion() {
		VerkehrsteilnehmerWeg inverser = new VerkehrsteilnehmerWeg();
		for (int i = _wegInfos.size() - 1; i >= 0; i--) {
			inverser.addStation(_stationen.get(i), _wegInfos.get(i));
		}
		inverser._wegInfos.remove(_wegInfos.size());
		return inverser;
	}

}
