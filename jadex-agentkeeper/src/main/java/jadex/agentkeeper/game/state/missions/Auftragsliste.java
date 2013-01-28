package jadex.agentkeeper.game.state.missions;

import jadex.agentkeeper.util.Neighborhood;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.LinkedList;


/**
 * Eine Klasse, die eine Auftragsliste verwaltet; 
 * Ist eine normale java.util-Liste mit Auftraegen, die 
 * eine Funktion bereitstellt, die den naechstegelegenen Auftrag
 * zurueckgibt
 * 
 * @author 8reichel
 */
@SuppressWarnings("serial")
public class Auftragsliste extends LinkedList<Auftrag>
{

	/**
	 * Initialisiert die Auftragsliste
	 */
	public Auftragsliste()
	{
		super();
	}
	
	/**
	 * Gibt den Auftrag zurueck, der am naechsten an der uebergebenen Position ist
	 * @param position Position, in deren Naehe der Auftrag sein soll
	 * @return	Naechster Auftrag
	 */
	public synchronized Auftrag gibNaechsten( Vector2Int position )
	{
		Auftrag tmpauftrag = null;
		if ( size() > 0 )
		{
			Vector1Double dist = new Vector1Double(200000.0);
			for (Auftrag auf : this ) {
				Vector2Int ziel = auf.gibZiel();
				Vector1Double tmpdist = (Vector1Double) ziel.getDistance(position);
				if ( tmpdist.less(dist) )
				{
					dist = tmpdist;
					tmpauftrag = auf;
				}
			}
			this.remove(tmpauftrag);
		}
		else
		{

		}

		return tmpauftrag;
			
	}
	
	public synchronized Auftrag checkNeighborfields( Vector2Int position )
	{
		
		for(int i = 0; i< Neighborhood.complexDirections.length ; i++ ) 
		{
			for (Auftrag auf : this ) 
			{
				Vector2Int test = (Vector2Int) position.copy().add(Neighborhood.complexDirections[i]);

				if(auf.gibZiel().equals(test))
						{
					this.remove(auf);
					return auf;
						}
				
			}
		}
		
		return null;
			
	}
}
