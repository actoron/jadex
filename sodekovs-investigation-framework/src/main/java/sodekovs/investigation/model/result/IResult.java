package sodekovs.investigation.model.result;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Abstract class for any result to be persisted.
 * @author vilenica
 *
 */
@XmlRootElement(name = "IResults")
public abstract class IResult {
	
	protected String id = null;
	protected String name = null;
	protected long starttime = -1;
	protected long endtime = -1;
	
	
	
	@XmlElement(name="Starttime")
	public long getStarttime() {
		return starttime;
	}
	
	
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	
	@XmlElement(name="Endtime")
	public long getEndtime() {
		return endtime;
	}
	
	
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
