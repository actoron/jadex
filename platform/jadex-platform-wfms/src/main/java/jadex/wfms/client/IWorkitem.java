package jadex.wfms.client;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;

import java.util.Map;
import java.util.Set;

/**
 * The workitem interface.
 *
 */
public interface IWorkitem
{
	/**
	 *  Gets the identifier of the process which issued the workitem
	 *  @return Identifier of the process which issued the workitem.
	 */
	public IComponentIdentifier getProcess();
	
	/**
	 *  Gets the identifier of the work item handler managing the workitem
	 *  @return Identifier of the handler managing the workitem.
	 */
	public IServiceIdentifier getHandler();
	
	/**
	 *  Gets the creation time of the process which issued the workitem
	 *  @return creation time of the process which issued the workitem.
	 */
	public long getProcessCreationTime();
	
	/**
	 * Gets the name of the workitem.
	 * 
	 * @return name of the workitem
	 */
	public String getName();
	
	/**
	 *  Returns the ID of the Workitem.
	 *  @return The ID.
	 */
	public String getId();
	
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
	 * Returns the Meta-properties of the workitem
	 * @return the Meta-properties, never null
	 */
	public Map getMetaProperties();
	
	/**
	 * Returns the Meta-properties of a parameter
	 * @param parameterName name of the parameter
	 * @return the Meta-properties, never null
	 */
	public Map getParameterMetaProperties(String parameterName);
	
	/**
	 * Returns whether a parameter is read-only.
	 * 
	 * @param parameterName name of the parameter
	 * @return true if the parameter is read-only
	 */
	public boolean isReadOnly(String parameterName);
}
