package jadex.agentkeeper.game.process;


import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.game.state.missions.MissionsVerwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.util.HashMap;

import javax.swing.JOptionPane;




/**
 * Dieser Micro Agent steuert die Laufenden Spielprozesse
 * 
 * @author 7willuwe
 */
@Properties(@NameValue(name = "space", clazz = IFuture.class, value = "$component.getParentAccess().getExtension(\"mygc2dspace\")"))
public class SpielProzesseAgent extends MicroAgent
{

	Grid2D space;
	MissionsVerwalter	_mv;

	/**
	 * Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final IExternalAccess app = (IExternalAccess) getParentAccess();

		final IComponentStep<Void> com = new IComponentStep<Void>()
		{
			int cntmonster = 0;
			int cntmana = 0;
		
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// huehner "wachsen" nach
				huehnerProduzieren();
				
				// alle 400 Ticks "k�nnte" ein Monster spawnen im Portal
				if (cntmonster > 400) {
					monsterSpawn();
					cntmonster = 0;
					
				}
				
				// mana & gui alle 50 ticks aktualisieren
				if (cntmana > 50) {
					manaAktualisieren();
					guiAktualisieren();
					cntmana = 0;
					
					//Und weil 50 Ticks ne gute Zeit ist, pruefen wir hier auch auf Sieg (und ausserdem gabs ja dann grad Mana..)
					if ( _mv.istZuende() )
					{
						
//						IComponentIdentifier appComp = app.getComponentIdentifier();
//						IComponentManagementService service = (IComponentManagementService)getServiceContainer().getService( IComponentManagementService.class, appComp );
//						service.destroyComponent( appComp );
//						JOptionPane.showMessageDialog(null, "Sie haben gewonnen!");
					}
					
					ISpaceObject[] center = space.getSpaceObjectsByType( InitMapProcess.DUNGEONHEARTCENTER );
					boolean besiegt = true;
					for ( ISpaceObject objekt : center )
					{
						if ( objekt.getProperty("type").equals(InitMapProcess.DUNGEONHEARTCENTER ) )
						{
							besiegt = false;
						}
					}
//							System.out.println("L�nge: "+center.length);
					if ( besiegt )
					{
						IComponentIdentifier appComp = app.getComponentIdentifier();
						IComponentManagementService service = (IComponentManagementService)getServiceContainer().getService( IComponentManagementService.class, appComp );
						service.destroyComponent( appComp );
						JOptionPane.showMessageDialog(null, "Sie haben verloren, ihr DungeonHeart ist gestorben!");
					}
				}
				
				cntmana++;
				cntmonster++;
				
				waitForTick(this);
				
				return IFuture.DONE;
			}
			
			public String toString() 
			{
				return "imp.body()";
			}
		};

		app.getExtension("mygc2dspace").addResultListener(new ExceptionDelegationResultListener<IExtensionInstance, Void>(ret)
		{
			public void customResultAvailable(IExtensionInstance xi) 
			{
				space = (Grid2D)xi;
				_mv = (MissionsVerwalter)space.getProperty("missionsverwalter");
				waitForTick(com);
			}
		});
		
		return ret;
			
	}
	
	
	private void guiAktualisieren() {
//		GUIInformierer.aktuallisierung();
	}

	private void manaAktualisieren() {
		int warlocks = 0;
		for (@SuppressWarnings("unused")
		Object o : space.getSpaceObjectsByType("warlock")) {
			warlocks++;
		}

		int altmana = (Integer) space.getProperty("mana");
		int neumana = altmana + 5 + (5 * warlocks);
		space.setProperty("mana", neumana);
//		GUIInformierer.aktuallisierung();

	}

	private void monsterSpawn() {
		if (checkNochPlatz(space)) {
			if (checkWarlockLibrary(space)) {

				String type = "warlock";
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put("type", "goblin");
				props.put("spieler", new Integer(1) );
				props.put(Space2D.PROPERTY_POSITION, InitMapProcess.portalort);
				space.createSpaceObject(type, props, null);
			}
			else {
				String type = "goblin";
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put("type", "goblin");
				props.put("spieler", new Integer(1) );
				props.put(Space2D.PROPERTY_POSITION, InitMapProcess.portalort);
				space.createSpaceObject(type, props, null);

			}
		}

	}

	private void huehnerProduzieren() {
		for (Gebaeude geb : InitMapProcess.gebaeuedeverwalter.gibGebaeude( InitMapProcess.HATCHERY )) {
			for (Object o : space.getSpaceObjectsByGridPosition(geb.gibPos(), InitMapProcess.HATCHERY)) {
				if (o instanceof ISpaceObject) {
					ISpaceObject feld = (ISpaceObject) o;
					String type = (String) feld.getProperty("type");
					if (type.equals(InitMapProcess.HATCHERY)) {
						double alt = (Double) feld.getProperty("huehner");
						double neuehuehner = alt + 0.005;
						feld.setProperty("huehner", neuehuehner);
					}
				}
			}
		}

	}

	boolean checkWarlockLibrary(Grid2D space) {
		int librarys = (InitMapProcess.gebaeuedeverwalter.gibGebaeude( InitMapProcess.LIBRARY )).size();
		int warlocks = 0;
		for (@SuppressWarnings("unused")
		Object o : space.getSpaceObjectsByType("warlock")) {
			warlocks++;
		}
		if ((librarys >= 4) && (librarys - (warlocks * 4)) >= 4) {
			return true;
		}
		else {
			return false;
		}
	}

	boolean checkNochPlatz(Grid2D space) {

		int lairs = (InitMapProcess.gebaeuedeverwalter.gibGebaeude( InitMapProcess.LAIR )).size();

		int goblins = 0;
		int warlocks = 0;
		for (@SuppressWarnings("unused")
		Object o : space.getSpaceObjectsByType("goblin")) {
			goblins++;
		}
		for (@SuppressWarnings("unused")
		Object o : space.getSpaceObjectsByType("warlock")) {
			warlocks++;
		}

		int monster = (goblins + warlocks);

		if (((lairs - (2 * monster)) >= 2)) {
			return true;
		}
		else {
			return false;
		}

	}



}
