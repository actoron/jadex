package jadex.agentkeeper.game.state.missions;

import jadex.agentkeeper.ai.oldai.heroes.IGegnerVerwalter;
import jadex.agentkeeper.game.userinput.UserEingabenManager;
import jadex.extension.envsupport.environment.IEnvironmentSpace;

/**
 * Datenhaltungsklasse fuer die Missionen
 * @author 8reichel
 *
 */
public class Mission {
	public static String GOLDMENGE = "Goldmenge";
	public static String MANAMENGE = "Manamenge";
	public static String FORSCHUNG = "Forschung";
	public static String CLAIMMENGE = "Claimmenge";
	public static String BESIEGEN = "Besiegen";
	public static String MONSTER = "Monster";
	public static String IMPS = "Imps";

	String _typ;
	int _menge;
	boolean _istErfuellt;
	UserEingabenManager _uem;
	IEnvironmentSpace _space;
	IGegnerVerwalter _gegnerverwalter;
	
	public Mission(String typ, UserEingabenManager uem) {
		this(typ, 0, uem);
	}

	public Mission(String typ, int menge, UserEingabenManager uem) {
		_typ = typ;
		_menge = menge;
		_istErfuellt = false;
		_uem = uem;
		_space = _uem.gibSpace();
		_gegnerverwalter = (IGegnerVerwalter) _space.getProperty( "gegnerverwalter" );
	}

	public int gibMenge() {
		return _menge;
	}

	public String gibTyp() {
		return _typ;
	}

	public boolean istErfuellt() {
		return _istErfuellt;
	}

	public void teste() {
		if (_typ.equals(GOLDMENGE)) {
			if ( _uem.gibGold() >= _menge) {
				_istErfuellt = true;
			}
			else {
				_istErfuellt = false;
			}
		}

		if (_typ.equals(MANAMENGE)) {
			if ( _uem.gibMana() >= _menge) {
				_istErfuellt = true;
			}
			else {
				_istErfuellt = false;
			}
		}

		if (_typ.equals(FORSCHUNG)) {
			if ( _uem.gibForschung() >= _menge) {
				_istErfuellt = true;
			}
			else {
				_istErfuellt = false;
			}
		}

		if (_typ.equals(CLAIMMENGE)) {
			if ( _uem.gibGeclaimt() >= _menge) {
				_istErfuellt = true;
			}
			else {
				_istErfuellt = false;
			}
		}

		if (_typ.equals(IMPS)) {
			if ( _uem.gibImps() >= _menge) {
				_istErfuellt = true;
			}
			else {
				_istErfuellt = false;
			}
		}

		if (_typ.equals(MONSTER)) {
			if ( _uem.gibMonster() >= _menge )
			{
				_istErfuellt = true;
			}
			else
			{
				_istErfuellt = false;
			}
		}
		if ( _typ.equals(BESIEGEN) )
		{
			if ( _gegnerverwalter.istBesiegt() )
			{
				_istErfuellt = true;
			}
			else
			{
				_istErfuellt = false;
			}
		}
		
	}

	public String toString() {
		String ret = "This:" + super.toString() + "Typ: " + _typ + "; Menge: " + _menge + "; Erfuellt: " + _istErfuellt;
		return ret;
	}
}
