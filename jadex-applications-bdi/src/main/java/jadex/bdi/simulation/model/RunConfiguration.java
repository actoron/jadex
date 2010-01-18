package jadex.bdi.simulation.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="RunConfiguration")
public class RunConfiguration {
	
	private General general;
	private Rows rows;
	
	@XmlElement(name="General")
	public General getGeneral() {
		return general;
	}
	
	public void setGeneral(General general) {
		this.general = general;
	}
	
	@XmlElement(name="Rows")
	public Rows getRows() {
		return rows;
	}
	
	public void setRows(Rows rows) {
		this.rows = rows;
	}

}
