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
 * Eine einfache Strategie zum Tauschen von Rollen die nur aus zwei Runden (Eskalations--Leveln) besteht:<br>
 * - In der ersten Runde wird versucht so viele defekte Rollen wie moeglich durch Tauschen zu ersetzen. Zunaechst prueft der Empfaenger ob er defekte Rollen uebernehmen kann und dem Absender des
 * HelpRequest dafuer einige seiner Rollen, die dieser anwenden kann, abgeben kann. Es werden zunaechst also so viele Rollen zum Tauschen abgegeben, wie auch aufgenommen werden. Anschliessend
 * ueberprueft der Empfaenger ob er noch freie Kapazitaeten hat um weitere defekte Rollen zu uebernehmen, auch wenn er dafuer keine eigenen Rollen an den Absender abgeben kann. Um festzustellen ob ein
 * Agent noch freie Kapazitaeten hat, ermittelt dieser zunaechst seine Puffer--Groesse und die Anzahl der freien Plaetze im Puffer. Die Kapazitaet um weitere Rollen aufzunehmen, wird vom kleineren der
 * beiden Werte bestimmt. Wenn ein Agent eine Puffergroesse von vier bei zwei Rollen hat, kann er maximal zwei weitere Rollen aufnehmen. Wenn von den zwei Rollen zum Zeitpunkt der Rekonfiguration aber
 * schon drei Plaetze im Puffer belegt sind, kann der Agent nur eine weitere Rolle aufnehmen. Wichtig bei der Aufnahme von weiteren Rollen ist es, das beachtet werden muss, dass fuer jede Rolle die
 * ein Agent hat, in seinem Puffer fuer diese Rolle ein Platz reserviert ist. Zwar koennen Rollen bei entsprechender Puffer-Groessere mehrere Plaetze im Puffer belegen, aber es muss sichergestellt
 * sein, dass jede Rolle mindestens einen reservierten Platz hat um Werkstuecke im Puffer abzulegen. Dieses dient der Vermeidung von Deadlocks bei Zyklen im Ressourcenflussgraphen (siehe Kapitel in
 * dem die Dynamik des Systems erklaert wird). In der ersten Runde werden mindestens genau so viele Rollen uebernommen, wie abgegeben werden.<br>
 * - In der zweiten Runde uebernimmt der Empfaenger solange defekte Rollen wie er noch freie Kapazitaeten im Puffer hat. Fuer jede weitere defekte Rolle, die er uebernimmt, gibt er eine von seinen
 * Rollen (immer die erste aus der Liste seiner Rollen) an den Absender ab. Dabei wird geprueft ob der Absender die Rolle uebernehmen kann, sondern es wird nur geprueft ob fuer die Rolle keine
 * Capability benoetigt wird, die in einer der defekten Rollen im HelpRequest vor kommt.<br>
 * <br>
 * Der Vorteil dieser einfachen Strategie ist es, dass wenig Runden und wenig Nachrichten gebraucht werden um eine Rekonfiguration durch gieriges Tauschen zu finden. Nachteilig ist, dass es bei dieser
 * Strategie zu Engpaessen kommen kann, wenn wenige Roboter viele Rollen uebernehmen, weil sie noch freie Kapazitaeten haben, ohne dafuer ihrerseits Rollen abzugeben.
 * 
 * @author thomas
 */
public class GreedyStrategy extends GenericStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see haw.mmlab.production_line.adaptivity.strategies.IStrategy#evaluate(haw .mmlab.production_line.domain.HelpRequest, haw.mmlab.production_line.adaptivity.strategies.AgentData)
	 */
	public EvaluationResult evaluate(HelpRequest request, AgentData data) {
		String[] capabilities = data.getCapabilities();
		List<Role> roles = data.getRoles();

		int bufferCapacity = 0;
		int freeSlots = 0;
		int freePlaces = 0;
		Buffer buffer = data.getBuffer();
		if (buffer != null) {
			bufferCapacity = buffer.capacity();
			freeSlots = buffer.getFreeSlots();
			freePlaces = bufferCapacity < freeSlots ? bufferCapacity : freeSlots;
		}

		EvaluationResult result = new EvaluationResult();

		// escalation level 0
		if (request.getEscalationLevel() == HelpRequest.MIN_ESCALATION_LEVEL) {
			List<Role> inRoles = getMatchingRoles(request.getDeficientRoles(), capabilities);
			List<Role> outRoles = getMatchingRoles(roles, request.getCapabilities());

			// if there are any deficient roles you can apply and the deficient
			// agent can apply any of your roles
			if (!inRoles.isEmpty()) {
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

				inRoles.removeAll(takeRoles);
				outRoles.removeAll(giveAwayRoles);

				// now take all roles you can apply, as long as you have the
				// capacity
				int roleCount = roles.size();
				while (!inRoles.isEmpty() && roleCount < freePlaces) {
					takeRoles.add(inRoles.remove(0));
					roleCount++;
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
			if (!inRoles.isEmpty()) {
				List<Role> takeRoles = new ArrayList<Role>();
				List<Role> giveAwayRoles = new ArrayList<Role>();

				// first take all roles you can apply, as long as you have the
				// capacity
				int roleCount = roles.size();
				while (!inRoles.isEmpty() && roleCount < freePlaces) {
					takeRoles.add(inRoles.remove(0));
					roleCount++;
				}
				// then take the rest of the deficient roles, as long as to can
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