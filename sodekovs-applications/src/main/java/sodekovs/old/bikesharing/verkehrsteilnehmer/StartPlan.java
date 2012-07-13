package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * Der Plan, mit dem der Agent startet, und der ihn initialisiert,
 * indem er die relevanten Orte als Wissen speichert
 * @author dagere
 *
 */
public class StartPlan extends Plan
{

	@Override
	public void body()
	{
		System.out.println("Initialisiere");
		
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getScope().getParent();
//
//		Grid2D space = (Grid2D) app.getSpace("simulationsspace");
//		ISpaceObject _avatar = SelbstBewegPlan.gibAvatar(getScope().getParent(), this);
		ISpaceObject _avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
		
		Vector2Double wohnort = (Vector2Double) _avatar.getProperty( ZielWaehlPlan.WOHNORT );
		Vector2Double arbeitsort = (Vector2Double) _avatar.getProperty( ZielWaehlPlan.ARBEITSORT );
		
		Vector2Int wohnortInt = new Vector2Int( wohnort.getXAsInteger(), wohnort.getYAsInteger() );
		Vector2Int arbeitsortInt = new Vector2Int( arbeitsort.getXAsInteger(), arbeitsort.getYAsInteger() );
		
		getBeliefbase().getBelief(ZielWaehlPlan.WOHNORT).setFact( wohnortInt );
		getBeliefbase().getBelief(ZielWaehlPlan.ARBEITSORT).setFact( arbeitsortInt );
		getBeliefbase().getBelief(ZielWaehlPlan.GEARBEITET).setFact( new Boolean( false ) );
		getBeliefbase().getBelief(ZielWaehlPlan.NAECHSTEBEWEGUNG).setFact( 7*60 );
		
		IGoal goal = createGoal("zielwaehlen");
		dispatchTopLevelGoal(goal);
//		dispatchSubgoal(goal);
	}
 
}
