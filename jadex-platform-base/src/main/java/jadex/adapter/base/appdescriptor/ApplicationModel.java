package jadex.adapter.base.appdescriptor;

import java.util.List;
import java.util.Map;

import jadex.bridge.IAgentModel;
import jadex.bridge.IArgument;
import jadex.bridge.IReport;

/**
 * 
 */
public class ApplicationModel implements IAgentModel
{
	//-------- attributes --------
	
	/** The application type. */
	protected ApplicationType apptype;
	
	/** The filename. */
	protected String filename;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public ApplicationModel(ApplicationType apptype, String filename)
	{
		this.apptype = apptype;
		this.filename = filename;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return apptype.getName();
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		// todo
		return "n/a";
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		// todo
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
		List apps = apptype.getApplications();
		String[] ret = new String[apps.size()];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = ((Application)apps.get(i)).getName();
		}
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
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the model type.
	 *  @return The model type (kernel specific).
	 */
	public String getType()
	{
		// todo:
		return "application";
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}
}
