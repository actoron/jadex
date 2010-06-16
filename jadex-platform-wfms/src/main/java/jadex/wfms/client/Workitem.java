package jadex.wfms.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A client workitem.
 *
 */
public class Workitem implements IWorkitem, IClientActivity
{
	/** Name of the workitem */
	private String name;
	
	/** Type of the workitem */
	private int type;
	
	/** Unique ID */
	private String id;
	
	/** Role that is handling this workitem. */
	private String role;
	
	/** Types of the parameters */
	private Map parameterTypes;
	
	/** Values of the parameters */
	private Map parameterValues;
	
	/** Meta properties for parameters and workitem */
	private Map workitemMetaProperties;
	
	/** Read-only parameters */
	private Set readOnlyParameters;
	
	public Workitem()
	{
	}
	
	public Workitem(String name, int type, String role, Map parameterTypes, Map parameterValues, Map guiProperties, Set readOnlyParameters)
	{
		this.id = String.valueOf(System.identityHashCode(this));
		this.name = name;
		this.type = type;
		this.role = role;
		/*this.parameterTypes = new HashMap();
		for (Iterator it = parameterTypes.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			Class clazz = (Class) entry.getValue();
			this.parameterTypes.put(entry.getKey(), clazz.getCanonicalName());
		}*/
		this.parameterTypes = parameterTypes;
		this.parameterValues = parameterValues!=null ? parameterValues:new HashMap();
		this.workitemMetaProperties = guiProperties;
		this.readOnlyParameters = readOnlyParameters;
	}
	
	/**
	 * Gets the name of the workitem.
	 * 
	 * @return name of the workitem
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the type of the workitem.
	 * 
	 * @return type of the workitem.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Gets the role responsible for handling this workitem.
	 * 
	 * @return role responsible for handling this workitem.
	 */
	public String getRole()
	{
		return role;
	}
	
	/**
	 * Gets the parameter names.
	 * 
	 * @return parameter names
	 */
	public Set getParameterNames()
	{
		return parameterTypes.keySet();
	}
	
	/**
	 * Gets the value of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @return value of the parameter, or null if no value is set.
	 */
	public Object getParameterValue(String parameterName)
	{
		return parameterValues.get(parameterName);
	}
	
	/**
	 * Gets the parameter values.
	 * 
	 * @return values of the parameters.
	 */
	public Map getParameterValues()
	{
		return parameterValues;
	}
	
	/**
	 * Returns the Meta-properties of the workitem
	 * @return the Meta-properties
	 */
	public Map getMetaProperties()
	{
		return getParameterMetaProperties(null);
	}
	
	/**
	 * Returns the Meta-properties of a parameter
	 * @param parameterName name of the parameter
	 * @return the Meta-properties
	 */
	public Map getParameterMetaProperties(String parameterName)
	{
		Map propertyMap = (Map) workitemMetaProperties.get(parameterName);
		if (propertyMap == null)
			propertyMap = Collections.EMPTY_MAP;
		return propertyMap;
	}
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @param value new value of the parameter
	 * @throws IllegalArgumentException if the parameter is read-only
	 */
	public void setParameterValue(String parameterName, Object value)
	{
		if (readOnlyParameters.contains(parameterName))
			throw new IllegalArgumentException("Parameter is read-only: " + parameterName);
		parameterValues.put(parameterName, value);
	}
	
	/**
	 * Sets the value of multiple parameters.
	 * 
	 * @param parameters the parameters
	 * @throws IllegalArgumentException if the parameter is read-only
	 */
	public void setMultipleParameterValues(Map parameters)
	{
		if ((new HashSet(readOnlyParameters)).removeAll(parameters.keySet()))
			throw new IllegalArgumentException("Some parameter are read-only.");
		parameterValues.putAll(parameters);
	}
	
	/**
	 * Gets the type of a parameter.
	 * 
	 * @param parameterName name of the parameter
	 * @return type of the parameter
	 */
	public Class getParameterType(String parameterName)
	{
		return (Class) parameterTypes.get(parameterName);
	}
	
	/**
	 * Returns whether a parameter is read-only.
	 * 
	 * @param parameterName name of the parameter
	 * @return true if the parameter is read-only
	 */
	public boolean isReadOnly(String parameterName)
	{
		return readOnlyParameters.contains(parameterName);
	}
	
	// ====================== BEAN ===============================
	
	public Map getParameterTypes() {
		return parameterTypes;
	}
	
	public Set getReadOnlyParameters() {
		return readOnlyParameters;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getId() {
		return id;
	}
	
	public void setParameterTypes(Map parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	public void setParameterValues(Map parameterValues)
	{
		this.parameterValues = parameterValues;
	}
	
	public void setReadOnlyParameters(Set readOnlyParameters) {
		this.readOnlyParameters = readOnlyParameters;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String toString()
	{
		return id.concat(" [").concat(role).concat("]");
	}
	
	// --------------------------- Comparison -------------------------
	
	/**
	 *  Get the workitemMetaProperties.
	 *  @return The workitemMetaProperties.
	 */
	public Map getWorkitemMetaProperties()
	{
		return workitemMetaProperties;
	}

	/**
	 *  Set the workitemMetaProperties.
	 *  @param workitemMetaProperties The workitemMetaProperties to set.
	 */
	public void setWorkitemMetaProperties(Map workitemMetaProperties)
	{
		this.workitemMetaProperties = workitemMetaProperties;
	}

	public int hashCode()
	{
		/*if (id == null)
			return 0;*/
		return id.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof Workitem)
			return (id.equals(((Workitem) obj).getId()));
		
		return false;
	}
}
