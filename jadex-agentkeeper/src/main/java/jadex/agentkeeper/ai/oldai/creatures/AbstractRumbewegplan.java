package jadex.agentkeeper.ai.oldai.creatures;



public abstract class AbstractRumbewegplan extends KreaturenPlan {


	protected void aktion() {
		_avatar.setProperty("status", "Idle");
		testAuftraege();
	}
	
	protected abstract void testAuftraege();

}
