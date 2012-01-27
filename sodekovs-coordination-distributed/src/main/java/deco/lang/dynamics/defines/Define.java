package deco.lang.dynamics.defines;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Define statement.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="define")
public class Define {

	//----------attributes----------

	/** Agent model definitions. */
	ArrayList<AgentModel> agent_models;
	
	/** Mechanism instance definitions. */
	ArrayList<MechanismModel> mechanism_models;

	//----------constructors--------
	
	public Define(ArrayList<AgentModel> agent_models,
			ArrayList<MechanismModel> mechanism_models) {
		super();
		this.agent_models = agent_models;
		this.mechanism_models = mechanism_models;
	}
	
	public Define() {
		super();
		this.agent_models = new ArrayList<AgentModel>();
		this.mechanism_models = new ArrayList<MechanismModel>();
	}
	
	//----------methods-------------

	@XmlElement(name="agent_model")
	public ArrayList<AgentModel> getAgent_models() {
		return agent_models;
	}

	public void setAgent_models(ArrayList<AgentModel> agent_models) {
		this.agent_models = agent_models;
	}
	
	public void addAgentModel(AgentModel am){
		this.agent_models.add(am);
	}

	@XmlElement(name="mechanism_model")
	public ArrayList<MechanismModel> getMechanism_models() {
		return mechanism_models;
	}

	public void setMechanism_models(ArrayList<MechanismModel> mechanism_models) {
		this.mechanism_models = mechanism_models;
	}
	
	public void addMechanismModel(MechanismModel mm){
		this.mechanism_models.add(mm);
	}
	
}