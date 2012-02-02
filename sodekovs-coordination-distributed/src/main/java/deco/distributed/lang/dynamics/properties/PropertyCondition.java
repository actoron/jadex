package deco.lang.dynamics.properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Execution Condition.<br>
 * Agents are considered part of the property, when the condition holds.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlType
@XmlRootElement(name="condition")
public class PropertyCondition {

	//----------attributes----------

	/** The condition name. */
	private String name;

	/** The condition expression. */
	private String expression;

	//----------constructors--------
	
	public PropertyCondition(String name, String expression) {
		super();
		this.name = name;
		this.expression = expression;
	}
	
	public PropertyCondition(String name) {
		super();
		this.name = name;
		this.expression = "";
	}

	public PropertyCondition() {
		super();
		this.name = "";
		this.expression = "";
	}

	//----------methods-------------
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlValue
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
