package sodekovs.util.model.benchmarking.description;

import java.io.Serializable;

/**
 * An benchmark description.
 */
public class HistoricDataDescription implements IHistoricDataDescription, Serializable {
	// -------- attributes ----------


	/** Attribute for slot name. */
	protected String name;

	/** Attribute for slot type. */
	protected String type;
	
	/** Attribute for slot timestamp. */
	protected String timestamp;
	
	/** Attribute for slot LogEntries. */
	protected String logEntries;

	/** Attribute for slot LogAsPNG. */
	protected String logAsPNG;
	
	/** Attribute for slot gnuPlotMainFileContent. */
	protected String gnuPlotMainFileContent;
	
	
	
	// -------- constructor --------

//	/**
//	 * Create a new historic data description.
//	 */
//	public HistoricDataDescription() {
//		this(null);
//	}


	/**
	 * Create a new w historic data description.
	 * 
	 * @param IComponentIdentifier
	 *            id.
	 * @param name
	 *            The name.
	 * @param services
	 *            The type.
	 */
	public HistoricDataDescription(String name, String type, String timestamp, String logEntries, String logAsPNG, String gnuPlotMainFileContent ) {		
		this.name = name;
		this.type = type;
		this.timestamp = timestamp;
		this.logEntries = logEntries;
		this.logAsPNG = logAsPNG;
		this.gnuPlotMainFileContent = gnuPlotMainFileContent;
	}

	// -------- accessor methods --------
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getLogEntries() {
		return logEntries;
	}

	public void setLogEntries(String logEntries) {
		this.logEntries = logEntries;
	}

	public String getLogAsPNG() {
		return logAsPNG;
	}

	public void setLogAsPNG(String logAsPNG) {
		this.logAsPNG = logAsPNG;
	}
	

	public String getGnuPlotMainFileContent() {
		return gnuPlotMainFileContent;
	}

	public void setGnuPlotMainFileContent(String gnuPlotMainFileContent) {
		this.gnuPlotMainFileContent = gnuPlotMainFileContent;
	}
	
	/**
	 * Clone a historic data description.
	 */
	public Object clone() {
		try {
			HistoricDataDescription ret = (HistoricDataDescription) super.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cannot clone: " + this);
		}
	}
	
	/**
	 * Get a string representation of this historic data description.
	 * 
	 * @return The string representation.
	 */
	public String toString() {
		return "HistoricDescription: " + getName() + " - " + getType() + " - " + getTimestamp() + " - " + getLogEntries() + " - " + getLogAsPNG() + " - " + getGnuPlotMainFileContent();
	}
}
