package jadex.agentkeeper.ai.oldai.basic;

import jadex.agentkeeper.ai.oldai.creatures.KreaturenPlan;
import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


@SuppressWarnings("serial")
public class ErreicheEinheitPlan extends KreaturenPlan {

	AStarSearch _suche;
	IVector2 _mypos;
	ISpaceObject _einheit;

	IVector2 _startvektor;
	IVector2 _zielvektor;

	int _schritt = 0;
	double _x_nd;
	double _y_nd;
	double _x_m;
	double _y_m;
	boolean _erreichbar;

	public ErreicheEinheitPlan() {
		super();
		_verbrauchsgrad = 0;
		_einheit = (ISpaceObject) getParameter("ziel").getValue();
		_erreichbar = false;

	}

	@Override
	protected void aktion() {



		Vector2Double einheit = (Vector2Double) _einheit
				.getProperty("position");
		Vector2Double meinepos = (Vector2Double) _avatar
				.getProperty(Space2D.PROPERTY_POSITION);

		if ((einheit.getDistance(meinepos)).getAsDouble() <= 1.0) {
			_ausfuehr = false;
			return;
		}

		if (_schritt == 0) {
			IVector2 zielpos = (IVector2) _einheit.getProperty("position");
			_mypos = (IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION);
			_startvektor = _mypos.copy();
			_zielvektor = zielpos.copy();

			_suche = new AStarSearch(_startvektor, _zielvektor, grid, true);
			_erreichbar = _suche.istErreichbar();
			if (_erreichbar) {

				Stack<Vector2Int> pfad = _suche.gibPfad();
				

				if (pfad.isEmpty()) {
					_ausfuehr = false;
 					return;
				}
				pfad.pop();
				Vector2Int nextp = new Vector2Int(pfad.pop());

				Vector2Double nextpdouble = new Vector2Double(nextp);

				IVector2 nextpos = nextpdouble.add(0.5);

				_x_m = _mypos.copy().getXAsDouble();
				_y_m = _mypos.copy().getYAsDouble();
				double x_ne = nextpos.copy().getXAsDouble();
				double y_ne = nextpos.copy().getYAsDouble();
				_x_nd = x_ne - _x_m;
				_y_nd = _y_m - y_ne;
			} else {
				_ausfuehr = false;
				return;
			}
		}

		if (_erreichbar) {
			bewegung();
		}
	}

	private void bewegung() {
		double x_step = (_x_nd) / 5;
		double y_step = (_y_nd) / 5;

		Vector2Double newpos = new Vector2Double(_x_m + (_schritt * x_step),
				_y_m - (_schritt * y_step));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.OBJECT_ID, _avatar.getId());
		params.put(GetPosition.PARAMETER_POSITION, newpos);

		grid.performSpaceAction("move", params, new IResultListener() {
			public void resultAvailable(Object source) {
			}

			public void exceptionOccurred(Exception exception) {

			}
		});
		if (++_schritt > 5) {
			_schritt = 0;
		}
	}
	
	public void gegnerNaehe( long id )
	{
		
	}

}
