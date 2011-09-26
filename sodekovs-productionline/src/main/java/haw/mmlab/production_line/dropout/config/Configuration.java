/**
 * 
 */
package haw.mmlab.production_line.dropout.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Contains a special configuration, instructing the DropOut agent what to do.
 * 
 * @author Peter
 * 
 */
public class Configuration {
	private Integer count;
	private AgentQuery query;
	private Action action;

	@XmlAttribute(name = "count")
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@XmlElement(name = "query")
	public AgentQuery getQuery() {
		return query;
	}

	public void setQuery(AgentQuery query) {
		this.query = query;
	}

	@XmlElement(name = "action")
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
