package jadex.agentkeeper.ai.pathfinding;

import jadex.extension.envsupport.math.IVector2;

/**
 * Eine Klasse fuer die Datenhaltung der Wegpunkte (nur relevant f�r die
 * Pfadsuche) und dadurch auch die Wegkostenverarbeitung
 * 
 * @author 7willuwe
 * 
 */
public class Waypoint {
	public int _fwert;
	public int _gwert;
	public int _hwert;
	public IVector2 _punkt;
	public IVector2 _vorgaengerpunkt;

	public Waypoint(IVector2 punkt, IVector2 vorgaengerpunkt, int gwert, int hwert) {

		_vorgaengerpunkt = vorgaengerpunkt;
		_punkt = punkt;
		_hwert = hwert;
		_gwert = gwert;
		_fwert = hwert + gwert;
	}

	public IVector2 gibpunkt() {
		return _punkt;
	}

	public IVector2 gibvorgangerpunkt() {
		return _vorgaengerpunkt;
	}

}
