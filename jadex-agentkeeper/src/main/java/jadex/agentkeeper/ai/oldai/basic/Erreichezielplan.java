package jadex.agentkeeper.ai.oldai.basic;

import jadex.agentkeeper.ai.oldai.creatures.KreaturenPlan;
import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;



@SuppressWarnings("serial")
public class Erreichezielplan extends KreaturenPlan {
	boolean _direkt;
	protected Auftragsverwalter _auftragsverwalter;

	AStarSearch _suche;
	
	IVector2 zielvektor;

	int _schritt = 0;
	int zwischenschritte = 10;
	double x_nd;
	double y_nd;
	double x_m;
	double y_m;
	
	public Erreichezielplan() {
		super();

		IVector2 zielpos = (IVector2) getParameter("ziel").getValue();
		_direkt = (Boolean) getParameter("direkt").getValue();
		zielvektor = zielpos.copy();
		IVector2 startvektor = _mypos.copy();
		


		
		grid =  (Grid2D)getBeliefbase().getBelief("environment").getFact();
		
		_suche = new AStarSearch(startvektor, zielvektor, grid, _direkt);
		
		_verbrauchsgrad = 0;
	}



	public void aktion() {

		if (_suche.istErreichbar()) {
			
			//TODO: was soll schritt?
			if (_schritt == 0) {
				
				//TODO: hier gut?
				Stack<Vector2Int> pfad = _suche.gibPfad();


				if (pfad.isEmpty()) {
					_ausfuehr = false;
					return;
				}
				_mypos = (Vector2Double) _avatar.getProperty(Space2D.PROPERTY_POSITION);
				Vector2Int myposInt = new Vector2Int(_mypos.copy().getXAsInteger(), _mypos.copy().getYAsInteger());
				
				
				Vector2Int nextp = new Vector2Int(pfad.pop());
				if(nextp.equals(myposInt))
				{
					nextp = new Vector2Int(pfad.pop());
				}
				_avatar.setProperty("intPos", nextp);
				_myIntPos = nextp;
				testUmgebung(grid, nextp);
				
				

				x_m = _mypos.copy().getXAsDouble();
				y_m = _mypos.copy().getYAsDouble();
				double x_ne = nextp.copy().getXAsDouble();
				double y_ne = nextp.copy().getYAsDouble();
				x_nd = x_ne - x_m;
				y_nd = y_m - y_ne;

				bewegung();
			}
			else {
				bewegung();
			}

		}
		else {
			 
			fail( new Throwable("Ziel leider nicht erreichbar, Wegfindung liefert keinen Weg"));
		}
	}

	private void bewegung() {
		double x_step = (x_nd) / zwischenschritte;
		double y_step = (y_nd) / zwischenschritte;

		Vector2Double newpos = new Vector2Double(x_m + (_schritt * x_step), y_m - (_schritt * y_step));


		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.OBJECT_ID, _avatar.getId());
		params.put(GetPosition.PARAMETER_POSITION, newpos);

		grid.performSpaceAction("move", params, new IResultListener() {
			public void resultAvailable(Object result) {
				
				_avatar.setProperty("status", "Walk");
			}

			public void exceptionOccurred(Exception exception) {


			}
		});
		if (++_schritt > zwischenschritte) {

			_schritt = 0;
		}
	}

	
	
	public void gegnerNaehe( long id )
	{
		
	}

}
