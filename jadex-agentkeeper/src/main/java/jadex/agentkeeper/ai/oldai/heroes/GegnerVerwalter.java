package jadex.agentkeeper.ai.oldai.heroes;

import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsliste;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.LinkedList;



/**
 * Verwaltet Gegner, u.a., um festzustellen, wann sie besiegt sind
 * 
 * @author 8reichel
 * 
 */
public class GegnerVerwalter implements IGegnerVerwalter {
	private boolean _istBesiegt;
	Auftragsliste _auftraege[];
	LinkedList<String> _einheiten;
	

	public GegnerVerwalter() {
		_istBesiegt = false;
		_auftraege = new Auftragsliste[2];
		_auftraege[0] = new Auftragsliste();
		_auftraege[1] = new Auftragsliste();
		_einheiten = new LinkedList<String>();
	}

	/**
	 * Gibt an, ob der Gegner besiegt ist
	 */

	public synchronized  boolean istBesiegt() {
		return _istBesiegt;
	}


	public synchronized void istTod(String id) {
		_einheiten.remove(id);
		if ( _einheiten.isEmpty() )
		{
			_istBesiegt = true;
			System.out.println("Ist besiegt");
		}
		

	}

	public synchronized void registriereGegner(String string) {
		_einheiten.add(string);
	}

	public synchronized Auftrag gibDichtestenAuftrag(Vector2Int position) {
		if (!_auftraege[1].isEmpty()) {
			Auftrag a = _auftraege[1].gibNaechsten(position);
			if (a != null) {
				return a;
			}
		}

		if (!_auftraege[0].isEmpty()) {
			Auftrag a = _auftraege[0].gibNaechsten(position);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

	public synchronized void setzBearbeitet(Auftrag auf) {
		// System.out.println("Auftrag: " + auf.toString());
		for (int i = 0; i < 2; i++) {
			if (_auftraege[i].contains(auf)) {
				_auftraege[i].remove(auf);
				return;
			}
		}

	}

	public synchronized void neuerAuftrag(String typ, Vector2Int position) {
		Auftrag a = new Auftrag(typ, position);
		neuerAuftrag(a);
	}

	public synchronized void neuerAuftrag(String typ, Vector2Int position,
			String zieltyp) {
		Auftrag a = new Auftrag(typ, position);
		a.setzZieltyp(zieltyp);

		neuerAuftrag(a);

	}

	public synchronized void neuerAuftrag(Auftrag a) {
		boolean enthalten = false;

		if ( a.gibTyp().equals( Auftragsverwalter.KAPUTTMACHEN ) )
		{
			for ( Auftrag auf : _auftraege[0] )
			{
				if ( auf.gibZiel().equals(a.gibZiel()) 
						&& _auftraege[0].contains( a )) {
					enthalten = true;
				}
			}
			
			if ( !enthalten )
				_auftraege[0].add( a );
		}
		
		if ( a.gibTyp().equals( Auftragsverwalter.ANGREIFEN ) )
		{
			for ( Auftrag auf : _auftraege[1] )
			{
				if ( auf.gibId() == a.gibId()
						&& _auftraege[1].contains( a )) {
					enthalten = true;
				}
			}
			
			if ( !enthalten )
				_auftraege[1].add( a );
		}

	}

	public synchronized void neuerAuftrag(String typ, Long id) {
		Auftrag a = new Auftrag(typ, id);

		neuerAuftrag(a);
	}
	
//	public synchronized String toString()
//	{
//		return "Prioritaet 0: "+_auftraege[0].toString()+" Prioritaet 1: "+_auftraege[1].toString();
//	}
}
