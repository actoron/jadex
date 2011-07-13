package deco4mas.coordinate.interpreter.agent_state;

import jadex.bridge.IComponentStep;

/**
 * This class holds information for the coordination of component steps.
 * 
 * @author Thomas Preisler
 */
public class CoordinationStepDetails {

	private IComponentStep step = null;

	/**
	 * @return the step
	 */
	public IComponentStep getStep() {
		return step;
	}

	/**
	 * @param step
	 *            the step to set
	 */
	public void setStep(IComponentStep step) {
		this.step = step;
	}

	private String simpleClassName = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinationStepDetails [" + (step != null ? "step=" + step + ", " : "") + (simpleClassName != null ? "simpleClassName=" + simpleClassName : "") + "]";
	}

	/**
	 * @return the simpleClassName
	 */
	public String getSimpleClassName() {
		return simpleClassName;
	}

	/**
	 * @param step
	 * @param simpleClassName
	 */
	public CoordinationStepDetails(IComponentStep step, String simpleClassName) {
		super();
		this.step = step;
		this.simpleClassName = simpleClassName;
	}

	/**
	 * @param simpleClassName
	 *            the simpleClassName to set
	 */
	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}
}