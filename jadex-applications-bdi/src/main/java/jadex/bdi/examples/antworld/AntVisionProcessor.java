package jadex.bdi.examples.antworld;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;

/**
 * Simple ant vision processer. Updates the agent's beliefset according to the
 * percepts felt gravitation.
 */
public class AntVisionProcessor extends SimplePropertyObject implements IPerceptProcessor {
	/**
	 * Process a new percept.
	 * 
	 * @param space
	 *            The space.
	 * @param type
	 *            The type.
	 * @param percept
	 *            The percept.
	 * @param agent
	 *            The agent identifier.
	 */
	public void processPercept(ISpace space, final String type, final Object percept, IAgentIdentifier agent) {

		IAMS ams = (IAMS) ((ApplicationContext) space.getContext()).getPlatform().getService(IAMS.class);
		ams.getExternalAccess(agent, new IResultListener() {
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}

			public void resultAvailable(Object result) {
				IExternalAccess exta = (IExternalAccess) result;
				// IBeliefSet garbages =
				// exta.getBeliefbase().getBeliefSet("garbages");
				if (AntVisionGenerator.GRAVITATION_FELT.equals(type)) {
					Vector2Int gravitationCenter = (Vector2Int) percept;
					 System.out.println("Setting belief in Ant! Ant is now influenced by follwing gravitation center: " + gravitationCenter.toString());
					exta.getBeliefbase().getBelief("hasGravitation").setFact(new Boolean(true));
					IGoal gravitationInfluence = exta.createGoal("gravitation_influence");
					gravitationInfluence.getParameter("gravitation_center").setValue((IVector2) gravitationCenter);								     
					exta.dispatchTopLevelGoal(gravitationInfluence);
//					exta.dispatchTopLevelGoalAndWait(goal)
				}
				// else
				// if(BurnerVisionGenerator.AntVisionGenerator.equals(type))
				// {
				// // System.out.println("garbage disappeared: "+percept);
				// if(garbages.containsFact(percept))
				// garbages.removeFact(percept);

			}
		});
	}
}