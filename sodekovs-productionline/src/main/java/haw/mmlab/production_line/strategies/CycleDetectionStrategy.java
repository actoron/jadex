/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Buffer;
import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.domain.HelpRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Eine Erweiterung der {@link HybridStrategy}, die ein aehnliches Verhalten zeigt. Der einzige Unterschied ist, dass diese Strategie darueber Buch fuehrt von welchem Agenten welche Capabilities
 * bereits uebernommen wurden. Dadurch wird das Zyklusproblem der {@link HybridStrategy} geloest. Es werden dem kaputten Agenten keine Rolle zugewiesen, die Capabilities vorraussetzen, die bereits
 * fuer den kaputten Agenten uebernommen wurden.
 * 
 * @author thomas
 */
public class CycleDetectionStrategy extends GenericStrategy {

	private ChangeHistory history = new ChangeHistory();

	/*
	 * (non-Javadoc)
	 * 
	 * @see haw.mmlab.production_line.adaptivity.strategies.IStrategy#evaluate(haw .mmlab.production_line.domain.HelpRequest, haw.mmlab.production_line.adaptivity.strategies.AgentData)
	 */
	public EvaluationResult evaluate(HelpRequest request, AgentData data) {
		String[] capabilities = data.getCapabilities();
		List<Role> roles = data.getRoles();

		EvaluationResult result = new EvaluationResult();

		List<Role> inRoles = getMatchingRoles(request.getDeficientRoles(), capabilities);
		List<Role> outRoles = getMatchingRoles(roles, request.getCapabilities());

		List<Role> ownRoles = new ArrayList<Role>(data.getRoles());

		List<Role> takeRoles = new ArrayList<Role>();
		List<Role> giveAwayRoles = new ArrayList<Role>();

		int bufferCapacity = 0;
		int freeSlots = 0;
		int freePlaces = 0;
		Buffer buffer = data.getBuffer();
		if (buffer != null) {
			bufferCapacity = buffer.capacity();
			freeSlots = buffer.getFreeSlots();
			freePlaces = bufferCapacity < freeSlots ? bufferCapacity : freeSlots;
		}

		// escalation level 0
		if (request.getEscalationLevel() == HelpRequest.MIN_ESCALATION_LEVEL) {
			// if there are any deficient roles you can apply and the deficient
			// agent can apply any of your roles
			if (!inRoles.isEmpty() && !outRoles.isEmpty()) {

				// take all roles you can apply as long as you give away one of
				// your own roles
				int i = 0;
				while (i < inRoles.size() && i < outRoles.size()) {
					takeRoles.add(inRoles.get(i));
					giveAwayRoles.add(outRoles.get(i));

					i++;
				}

				if (!takeRoles.isEmpty()) {
					result.setTakeRoles(takeRoles);
					result.setGiveAwayRoles(giveAwayRoles);
					result.setReconfigure(true);
				}
			}
		}
		// escalation level 1
		else if (request.getEscalationLevel() == HelpRequest.MIN_ESCALATION_LEVEL + 1) {
			int roleCount = roles.size();
			// if there are any deficient roles you can apply and you have
			// enough free slots in the buffer
			while (!inRoles.isEmpty() && roleCount < freePlaces) {
				takeRoles.add(inRoles.remove(0));

				roleCount++;
			}

			if (!takeRoles.isEmpty()) {
				result.setTakeRoles(takeRoles);
				result.setReconfigure(true);
			}
		}
		// escalation level 2
		else if (request.getEscalationLevel() == HelpRequest.MIN_ESCALATION_LEVEL + 2) {

			List<Capability> deficientCaps = getDeficientCaps(request.getDeficientRoles());

			// if there are any deficient roles you can apply
			if (!inRoles.isEmpty() && !ownRoles.isEmpty()) {
				// then the deficient roles, as long as to can
				// give away one of your own roles (and the capabiliy of that
				// role is not deficient in the requesting agent)
				while (!inRoles.isEmpty() && !ownRoles.isEmpty()) {
					// don't give away any roles where the needed capability is
					// one of the deficient in the requesting agent or roles
					// which require a capability, which you have already taken
					// (in the past - history) for the requesting agent
					if (deficientCaps.contains(ownRoles.get(0).getCapability()) || history.contains(request.getAgentId(), ownRoles.get(0).getCapabilityAsString())) {
						ownRoles.remove(0);
						continue;
					}

					takeRoles.add(inRoles.remove(0));
					giveAwayRoles.add(ownRoles.remove(0));
				}

				if (!takeRoles.isEmpty()) {
					result.setTakeRoles(takeRoles);
					result.setGiveAwayRoles(giveAwayRoles);
					result.setReconfigure(true);
				}
			}
		}

		addToHistory(result, request.getAgentId());
		return result;
	}

	/**
	 * Adds the capabilities from the given {@link EvaluationResult#getTakeRoles()} for the given agent id to the history.
	 * 
	 * @param result
	 *            the given {@link EvaluationResult}
	 * @param otherAgentId
	 *            the given agent id
	 */
	private void addToHistory(EvaluationResult result, String otherAgentId) {
		if (result.isReconfigure()) {
			Set<String> takenCaps = getCapabilities(result.getTakeRoles());
			history.add(otherAgentId, takenCaps);
		}
	}

	/**
	 * Returns a {@link Set} of capabilities (as {@link String}s) which are applied in the given {@link List} of {@link Role}s.
	 * 
	 * @param roles
	 *            the given {@link List} of {@link Role}s
	 * @return a {@link Set} of capabilities (as {@link String}s)
	 */
	private Set<String> getCapabilities(List<Role> roles) {
		Set<String> capabilities = new HashSet<String>();

		for (Role role : roles) {
			capabilities.add(role.getCapabilityAsString());
		}

		return capabilities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seehaw.mmlab.production_line.adaptivity.strategies.IStrategy# getMaximumEscalationLevel()
	 */
	public int getMaximumEscalationLevel() {
		return 2;
	}
}
