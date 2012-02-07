/**
 * 
 */
package deco4mas.distributed.mechanism.pastry;

import rice.p2p.scribe.ScribeContent;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * @author thomas
 * 
 */
public class ScribeCoordinationContent implements ScribeContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5043708049148415046L;

	private CoordinationInfo ci = null;

	public ScribeCoordinationContent(CoordinationInfo ci) {
		this.ci = ci;
	}

	/**
	 * @return the ci
	 */
	public CoordinationInfo getCi() {
		return ci;
	}

	/**
	 * @param ci
	 *            the ci to set
	 */
	public void setCi(CoordinationInfo ci) {
		this.ci = ci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScribeCoordinationContent [ci=" + ci + "]";
	}
}