package sodekovs.util.model.benchmarking.description;

import jadex.bridge.IComponentIdentifier;

/**
 *  Interface for historic data descriptions.
 */
public interface IHistoricDataDescription
{
	/**
	 *  Get the name of the historic data.
	 *  @return name The name.
	 */
	public String getName();
	
	/**
	 *  Get the type of this historic data.
	 *  @return type The type.
	 */
	public String getType();
	
	/**
	 *  Get the timestamp of this historic data.
	 *  @return type The type.
	 */
	public String getTimestamp();
	
	/**
	 *  Get the log entries of this historic data.
	 *  @return type The type.
	 */
	public String getLogEntries();
	
	/**
	 *  Get the path of the file that contains a visualization of the log entries of this historic data.
	 *  @return type The type.
	 */
	public String getLogAsPNG();
	
	/**
	 *  Get the content of the main file responsible for creating the png of the history.
	 *  @return type The type.
	 */
	public String getGnuPlotMainFileContent();
	
}
