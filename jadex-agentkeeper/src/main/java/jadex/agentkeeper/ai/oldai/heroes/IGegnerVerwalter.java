package jadex.agentkeeper.ai.oldai.heroes;

import jadex.agentkeeper.game.state.missions.IAuftragsverwalter;

/**
 * Interface f�r eine Gegnerverwaltung
 * Soll vom Gegnerverwalter implementiert werden
 * Denkbar sind verschiedene Gegnerverwaltungstypen,
 * die sich z.B. in Laufart der Gegner,
 * Besiegtbedingungen etc. unterscheiden
 * 
 */
public interface IGegnerVerwalter extends IAuftragsverwalter 
{
	/**
	 * Gibt an, ob der Gegner besiegt ist (fuer Spielendbedingungen)
	 * @return Ob Gegner besiegt ist
	 */
	public boolean istBesiegt();
	
	/**
	 * Wird von Gegnereinheit aufgerufen, wenn sie tod ist
	 * (Iwie muss da der Agent ne Eigenschaft von sich uebergeben)
	 */
	public void istTod( String id );
	
	/**
	 * Registriert einen neuen Gegner, damit der Gegnerverwalter weiss, wie 
	 * viele Gegner vorhanden sind
	 * @param id Id des neuen Gegners
	 */
	public void registriereGegner( String id );
}
