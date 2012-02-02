package deco.distributed.lang.dynamics;

/**
 * The available agent-element types:
 * <ul>
 * <li>BDI_PLAN : BDI agent plan is (re-)started.</li>
 * <li>BDI_BELIEF : BDI agent belief value is modified.</li>
 * <li>BDI_BELIEFSET : BDI agent belief-set value(s) are modified.</li>
 * <li>BDI_GOAL : BDI agent goal is (re-)tried.</li>
 * <li>MICRO_STEP : Micro agent component step is started.</li>
 * <li>INTERNAL_EVENT : Agent-internal event occurred.</li>
 * <li>GENERIC_ACTIVITY : A generic agent activity.</li>
 * </ul>
 * 
 * @author Jan Sudeikat & Ante Vilenica & Thomas Preisler
 * 
 */
public enum AgentElementType {

	BDI_PLAN, BDI_BELIEF, BDI_BELIEFSET, BDI_GOAL, MICRO_STEP, INTERNAL_EVENT, GENERIC_ACTIVITY, AGENT_ELEMENT_TYPE;

}
