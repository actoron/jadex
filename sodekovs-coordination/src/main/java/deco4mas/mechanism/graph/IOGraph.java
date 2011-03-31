/**
 * 
 */
package deco4mas.mechanism.graph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple representation of an input output graph.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name = "iograph")
public class IOGraph {

	private List<GraphEntry> entries = new ArrayList<GraphEntry>();

	/**
	 * @return the entries
	 */
	@XmlElementWrapper(name = "entries")
	@XmlElement(name = "entry")
	public List<GraphEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(List<GraphEntry> entries) {
		this.entries = entries;
	}

	public GraphEntry lookupEntry(String id) {
		for (GraphEntry entry : entries) {
			if (entry.getId().equals(id)) {
				return entry;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "IOGraph [" + (entries != null ? "entries=" + entries.subList(0, Math.min(entries.size(), maxLen)) : "") + "]";
	}
}