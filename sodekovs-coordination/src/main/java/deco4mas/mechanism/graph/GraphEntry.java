/**
 * 
 */
package deco4mas.mechanism.graph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The entries in an {@link IOGraph}. An entry consists of an id and a {@link List} of input and output entries.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "entry")
public class GraphEntry {

	private String id = null;

	private List<String> inputs = new ArrayList<String>();

	private List<String> outputs = new ArrayList<String>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@XmlAttribute(name = "id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the inputs
	 */
	@XmlElementWrapper(name = "inputs")
	@XmlElement(name = "input")
	public List<String> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs
	 *            the inputs to set
	 */
	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	@XmlElementWrapper(name = "outputs")
	@XmlElement(name = "output")
	public List<String> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs
	 *            the outputs to set
	 */
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "GraphEntry [" + (id != null ? "id=" + id + ", " : "") + (inputs != null ? "inputs=" + inputs.subList(0, Math.min(inputs.size(), maxLen)) + ", " : "")
				+ (outputs != null ? "outputs=" + outputs.subList(0, Math.min(outputs.size(), maxLen)) : "") + "]";
	}
}