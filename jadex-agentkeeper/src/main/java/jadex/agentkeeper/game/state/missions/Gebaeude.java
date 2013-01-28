package jadex.agentkeeper.game.state.missions;

import jadex.extension.envsupport.math.Vector2Int;
/**
 * Klasse, die Daten fuer ein Gebaeude halten soll
 * @author 8reichel
 *
 */
public class Gebaeude {

	Vector2Int _pos; // Position des Gebaeudes
	String _typ; // Typ des Gebaeudes
	
	/**
	 * Initialisiert das Gebaeude mit Position und Typ
	 * @param pos Position des Gebaeudes
	 * @param typ Typ des Gebaeudes
	 */
	public Gebaeude( Vector2Int pos, String typ )
	{
		_typ = typ;
		_pos = pos;
	}
	
	/**
	 * Gibt den Typ des Gebaeudes zurueck

	 */
	public String gibTyp() {
		return _typ;
	}
	
	/**
	 * Gibt die Position des Gebaeudes zurueck

	 */
	public Vector2Int gibPos( )
	{
		return _pos;
	}
	
	public String toString()
	{
		return "Position: "+_pos+" Typ: "+_typ;
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof Gebaeude )
		{
			Gebaeude anderes = (Gebaeude) o;
		    if ( anderes.gibPos().equals( _pos ) &&
		    	 anderes.gibTyp().equals( _typ ) )
		    {
		    	return true;
		    }
		    else
		    {
		    	return false;
		    }
		}
		else
		{
			return false;
		}
	}

}
