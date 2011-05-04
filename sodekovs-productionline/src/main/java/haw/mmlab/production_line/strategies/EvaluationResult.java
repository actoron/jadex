/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of an evaluation. The result contains a {@link List} of {@link Role}s which the agent has to take and an other {@link List} of {@link Role}s which the agent has to give away.
 * 
 * @author thomas
 */
public class EvaluationResult {

	private List<Role> takeRoles = null;
	private List<Role> giveAwayRoles = null;
	private boolean reconfigure = false;

	/**
	 * Constructor
	 */
	public EvaluationResult() {
		super();
		this.takeRoles = new ArrayList<Role>();
		this.giveAwayRoles = new ArrayList<Role>();
	}

	/**
	 * @return the takeRoles
	 */
	public List<Role> getTakeRoles() {
		return takeRoles;
	}

	/**
	 * @param takeRoles
	 *            the takeRoles to set
	 */
	public void setTakeRoles(List<Role> takeRoles) {
		this.takeRoles = takeRoles;
	}

	/**
	 * @return the giveAwayRoles
	 */
	public List<Role> getGiveAwayRoles() {
		return giveAwayRoles;
	}

	/**
	 * @param giveAwayRoles
	 *            the giveAwayRoles to set
	 */
	public void setGiveAwayRoles(List<Role> giveAwayRoles) {
		this.giveAwayRoles = giveAwayRoles;
	}

	/**
	 * @return the reconfigure
	 */
	public boolean isReconfigure() {
		return reconfigure;
	}

	/**
	 * @param reconfigure
	 *            the reconfigure to set
	 */
	public void setReconfigure(boolean reconfigure) {
		this.reconfigure = reconfigure;
	}
}