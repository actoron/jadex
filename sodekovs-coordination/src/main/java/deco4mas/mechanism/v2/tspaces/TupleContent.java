package deco4mas.mechanism.v2.tspaces;

import java.io.Serializable;
import java.util.HashMap;

import deco4mas.mechanism.CoordinationInformation;

/**
 * The Content object that allows to store coordination information in TSpaces tuples.
 * 
 * @author Jan Sudeikat
 * 
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class TupleContent implements CoordinationInformation, Serializable, Comparable {

	// -------- attributes ----------

	/** The type of subject to be transmitted. */
	private String type;

	/** The name of the subject. */
	private String name;

	/** The value of the subject. */
	private HashMap<String, Object> parameters;

	// -------- constructors --------

	public TupleContent() {
		super();
		this.parameters = new HashMap<String, Object>();
	}

	public TupleContent(String type, String name) {
		super();
		this.type = type;
		this.name = name; // TODO name equals subject...
		this.parameters = new HashMap<String, Object>();
	}

	public TupleContent(CoordinationInformation ci) {
		this.type = ci.getType();
		this.name = ci.getName();
		this.parameters = ci.getValues();
	}

	// -------- methods -------------

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public void addValue(String key, Object value) {
		this.parameters.put(key, value);

	}

	@Override
	public Object getValueByName(String key) {
		return this.parameters.get(key);
	}

	@Override
	public HashMap<String, Object> getValues() {
		return parameters;
	}

	// -------- Comparable interface -------------

	/**
	 * Compare objects by the type attribute.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object other) {
		if ((other == null) || !(other instanceof TupleContent))
			return +1;
		if (type == null)
			return (-1);
		return type.compareTo(((TupleContent) other).getType());
	}

	/**
	 * Compare objects by the type attribute.
	 */
	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof TupleContent))
			return false;
		if (type == null)
			return (((TupleContent) other).getType() == null);
		return type.equals(((TupleContent) other).getType());
	}

}
