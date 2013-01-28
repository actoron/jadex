package jadex.agentkeeper.game.state.missions;

import jadex.extension.envsupport.math.Vector2Int;

/**
 * Basisfunktionalit�ten von Auftragsverwaltern, die von allen Auftragsverwaltern
 * implementiert werden m�ssen
 * @author 8reichel
 *
 */
public interface IAuftragsverwalter {
	
	/**
	 * Erstellt einen neuen Auftrag und packt ihn in die Auftragsliste
	 * @param typ
	 * @param position Position, an der Auftrag stattfindet (im ganzzahligen Koordinatensystem
	 */
	public void neuerAuftrag(String typ, Vector2Int position);
	
	
	/**
	 * Setzt einen Auftrag auf bearbeitet (wird vom Imp aufgerufen, wenn er beginnt, ihn zu bearbeiten
	 * @param auf
	 */
	public void setzBearbeitet(Auftrag auf);
	
	/**
	 * Gibt den Auftrag zur�ck, der am n�chsten dran ist
	 * (bzw. aufgrund der Abl�ufe des Auftragsverwalter den priorisierten)
	 * @param position

	 */
	public Auftrag gibDichtestenAuftrag(Vector2Int position);

	
}
