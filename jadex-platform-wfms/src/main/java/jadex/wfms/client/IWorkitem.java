package jadex.wfms.client;

import java.util.Map;
import java.util.Set;

/**
 * The workitem interface.
 *
 */
public interface IWorkitem
{
	public static final int GENERIC_WORKITEM_TYPE = 0;
	public static final int TEXT_INFO_WORKITEM_TYPE = 1;
	public static final int DATA_FETCH_WORKITEM_TYPE = 2;
	
	/**
	 * Gets the name of the workitem.
	 * 
	 * @return name of the workitem
	 */
	public String getName();
	
	/**
	 * Gets the type of the workitem.
	 * 
	 * @return type of the workitem.
	 */
	public int getType();
	
	/**
	 * Gets the role responsible for handling this workitem.
	 * 
	 * @return role responsible for handling this workitem.
	 */
	public String getRole();
	
	/**
	 * Gets the parameter names.
	 * 
	 * @return parameter names
	 */
	public Set getParameterNames();
	
	/**
	 * Gets the value of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @return value of the parameter, or null if no value is set.
	 */
	public Object getParameterValue(String parameterName);
	
	/**
	 * Gets the type of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @return type of the parameter
	 */
	public Class getParameterType(String parameterName);
	
	/**
	 * Returns the GUI-properties of a parameter
	 * @param parameterName name of the parameter
	 * @return the GUI-properties
	 */
	public Map getParameterGuiProperties(String parameterName);
	
	/**
	 * Returns whether a parameter is read-only.
	 * 
	 * @param parameterName name of the parameter
	 * @return true if the parameter is read-only
	 */
	public boolean isReadOnly(String parameterName);
}
