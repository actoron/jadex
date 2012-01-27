package deco4mas.annotation.agent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * This class annotates the coordination<br>
 * of a certain type (subject),<br>
 * in a certain direction (perception / reception),<br>
 * of a certain type (+/-),<br>
 * under a certain condition (boolean).
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement
public class CoordinationAnnotation {

	//----------constants----------
	
	/** Coordination directions. */
	public enum DirectionType {PUBLICATION, PERCEPTION};
	
	/** Coordination type. */
	public enum CoordinationType {POSITIVE, NEGATIVE};

	/** Elements that can be subjects to coordination. */
	public enum ElementType {BELIEF, BELIEFSET, GOAL, PLAN, INTERNAL_EVENT, GENERIC_TASK};
		
	//----------attributes----------

	/** The type (subject) to be coordinated. */
	private String type;
	
	/** The direction of coordination. (publish/perceive)*/
	private String direction;
	
	/** The (optional) condition when the coordination should be triggered. */
	private Condition condition;
	
	/** The type of (BDI) element to be coordinated. */
	private String element_type;
	
	/** The name of the (BDI) element to be coordinated. */
	private String element_name;
		
	/** The optional parameters to be mapped. */
	private ArrayList<ParameterMapping> parameter_mappings;
	
	/** The optional constraints to be mapped. */
	private CoordinationContraint constraint = new CoordinationContraint();
	
	/** The type of coordination (+/-). */
	private CoordinationType coordinationType;
	
	/** The id of the "coordination mechanism" that processes / enacts this coordination annotation. */
	private String mechID;
	
	/** Agent data element -> to be checked by the agent state interpreter; these elements are to be communicated along the event publication data. */
	private ArrayList<DataMapping> data_mappings;
	
	//----------constructors----------
	
	public CoordinationAnnotation() {
		super();
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.condition = new Condition();
		this.data_mappings = new ArrayList<DataMapping>();
	}

	public CoordinationAnnotation(String type, String direction,
			String element_type, String element_name, String condition) {
		super();
		this.type = type;
		this.direction = direction;
		this.element_type = element_type;
		this.element_name = element_name;
		this.condition = new Condition(condition);
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.data_mappings = new ArrayList<DataMapping>();
	}
	
	public CoordinationAnnotation(String type, String direction,
			String element_type, String element_name) {
		super();
		this.type = type;
		this.direction = direction;
		this.element_type = element_type;
		this.element_name = element_name;
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.data_mappings = new ArrayList<DataMapping>();
	}
	
	public CoordinationAnnotation(String type, String direction,
			String element_type, String element_name, String condition, String mapping_local_name, String mapping_ref, String mapping_clazz) {
		super();
		this.type = type;
		this.direction = direction;
		this.element_type = element_type;
		this.element_name = element_name;
		this.condition = new Condition(condition);
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.parameter_mappings.add(new ParameterMapping(mapping_local_name,mapping_ref,mapping_clazz));
		this.data_mappings = new ArrayList<DataMapping>();
	}
	
	public CoordinationAnnotation(String type, String direction,
			String element_type, String element_name, String condition, String mapping_local_name, String mapping_ref) {
		super();
		this.type = type;
		this.direction = direction;
		this.element_type = element_type;
		this.element_name = element_name;
		this.condition = new Condition(condition);
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.parameter_mappings.add(new ParameterMapping(mapping_local_name,mapping_ref));
		this.data_mappings = new ArrayList<DataMapping>();
	}
	
	public CoordinationAnnotation(String type, String direction,
			String element_type, String element_name, String mapping_local_name, String mapping_ref) {
		super();
		this.type = type;
		this.direction = direction;
		this.element_type = element_type;
		this.element_name = element_name;
		this.parameter_mappings = new ArrayList<ParameterMapping>();
		this.parameter_mappings.add(new ParameterMapping(mapping_local_name,mapping_ref));
		this.data_mappings = new ArrayList<DataMapping>();
	}

	public CoordinationAnnotation getXMLConfiguration(String file_name){
		try {
			return (CoordinationAnnotation) deco4mas.util.xml.XmlUtil.retrieveFromXML(CoordinationAnnotation.class, file_name);
		} catch (FileNotFoundException e) {
			System.out.println("Error: can not read specified configuration file....");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("Error: can not extract conf. information from specified file....");
			e.printStackTrace();
		}
		return null;
	}
	
	//----------methods----------
	
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name="direction")
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@XmlElement(name="condition")
	public Condition getCondition() {
		return condition;
	}
	
	@XmlTransient
	public String getConditionName() {
		return condition.getName();
	}
	
	@XmlTransient
	public String getConditionExpression() {
		return condition.getExpression();
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public boolean hasCondition(){
		if (this.condition == null) return false;
		else return true;
	}
	
	public void setConditionName(String name) {
		this.condition.setName(name);
	}
	
	public void setConditionExpression(String expression) {
		this.condition.setExpression(expression);
	}

	@XmlAttribute(name="element_type")
	public String getElement_type() {
		return element_type;
	}

	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}

	@XmlAttribute(name="element_name")
	public String getElement_name() {
		return element_name;
	}

	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	@XmlElementWrapper(name="parametermapping")
	@XmlElement(name="parametermapping")
	public ArrayList<ParameterMapping> getParameter_mappings() {
		return parameter_mappings;
	}

	public void setParameter_mappings(ArrayList<ParameterMapping> parameter_mappings) {
		this.parameter_mappings = parameter_mappings;
	}
	
	public void addParameter_mapping(ParameterMapping pm){
		this.parameter_mappings.add(pm);
	}
	
	public int getParameterMappingCount(){
		return this.parameter_mappings.size();
	}

	@XmlElement(name="constraint")
	public CoordinationContraint getConstraint() {
		return constraint;
	}

	public void setConstraint(CoordinationContraint constraint) {
		this.constraint = constraint;
	}
	
	public boolean containsParameter(String parameter){
		for (ParameterMapping pm : this.parameter_mappings){
			if (pm.getLocalName().equals(parameter)) return true;
		}
		return false; 
	}
	
	public boolean containsReference(String ref){
		for (ParameterMapping pm : this.parameter_mappings){
			if (pm.getRef().equals(ref)) return true;
		}
		return false; 
	}
	
	public ParameterMapping getMappingByLocalName(String name){
		for (ParameterMapping pm : this.parameter_mappings){
			if (pm.getLocalName().equals(name)) return pm;
		}
		return null;
	}

	public CoordinationType getCoordinationType() {
		return coordinationType;
	}

	public void setCoordinationType(CoordinationType coordinationType) {
		this.coordinationType = coordinationType;
	}

	@XmlAttribute(name="mechanims_id")
	public String getMechID() {
		return mechID;
	}

	public void setMechID(String mechID) {
		this.mechID = mechID;
	}

	@XmlElementWrapper(name="agent_data_mappings")
	@XmlElement(name="mapping")
	public ArrayList<DataMapping> getData_mappings() {
		return data_mappings;
	}

	public void setData_mappings(ArrayList<DataMapping> data_mappings) {
		this.data_mappings = data_mappings;
	}

	/**
	 * Add a mapping to the annotation.
	 * 
	 * @param dm
	 */
	public void addDataMapping(DataMapping dm) {
		this.data_mappings.add(dm);
	}
	
}