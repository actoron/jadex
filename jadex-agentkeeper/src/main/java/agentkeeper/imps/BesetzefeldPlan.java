package agentkeeper.imps;

import jadex.extension.envsupport.environment.SpaceObject;
import agentkeeper.map.InitMapProcess;

@SuppressWarnings("serial")
public class BesetzefeldPlan extends ImpPlan {

	public static int BESETZDAUER = 10;
	
	public BesetzefeldPlan()
	{
		_verbrauchsgrad = 0;
	}
	
	
	@Override
	public void aktion() {
		ladAuftrag();

		// Wurde es schon bebaut..?
		if (isCorrectField(_zielpos, InitMapProcess.DIRT_PATH)) {
			
			SpaceObject field = InitMapProcess.getFieldTypeAtPos(_zielpos, grid);
			
			if(!((Boolean)field.getProperty("locked")))
			{
				waitForTick();
			}
			
			if(!((Boolean)field.getProperty("locked")))
			{
			field.setProperty("locked", true);
				
			erreicheZiel(_zielpos, true);

			_avatar.setProperty("status", "Idle");
			
			bearbeite(_zielpos, BESETZDAUER);

			setze(_zielpos, InitMapProcess.CLAIMED_PATH, false);
			}
			
		}
		else
		{
			System.out.println("not correct Field BesetzefeldPlan");
		}
		
		
		_ausfuehr = false;
	}

}
