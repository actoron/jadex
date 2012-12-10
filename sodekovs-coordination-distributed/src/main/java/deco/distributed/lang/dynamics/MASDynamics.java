package deco.distributed.lang.dynamics;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import deco.distributed.lang.dynamics.causalities.DecentralMechanismLink;
import deco.distributed.lang.dynamics.causalities.DirectLink;
import deco.distributed.lang.dynamics.convergence.Convergence;
import deco.distributed.lang.dynamics.defines.AgentModel;
import deco.distributed.lang.dynamics.defines.AgentModel.AgentModelType;
import deco.distributed.lang.dynamics.defines.Define;
import deco.distributed.lang.dynamics.defines.MechanismModel;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.mechanism.DecentralizedCausality;
import deco.distributed.lang.dynamics.mechanism.MechanismConfiguration;
import deco.distributed.lang.dynamics.mechanism.MechanismParameterMapping;
import deco.distributed.lang.dynamics.properties.AgentReference;
import deco.distributed.lang.dynamics.properties.ElementReference;
import deco.distributed.lang.dynamics.properties.EnvrionmentProperty;
import deco.distributed.lang.dynamics.properties.Group;
import deco.distributed.lang.dynamics.properties.GroupMembership;
import deco.distributed.lang.dynamics.properties.RoleOccupation;
import deco.distributed.lang.dynamics.properties.SystemProperty;
import deco4mas.distributed.annotation.agent.CoordinationAnnotation.CoordinationType;
import deco4mas.distributed.annotation.agent.ParameterMapping;

/**
 * This class represents the dynamics of a Multi-Agent System (MASDynamics language).<br>
 * <br>
 * This language describes analysis pattern of decentralized agent coordination in terms of structures of agent-behavior interdependencies.<br>
 * <br>
 * The system behavior is described by a set of system variables (Properties) that denote the numbers of agents that exhibit specific behaviors. These variables are linked by direct (i.e. based on
 * "inter-agent communication") and decentralized (i.e. based on "decentralized coordination mechanisms") interdependencies. Both types of interdependencies can be configured to allow their automated
 * enforcement (cf. dec4mas.*).
 * <br>
 * The interdependencies can be configured and activated or deactivated autonomously by the participating agents using according to the specified possible adaptions in the convergence part of the
 * language.
 * 
 * @author Jan Sudeikat & Thomas Preisler
 * 
 */
@XmlRootElement(name = "MASDynamic")
public class MASDynamics {

	// ----------attributes----------

	/** The name of the model. */
	private String name;

	/** The define statements. */
	Define define;

	/** The system properties (nodes). */
	private Properties properties = new Properties();

	/** The causal relations between system properties (edges). */
	private Causalities causalities = new Causalities();
	
	/** The convergence properties. */
	private Convergence convergence = new Convergence();

