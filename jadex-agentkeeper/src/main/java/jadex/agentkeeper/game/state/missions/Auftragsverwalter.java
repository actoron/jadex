package jadex.agentkeeper.game.state.missions;

import jadex.agentkeeper.view.selection.SelectionArea;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * Verwaltet die Auftraege der Imps
 * @author 8reichel
 *
 */
public class Auftragsverwalter implements IAuftragsverwalter {

	public static final String WANDABBAU = "wandabbau";
	public static final String VERSTAERKEWAND = "verstaerkewand";
	public static final String GOLDSAMMELN = "goldsammeln";
	public static final String BESETZEN = "besetzefeld";
	public static final String ERREICHEZIEL = "basis.erreicheziel";
	public static final String ERREICHEEINHEIT = "basis.erreicheeinheit";
	public static final String KAPUTTMACHEN = "kaputtmachen";
	public static final String ANGREIFEN = "angreifen";
	
	private BreakWallList breakWallList;

	// 1. Prio Auftr�ge
	Auftragsliste _auftraege[];

	/**
	 * Initialisiert die Auftragsliste
	 */
	public Auftragsverwalter(Grid2D grid) {
		
		breakWallList = new BreakWallList(grid);

		_auftraege = new Auftragsliste[3];
		//Prio 1
		_auftraege[0] = new Auftragsliste();
		//Prio 2
		_auftraege[1] = new Auftragsliste();
		//Prio 3
		_auftraege[2] = new Auftragsliste();

	}

	/**
	 * Gibt den Naechsten Auftrag mit der wichtigsten Prioritaet zurueck
	 */
	boolean loading = false;
	public synchronized Auftrag gibDichtestenAuftrag(Vector2Int position) {
		if(position != null)
		{
			if(!loading)
			{
				loading = true;
				Auftrag auf = breakWallList.getClosest(position);
				if(auf!=null)
				{
					loading = false;
					return auf;
				}
				for(int i = 0; i<_auftraege.length; i++ )
				{
					if (!_auftraege[i].isEmpty()) {
						Auftrag a = _auftraege[i].checkNeighborfields( position );
						if (a != null) {
							loading = false;
							return a;
						}
					
						Auftrag b = _auftraege[i].gibNaechsten( position );
						if (b != null) {
							loading = false;
							return b;
						}
					}
				}
				
				
				loading = false;
				

				
				
			}
			else
			{
				return null;
			}


			return null;
		}
		else
		{
			
			return null;
		}

	}
	
	/**
	 * Loescht den Auftrag, der an der Position ist
	 * @param position
	 */
	public synchronized void deleteAuftragAnKoord(IVector2 position) {
		for(int i = 0; i<_auftraege.length; i++ )
		{
		if (!_auftraege[i].isEmpty()) {
			for (Auftrag auf : _auftraege[i]) {
				if (position.equals(auf.gibZiel())) {
					_auftraege[i].remove(auf);
				}
			}
		}
		}
	}

	/**
	 * Setzt den Auftrag an der Position auf bearbeitet
	 */
	public synchronized void setzBearbeitet(Auftrag auf) {
		for(int i = 0; i<_auftraege.length; i++ )
		{
			if (_auftraege[i].contains(auf)) {
				_auftraege[i].remove(auf);
				return;
			}
		}
	}

	/**
	 * F�gt einen neuen Auftrag in die Auftragsliste ein
	 * 
	 * @param a
	 */
	public synchronized void neuerAuftrag(String typ, Vector2Int position) {
		
		Auftrag a = new Auftrag(typ, position);

		boolean enthalten = false;
		for(int i = 0; i<_auftraege.length; i++ )
		{
			if (!_auftraege[i].isEmpty()) {
				for (Auftrag auf : _auftraege[i]) {
					if (auf.gibZiel().equals(position)) {
						enthalten = true;
					}

				}
			}
		}

	

		if (!enthalten) {
			if (typ.equals(WANDABBAU)) {
				_auftraege[0].add(a);
			}
			else if (typ.equals(BESETZEN)) {
				_auftraege[1].add(a);
			}
			else {
				_auftraege[2].add(a);
			}
		}
	}
	
	public synchronized void newBreakWalls(SelectionArea area)
	{
		breakWallList.newBreakWalls(area);
	}
	
	public synchronized void updatePosition(IVector2 position)
	{
		breakWallList.updatePosition(position);
	}
	

	@Override
	public synchronized String toString() {
		return "0: " + _auftraege[0] + "\n 1: " + _auftraege[1];
	}
}
