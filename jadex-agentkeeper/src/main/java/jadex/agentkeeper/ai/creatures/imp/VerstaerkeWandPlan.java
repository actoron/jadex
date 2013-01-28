package jadex.agentkeeper.ai.creatures.imp;

import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.SpaceObject;


public class VerstaerkeWandPlan extends ImpPlan {
	public static int VERSTAERKDAUER = 20;
	
	public VerstaerkeWandPlan()
	{
		_verbrauchsgrad = 0;
	}
	

	public void aktion() {
//		 System.out.println("beginne Wandverstärkeplan");
		ladAuftrag();

		if (isCorrectField(_zielpos, InitMapProcess.ROCK)) {
			
			SpaceObject field = InitMapProcess.getFieldTypeAtPos(_zielpos, grid);
			
			if(!((Boolean)field.getProperty("locked")))
			{
				waitForTick();
			}
			
			if(!((Boolean)field.getProperty("locked")))
			{
				field.setProperty("locked", true);
				erreicheZiel(_zielpos, false);
			
				_avatar.setProperty("status", "Dig");
			
				bearbeite(_zielpos, VERSTAERKDAUER);

				setze(_zielpos, InitMapProcess.REINFORCED_WALL, false);
			}
		}
		else {
//			 System.out.println("verstaerkewandplan: kein ROCK");
		}
		
		
		_ausfuehr = false;

	}
	
	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
//		System.out.println("Verstaerkewandplan: failed");
	}
}
