package haw.mmlab.production_line.logging;

import haw.mmlab.production_line.state.DeficientState;
import haw.mmlab.production_line.state.MainState;

/**
 * This class encapsulates a state composed of several sub states.
 * 
 * @author thomas
 */
public class State {

	/**
	 * The agent's deficient state.
	 */
	private int deficientState = DeficientState.NOT_DEFICIENT;

	/**
	 * The agent's main state.
	 */
	private int mainState = MainState.RUNNING_IDLE;

	/**
	 * The agent's number of roles.
	 */
	private int noRoles = 0;

	/**
	 * The number of elements in the agent's buffer.
	 */
	private int bufferLoad = 0;

	/**
	 * The capacity (max size) of the agent's buffer.
	 */
	private int bufferCapacity = 0;

	/**
	 * @return the noRoles
	 */
	public int getNoRoles() {
		return noRoles;
	}

	/**
	 * @param noRoles
	 *            the noRoles to set
	 */
	public void setNoRoles(int noRoles) {
		this.noRoles = noRoles;
	}

	/**
	 * @return the bufferLoad
	 */
	public int getBufferLoad() {
		return bufferLoad;
	}

	/**
	 * @param bufferLoad
	 *            the bufferLoad to set
	 */
	public void setBufferLoad(int bufferLoad) {
		this.bufferLoad = bufferLoad;
	}

	/**
	 * @return the bufferCapacity
	 */
	public int getBufferCapacity() {
		return bufferCapacity;
	}

	/**
	 * @param bufferCapacity
	 *            the bufferCapacity to set
	 */
	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

	/**
	 * @return the deficientState
	 */
	public int getDeficientState() {
		return deficientState;
	}

	/**
	 * @param deficientState
	 *            the deficientState to set
	 */
	public void setDeficientState(int deficientState) {
		this.deficientState = deficientState;
	}

	/**
	 * @return the mainState
	 */
	public int getMainState() {
		return mainState;
	}

	/**
	 * @param mainState
	 *            the mainState to set
	 */
	public void setMainState(int mainState) {
		this.mainState = mainState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "State [bufferCapacity=" + bufferCapacity + ", bufferLoad=" + bufferLoad + ", deficientState="
				+ deficientState + ", mainState=" + mainState + ", noRoles=" + noRoles + "]";
	}
}