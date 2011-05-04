/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.domain.HelpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Das Ziel dieser Strategie ist es, zu vermeiden dass durch die Rekonfiguration Engpaesse im System entstehen. Daher werden bei dieser Strategie immer nur so viele Rollen uebernommen, wie auch
 * abgegeben werden koennen. aehnlich wie die bereits vorgestellte, einfache Strategie besteht auch diese Strategie aus zwei Runden (Eskalations--Leveln.)<br>
 * - In der ersten Runde nimmt der Empfaenger des HelpRequest so viele der defekten Rollen auf, wie er auch eigene, die vom Absender uebernommen werden koennen, abgegeben kann. Dabei veraendert sich
 * die Anzahl der Rollen die Empfaenger und Absender nach der Rekonfiguration ausfuehren nicht.<br>
 * - In der zweiten Runde werden vom Empfaenger so lange defekte Rollen aus uebernommen, wie er eigene Rollen an den Absender abgegeben kann. Dabei prueft er nicht ob diese vom Absender ausgefuehrt
 * werden koennen, sondern nur ob fuer die Rolle keine Capability benoetigt wird, die in einer der defekten Rollen im HelpRequest vor kommt. Auch in dieser Runde veraendert sich die Anzahl der Rollen
 * die vom Empfaenger und Absender ausgefuehrt werden nicht.<br>
 * <br>
 * Der Vorteil dieser Strategie, im Gegensatz zur {@link GreedyStrategy} ist, dass durch die Rekonfiguration keine Engpaesse entstehen koennen, da die Anzahl der Rollen die ein Agent ausfuehrt vor und
 * nach der Rekonfiguration gleich bleibt. Im Gegenzug benoetigt diese Strategie aber mehr Nachrichten und Tauschoperationen zur Rekonfiguration. Ausserdem gibt es Faelle in denen von der
 * GreedyStrategy eine Loesung zur Rekonfiguration des Systems gefunden wird, bei denen diese Strategie keine Loesung findet, weil sie es nicht erlaubt, dass Agenten Rollen uebernehmen ohne dafuer
 * eigene Rollen abzugeben. So werden zwar Engpaesse vermieden, aber in den Faellen in denen das System nur dann laufend gehalten werden kann, wenn es so um rekonfiguriert wird, dass ein Engpass
 * entsteht, findet diese Strategie keine Loesung.
 * 
 * @author thomas
 */
public class OptimisticStrategy extends GenericStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see haw.mmlab.production_line.adaptivity.strategies.IStrategy#evaluate(haw .mmlab.production_line.domain.HelpRequest, haw.mmlab.production_line.adaptivity.strategies.AgentData)
	 */
	public EvaluationResult evaluate(HelpRequest request, AgentData data) {
		String[] capabilities = data.getCapabilities();
		List<Role> roles = data.getRoles();

		EvaluationResult result = new EvaluationResult();

		// escalation level 0
		if (request.getEscalationLevel() == HelpRequest.MIN_ESCALATION_LEVEL) {
			List<Role> inRoles = getMatchingRoles(request.getDeficientRoles(), capabilities);
			List<Role> outRoles = getMatchingRoles(roles, request.getCapabilities());

			// if there are any deficient roles you can apply and the deficient
			// agent can apply any of your roles
			if (!inRoles.isEmpty() && !outRoles.isEmpty()) {
				List<Role> takeRoles = new ArrayList<Role>();
				List<Role> giveAwayRoles = new ArrayList<Role>();

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
		else {
			List<Role> inRoles = getMatchingRoles(request.getDeficientRoles(), capabilities);
			List<Role> ownRoles = new ArrayList<Role>(data.getRoles());

			List<Capability> deficientCaps = getDeficientCaps(request.getDeficientRoles());

			// if there are any deficient roles you can apply
			if (!inRoles.isEmpty() && !ownRoles.isEmpty()) {
				List<Role> takeRoles = new ArrayList<Role>();
				List<Role> giveAwayRoles = new ArrayList<Role>();

				// then the deficient roles, as long as to can
				// give away one of your own roles (and the capabiliy of that
				// role is not deficient in the requesting agent)
				while (!inRoles.isEmpty() && !ownRoles.isEmpty()) {
					if (deficientCaps.contains(ownRoles.get(0).getCapability())) {
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

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seehaw.mmlab.production_line.adaptivity.strategies.IStrategy# getMaximumEscalationLevel()
	 */
	public int getMaximumEscalationLevel() {
		return 1;
	}
}