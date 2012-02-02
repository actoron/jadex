package deco4mas.annotation.agent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


/**
 * Condition element.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="condition")
public class Condition {

	//----------attributes----------

	/** The condition name. */
	private String name;

	/** The condition expression. */
	private String expression;

	//----------constructors--------
	
	public Condition(String name, String expression) {
		super();
		this.name = name;
		this.expression = expression;
	}
	
	public Condition(String name) {
		super();
		this.name = name;
		this.expression = "";
	}

	public Condition() {
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