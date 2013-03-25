package jadex.simulation.model.result;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SimulationResults")
public class SimulationResult extends IResult{

private ArrayList<RowResult> rowsResults = new ArrayList<RowResult>();
	

	@XmlElementWrapper(name="Rows")
	@XmlElement(name="Row")	
	public ArrayList<RowResult> getRowsResults() {
		return rowsResults;
	}
	
	public void setRowsResults(ArrayList<RowResult> rowResults) {
		this.rowsResults = rowResults;
	}
	
	public void addRowsResults(RowResult rowResults) {
		this.rowsResults.add(rowResults);
	}
	

	
	/**
	 * Returns the duration of the row
	 * @return
	 */
	@XmlAttribute(name="SimulationDuration")
	public long getDuraration(){		
		return getEndtime() - getStarttime();		
	}
		

	@XmlAttribute(name="Name")
	public String getName() {
		return name;
	}
	
	@XmlAttribute(name="Description")
	public String getDescription() {
		return description;
	}
		
	public void setDescription(String value) {
		description = value;
	}
	
}

