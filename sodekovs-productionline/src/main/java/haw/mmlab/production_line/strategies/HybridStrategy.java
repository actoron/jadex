/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Buffer;
import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.domain.HelpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Das Ziel dieser Strategie ist es die Vorteile der {@link GreedyStrategy} mit denen der {@link OptimisticStrategy} zu verbinden. Die Entstehung von Engpaessen bei der Rekonfiguration soll moeglichst
 * vermieden werden, d.h. diese Strategie versuchst zu erst nur so viele Rollen durch einen direkten Tausch (direkter Tausch bedeutet, dass beide Agenten die jeweils getauschten Rollen ausfuehren
 * koennen) aufzunehmen, wie auch abgegeben werden koennen. Erst wenn dieses nicht moeglich ist, werden in der folgenden Runde, sofern der Agent noch freie Kapazitaeten hat, weitere Rollen aufgenommen
 * ohne dafuer eigene Rollen abzugeben. Insgesamt besteht diese Strategie aus drei Runden (Eskalations-Leveln):<br>
 * - Die erste Runde entspricht der der {@link OptimisticStrategy} der Empfaenger des HelpRequest nimmt so viele der defekten Rollen auf, wie er auch eigene, die vom Absender uebernommen werden
 * koennen, abgegeben kann. Die Strategie versucht also einen Ausfall als erstes durch das direkte Tauschen von Rollen zu loesen, was bedeutet, dass fuer den Tausch eine geringe Anzahl an Nachrichten
 * bzw. Tauschoperationen benoetigt wird und dadurch, dass sich die Anzahl der Rollen, die die Agenten ausfuehren nicht aendert, kommt es zu keinen Engpaessen durch die Rekonfiguration. <br>
 * - In der zweiten Runde uebernimmt der Empfaenger nun solange defekte Rollen auf, wie er noch freie Kapazitaeten hat. Hierbei kann es zwar zu Engpaessen durch die Rekonfiguration kommen, aber die
 * Anzahl der fuer die Rekonfiguration benoetigten Nachrichten bzw. Tauschoperationen wird gering gehalten.<br>
 * - Die dritte Runde entspricht nun der zweiten Runde der {@link OptimisticStrategy}, der Empfaenger uebernimmt so lange defekte Rollen, wie er eigene Rollen an den Absender abgegeben kann. Dabei
 * prueft er nicht ob diese vom Absender ausgefuehrt werden koennen, sondern nur ob fuer die Rolle keine Capability benoetigt wird, die in einer der defekten Rollen im HelpRequest vor kommt.<br>
 * <br>
 * Gegenbeispiel:<br>
 * 
 * <table border="1">
 * <tr>
 * <th>&nbsp;</th>
 * <th>Startkonfiguration</th>
 * <th>Loesung</th>
 * <th>Fehler</th>
 * </tr>
 * <tr>
 * <td>R1 (Puffer 2)</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>2xA</td>
 * <td><s>0xA</s></td>
 * <td>1xC</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>0xD</td>
 * <td>2xD</td>
 * <td>1XD</td>
 * </tr>
 * <tr>
 * <td>R2 (Puffer 3)</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>1xB</td>
 * <td>0xB</td>
 * <td>0xB</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>1xC</td>
 * <td>3xC</td>
 * <td>2xC</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>1xE</td>
 * <td>0xE</td>
 * <td>1xE</td>
 * </tr>
 * <tr>
 * <td>R3 (Puffer 4)</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>0xA</td>
 * <td>2xA</td>
 * <td>2xA</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>4xC</td>
 * <td>2xC</td>
 * <td>2xC</td>
 * </tr>
 * <tr>
 * <td>R4 (Puffer 2)</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>0xB</td>
 * <td>2xB</td>
 * <td>1xB</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>2xD</td>
 * <td>0xD</td>
 * <td>1xD</td>
 * </tr>
 * <tr>
 * <td>R5 (Puffer 3)</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>1xB</td>
 * <td>0xB</td>
 * <td>1xB</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>2xE</td>
 * <td>3xE</td>
 * <td>2xB</td>
 * </tr>
 * </table>
 * Zyklus: R1 sucht fuer C und bekommt E von R2, R1 sucht fuer E und bekommt C von R2 usw.
 * 
 * @author thomas
 */
public class HybridStrategy extends GenericStrategy {

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
		return 2;
	}

}
