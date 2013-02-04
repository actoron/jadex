package jadex.agentkeeper.ai.creatures.imp;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.bdi.runtime.IGoal;

/**
 * TODO: Refractor in English and as BDIv3 Agent-Plan
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 *
 */
public class WandabbauPlan extends ImpPlan
{
	public static int	ABBAUZEIT	= 45;

	public WandabbauPlan()
	{
		_verbrauchsgrad = 0;
	}

	@Override
	public void aktion()
	{
		ladAuftrag();

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

			bearbeite(_zielpos, ABBAUZEIT);
			
			
		}


		// Click effekt entfernen
		for(Object o : (grid.getSpaceObjectsByGridPosition(_zielpos, null)))
		{
			if(o instanceof ISpaceObject)
			{
				ISpaceObject blub = (ISpaceObject)o;
				blub.setProperty("clicked", false);
			}
		}

		if(isCorrectField(_zielpos, InitMapProcess.GOLD))
		{
			setze(_zielpos, InitMapProcess.GOLD2, true);

			IGoal sammele = createGoal(Auftragsverwalter.GOLDSAMMELN);
			Auftrag auf = new Auftrag(Auftragsverwalter.GOLDSAMMELN, _zielpos);
			sammele.getParameter("auftrag").setValue(auf);
			dispatchSubgoalAndWait(sammele);
		}
		else
		{
			setze(_zielpos, InitMapProcess.DIRT_PATH, true);
			auftragsverwalter.neuerAuftrag(Auftragsverwalter.BESETZEN, _zielpos);
		}

		auftragsverwalter.updatePosition(_zielpos);
		
		_ausfuehr = false;

	}

	@Override
	public void passed()
	{
		super.passed();
		auftragsverwalter.neuerAuftrag(Auftragsverwalter.BESETZEN, _zielpos);
	}

}
