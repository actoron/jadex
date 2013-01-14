package agentkeeper.monster;

import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractRumbewegplan extends KreaturenPlan {


	protected void aktion() {
		_avatar.setProperty("status", "Idle");
		testAuftraege();
	}
	
	protected abstract void testAuftraege();

}
