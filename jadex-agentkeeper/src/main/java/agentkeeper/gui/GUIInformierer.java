package agentkeeper.gui;

/**
 * Arbeitet mit dem Listener zusammen um die GUI zu Informieren
 * 
 * @author 7willuwe
 * 
 */

import java.util.Vector;

public class GUIInformierer {

	static Vector<Listener> _listener;

	static {
		_listener = new Vector<Listener>();
	}

	public static void addListener(Listener listener) {
		if (!(_listener.contains(listener))) {
			_listener.addElement(listener);
		}
	}

	public static void aktuallisierung()
	{
		for (int i = 0; i < _listener.size(); i++) {
			Listener l = _listener.elementAt(i);
			l.aktualisierung( );
		}
	}

}