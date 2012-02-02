package deco.lang.dynamics.mechanism;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * The condition element.
 * 
 * @author Jan Sudeikat
 *
 */
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
