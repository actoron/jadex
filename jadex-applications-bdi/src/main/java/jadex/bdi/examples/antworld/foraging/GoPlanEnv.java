package jadex.bdi.examples.antworld.foraging;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.antworld.GravitationListener;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Go to a specified position.
 */
public class GoPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		Boolean hasGravitation = (Boolean) getBeliefbase().getBelief("hasGravitation").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 target = (IVector2)getParameter("pos").getValue();
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();		
		myself.setProperty(GravitationListener.FEELS_GRAVITATION, hasGravitation);
		
		//TEST**************************************
//		SyncResultListener srl1 = new SyncResultListener();
//		env.performSpaceAction("testAction", null, srl1);
//		srl1.waitForResult();
//		
////		waitFor(5000);
//		
//		SyncResultListener srl2 = new SyncResultListener();
//		Map props1 = new HashMap();
//		props1.put("SecondTime", new String("yes"));
//		env.performSpaceAction("testAction", props1, srl2);
//		srl2.waitForResult();
		
		
		//TEST**************************************
		
		//Update destination and gravitationSensor of ant on space 
		Map params = new HashMap();
		params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getAgentIdentifier()).getId());
		params.put(UpdateDestinationAction.DESTINATION, target);		
		params.put(GravitationListener.FEELS_GRAVITATION, hasGravitation);
		SyncResultListener srl = new SyncResultListener();
		env.performSpaceAction("updateDestination", params, srl); 
		srl.waitForResult();
		
		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
		{
			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			String dir = null;
			int mx = mypos.getXAsInteger();
			int tx = target.getXAsInteger();
			int my = mypos.getYAsInteger();
			int ty = target.getYAsInteger();

			assert mx!=tx || my!=ty;

			if(mx!=tx)
			{
				dir = GoAction.RIGHT;
				int dx = Math.abs(mx-tx);
				if(mx>tx && dx<=size.getXAsInteger()/2)
					dir = GoAction.LEFT;
			}
			else
			{
				dir = GoAction.DOWN;
				int dy = Math.abs(my-ty);
				if(my>ty && dy<=size.getYAsInteger()/2)
					dir = GoAction.UP;
			}

			//System.out.println("Wants to go: "+dir);
					
			params = new HashMap();
			params.put(GoAction.DIRECTION, dir);
			params.put(ISpaceAction.OBJECT_ID, env.getAvatars(getAgentIdentifier())[0].getId());			
			srl	= new SyncResultListener();
			env.performSpaceAction("go", params, srl); 
			srl.waitForResult();
			
			//Update trace route of ant in space 
			params = new HashMap();
			params.put(ISpaceAction.OBJECT_ID, env.getAvatars(getAgentIdentifier())[0].getId());
			params.put(TraceRouteAction.POSITION, mypos);
			params.put(TraceRouteAction.ROUND, new Integer(1));
			srl = new SyncResultListener();
			env.performSpaceAction("updateTraceRoute", params, srl); 
			srl.waitForResult();

//			String obj = new String("a");
//			getBeliefbase().getBeliefSet("wastes").addFact(obj);
			ISpaceObject[] wastes = null;
			wastes = (ISpaceObject[])getBeliefbase().getBeliefSet("wastes").getFacts();			
			System.out.println("#GoPlanEnv# Number of WasteObjects" + wastes.length );
			for(int i=0; i<wastes.length; i++){
				System.out.println(wastes[i].toString());
			}
			
//			ISpaceObject wastes = null;
//			wastes = (ISpaceObject)getBeliefbase().getBelief("wastes").getFact();
//			if(wastes != null){
//			System.out.println("#GoPlanEnv# WasteObjects: " + wastes.getId() );
//			}else{
//				System.out.println("#GoPlanEnv# WasteObjects == null!!");
//			}
			
			
		}
	}
}
