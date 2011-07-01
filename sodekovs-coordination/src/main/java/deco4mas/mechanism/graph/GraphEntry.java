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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphEntry other = (GraphEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		return true;
	}

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
		return "GraphEntry [id=" + id + ", inputs=" + inputs + ", outputs=" + outputs + "]";
	}
}