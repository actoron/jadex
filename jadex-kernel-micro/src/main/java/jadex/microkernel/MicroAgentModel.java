package jadex.microkernel;

import java.util.Map;

import jadex.bridge.IArgument;
import jadex.bridge.IJadexModel;
import jadex.bridge.IReport;
import jadex.commons.SUtil;

/**
 *  The agent model contains the OAV agent model in a state and
 *  a type-specific compiled rulebase (matcher functionality).
 */
public class MicroAgentModel implements IJadexModel
{
	//-------- attributes --------

	/** The microagent. */
	protected Class microagent;
	
	/** The filename. */
	protected String filename;
	
	//-------- constructors --------
	
	/**
	 *  Create a model.
	 */
	public MicroAgentModel(Class microagent, String filename)
	{
		this.microagent = microagent;
		this.filename = filename;
	}
	
	//-------- IJadexModel methods --------
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the model type.
	 *  @reeturn The model type (kernel specific).
	 */
	public String getType()
	{
		// todo: 
		return "v2microagent";
	}

	//-------- IJadexModel methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return microagent.toString();
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		// todo: 
		return microagent.toString();
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		// todo: 
		return new IReport()
		{
			public Map getDocuments()
			{
				return null;
			}
			
			public boolean isEmpty()
			{
				return true;
			}
			
			public String toHTMLString()
			{
				return "";
			}
		};
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations()
	{
		// todo
		String[] ret = SUtil.EMPTY_STRING;
		return ret;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{		
		// todo
		return new IArgument[0];
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
//		System.out.println("Filename: "+fn);
		return filename;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the micro agent class.
	 *  @return The class.
	 */
	public Class getMicroAgentClass()
	{
		return microagent;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MicroAgentModel("+microagent+", "+filename+")";
	}
}
