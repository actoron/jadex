/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class forces it's sub classes to implement the {@link IStrategy} interface. This class supports the implementation by offering some useful functions.
 * 
 * @author thomas
 */
public abstract class GenericStrategy implements IStrategy {

	/**
	 * Returns all the matching roles from the given {@link List} of {@link Role}s which are applicable by the given array of capabilities (as {@link String}s).
	 * 
	 * @param roles
	 *            the given {@link List} of {@link Role}s
	 * @param capabilities
	 *            the given array of capabilities (as {@link String}s
	 * @return all the matching roles
	 */
	protected List<Role> getMatchingRoles(List<Role> roles, String[] capabilities) {
		List<Role> newRoles = new ArrayList<Role>();

		for (Role role : roles) {
			if (contains(capabilities, role.getCapabilityAsString())) {
				newRoles.add(role);
			}
		}

		return newRoles;
	}

	/**
	 * Checks if the given {@link String}[] contains the given {@link String}.
	 * 
	 * 
	 * @param capabilities
	 *            the given {@link String}[]
	 * @param capString
	 *            the given {@link String}
	 * @return <code>true</code> if the given {@link String}[] contains the given {@link String} else <code>false</code>
	 */
	private boolean contains(String[] capabilities, String capString) {
		for (String cap : capabilities) {
			if (cap.equals(capString)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns a {@link List} with all the deficient {@link Capability} from the given {@link List} of deficient {@link Role}s.
	 * 
	 * @param deficientRoles
	 *            the given {@link List} of deficient {@link Role}s
	 * @return a {@link List} with all the deficient {@link Capability}
	 */
	protected List<Capability> getDeficientCaps(List<Role> deficientRoles) {
		List<Capability> defCaps = new ArrayList<Capability>();

		for (Role defRole : deficientRoles) {
			Capability defCap = defRole.getCapability();

			if (!defCaps.contains(defCap)) {
				defCaps.add(defCap);
			}
		}

		return defCaps;
	}
}