	// ----------methods-------------

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "defines")
	public Define getDefine() {
		return define;
	}

	public void setDefine(Define define) {
		this.define = define;
	}

	@XmlElement(name = "causalities")
	public Causalities getCausalities() {
		return causalities;
	}

	private void addGroupMembership(GroupMembership gm) {
		this.properties.addGroupMembership(gm);

	}

	private void addGroupProperty(Group g) {
		this.properties.addGroupProperty(g);

	}

	private void addRoleOccupation(RoleOccupation ro) {
		this.properties.addRoleOccupation(ro);

	}

	private void addEnvironmentProperty(EnvrionmentProperty ep) {
		this.properties.addEnvironmentProperty(ep);
	}

	@XmlElement(name = "system_properties")
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	@XmlElement(name = "convergence")
	public Convergence getConvergence() {
		return convergence;
	}
	
	public void setConvergence(Convergence convergence) {
		this.convergence = convergence;
	}

	public void setCausalities(Causalities causalities) {
		this.causalities = causalities;
	}

	public void addDirectLink(DirectLink dl) {
		this.causalities.addDirectLink(dl);
	}

	public void addDecentralmechanismLink(DecentralMechanismLink dml) {
		this.causalities.addDml(dml);
	}

	// ----------main--------------

	/**
	 * Exemplifying the basic functionality of the model and the xml-utilities.
	 */
	public static void main(String[] args) throws Exception {

		// create instance:
		MASDynamics mas_dyn = new MASDynamics();
		mas_dyn.setName("test-model");

		Define d = new Define();
		AgentModel am = new AgentModel("Test-Agent", "path/TestAgent.agent.xml", AgentModelType.JADEX);
		MechanismModel mm = new MechanismModel("Test-Mechanism", "path/mech.conf");
		d.addAgentModel(am);
		d.addMechanismModel(mm);

		mas_dyn.setDefine(d);

		// creating nodes:
		ArrayList<SystemProperty> sys_prop = new ArrayList<SystemProperty>();

		EnvrionmentProperty prop1 = new EnvrionmentProperty();
		prop1.setMultiple(true);
		prop1.setName("environment-element");
		prop1.setValue(0);

		mas_dyn.addEnvironmentProperty(prop1);

		RoleOccupation prop2 = new RoleOccupation();
		AgentReference ar = new AgentReference("agent_1");
		ar.addElement(new ElementReference("element-name", deco.distributed.lang.dynamics.AgentElementType.BDI_GOAL));
		prop2.addAgentReference(ar);
		prop2.addAgentReference(ar);
		prop2.setMultiple(false);
		prop2.setName("example-role");

		mas_dyn.addRoleOccupation(prop2);

		Group g = new Group();
		g.setName("g");
		AgentReference member = new AgentReference();
		member.setAgent_id("agent-id");
		member.addElement(new ElementReference("element-name", deco.distributed.lang.dynamics.AgentElementType.BDI_GOAL));
		g.addAgentElement(member);

		AgentReference member2 = new AgentReference();
		member2.setAgent_id("agent2-id");
		member2.addElement(new ElementReference("element-name", deco.distributed.lang.dynamics.AgentElementType.BDI_GOAL));
		g.addAgentElement(member2);

		mas_dyn.addGroupProperty(g);

		GroupMembership gm = new GroupMembership();
		gm.setName("gm");
		AgentReference member3 = new AgentReference();
		member3.setAgent_id("agent3-id");
		member3.addElement(new ElementReference("element-name", deco.distributed.lang.dynamics.AgentElementType.BDI_GOAL));
		gm.addAgentElement(member3);

		AgentReference member4 = new AgentReference();
		member4.setAgent_id("agent4-id");
		member4.addElement(new ElementReference("element-name", deco.distributed.lang.dynamics.AgentElementType.BDI_GOAL));
		gm.addAgentElement(member4);

		sys_prop.add(gm);
		mas_dyn.addGroupMembership(gm);

		// creating links:

		DirectLink dl = new DirectLink();
		dl.addFrom(prop1);
		dl.addTo(prop2);
		dl.setName("direct-link");
		dl.setRate(1.5);
		dl.setType(CoordinationType.POSITIVE);
		mas_dyn.addDirectLink(dl);

		DecentralMechanismLink dml = new DecentralMechanismLink();
		dml.setName("decentral-mechanism-link");
		dml.setType(CoordinationType.NEGATIVE);
		dml.addFrom(gm);
		dml.addTo(g);

		DecentralizedCausality c = new DecentralizedCausality();
		AgentElement ae = new AgentElement("id", "element_id", deco.distributed.lang.dynamics.AgentElementType.INTERNAL_EVENT.toString());
		ae.addParameterMapping(new ParameterMapping("local_name", "ref", "clazz"));
		c.addFrom_agents(ae);
		c.addTo_agents(new AgentElement("id", "element_id", deco.distributed.lang.dynamics.AgentElementType.INTERNAL_EVENT.toString()));
		c.setId("id");
		MechanismConfiguration mc = new MechanismConfiguration();
		mc.addParameterMapping(new MechanismParameterMapping("from_name", "to_id", "class", "agent_id"));
		mc.setMechanism_id("mechanism_id");
		mc.addProperty("key", "value");
		c.setMechanismConfiguration(mc);

		dml.setRealization(c.getId()); // this is the concrete mechanism configuration (cf. SSCC08 paper);
		mas_dyn.getCausalities().addRealization(c);
		mas_dyn.addDecentralmechanismLink(dml);

		// write XML:
		deco4mas.distributed.util.xml.XmlUtil.saveAsXML(mas_dyn, "example.xml");
		System.out.println("saved...");

		// read from XML:
		MASDynamics mas_dyn2 = (MASDynamics) deco4mas.distributed.util.xml.XmlUtil.retrieveFromXML(MASDynamics.class, "foraging.dynamics.xml");
		System.out.println("Read conf file: " + mas_dyn2);

		// write XML:
		deco4mas.distributed.util.xml.XmlUtil.saveAsXML(mas_dyn2, "example2.xml");
		System.out.println("saved...");

		// generate the XML-Schema (.xsd) that represents the language meta-model.
		deco4mas.distributed.util.xml.XmlUtil.generateSchema(MASDynamics.class);

	}

}