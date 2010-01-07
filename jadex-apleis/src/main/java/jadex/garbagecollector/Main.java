package jadex.garbagecollector;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.standalone.Platform;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IContextService;

import java.util.HashMap;
import java.util.Map;

import eis.jadex.JadexDelegationEisImpl;
import eis.jadex.SpacePerceptProcessor;

/**
 * 
 */
public class Main
{
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		// Create environment of garbage collection via application
		Platform plat = new Platform("jadex/garbagecollector/standalone_conf_gc.xml", null);
		plat.start();
		
		// Create environment connector, i.e. eis 
		IContextService cs = (IContextService)plat.getService(IContextService.class);
		IApplicationContext app = (IApplicationContext)cs.getContext("gc");
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)app.getSpace("mygc2dspace");
		JadexDelegationEisImpl eis = new JadexDelegationEisImpl(space);
		
		// Add default eis percept processor for each external agent type of the app.
		String[] agenttypes = app.getAgentTypes();
		SpacePerceptProcessor perproc = new SpacePerceptProcessor(eis);
		for(int i=0; i<agenttypes.length; i++)
		{
			space.addPerceptProcessor(agenttypes[i], null, perproc);
		}
		
		// Create (external) agents and connect them to the environment
		// Problem: currently the creation of entities in EIS does not allow to specify 
		// additional infos such as the object type parameters etc.
		// Solved by creating agents here internally, i.e. inside of the application. Then
		// automatically avatars (entities) are created.
		Map params = new HashMap();
		params.put("eis", eis);
		app.createAgent("ma1", "Collector", null, params, true, false, null);
		
	}
}
