package deco4mas.coordinate.interpreter.agent_state;

import jadex.bridge.IComponentStep;
import jadex.bridge.ITransferableStep;

/**
 * Superclass for {@link IComponentStep}s which are used for the coordination framework.
 * 
 * @author Thomas Preisler
 */
public abstract class CoordinationComponentStep implements ITransferableStep {

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.bridge.ITransferableStep#getTransferableObject()
	 */
	@Override
	public Object getTransferableObject() {
		CoordinationStepDetails details = new CoordinationStepDetails(this, this.getClass().getSimpleName());
		return details;
	}
}