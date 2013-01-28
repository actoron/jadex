package jadex.agentkeeper.game.state.missions;

import jadex.extension.envsupport.math.Vector2Int;

import java.util.LinkedList;
import java.util.List;

/**
 * Klasse, die alle Gebaeude verwalten soll
 * @author 8reichel
 *
 */
public class Gebaudeverwalter 
{
	private List<Gebaeude> _gebaeude; // Liste mit Gebaeuden
	
	public Gebaudeverwalter()
	{
		_gebaeude = new LinkedList<Gebaeude>();
	}
	
	/**
	 * Gibt alle Gebaeude mit einem bestimmten Typ zurueck
	 * @param typ Typ der Gebaeude
	 * @return Liste mit dem Typ der Gebaeude
	 */
	public synchronized List<Gebaeude> gibGebaeude( String typ )
	{
		List<Gebaeude> temp = new LinkedList<Gebaeude>();
		for ( Gebaeude g : _gebaeude )
		{
			if ( g.gibTyp().equals( typ ) ) 
			{
				temp.add( g );
			}
		}
		return temp;
	}
	
	/**
	 * Fuegt ein neues Gebaeude an der gegebenen Position mit dem gegebenen Typ in die Liste ein
	 * @param pos
	 * @param typ
	 */
	public synchronized void machGebaeude( Vector2Int pos, String typ )
	{
		_gebaeude.add( new Gebaeude( pos , typ) );
	}

	/**
	 * Loescht ein Gebaeude aus der Liste
	 * @param zielpos
	 */
	public synchronized void loeschen( Vector2Int zielpos) {
		Gebaeude loesch = null;
		for ( Gebaeude g : _gebaeude )
		{
			if ( g.gibPos().equals( zielpos ) )
			{
				loesch = g;
			}
		}
		if ( loesch != null )
		{
			_gebaeude.remove( loesch );
		}
	}
	
	public String toString(){
		return _gebaeude.toString();
	}
}
