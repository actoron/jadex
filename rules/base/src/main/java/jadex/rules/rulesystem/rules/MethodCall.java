package jadex.rules.rulesystem.rules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jadex.rules.state.OAVJavaType;

/**
 *  Definition of a Java method call.
 */
public class MethodCall
{
	//-------- attributes --------
	
	/** The java object type. */
	protected OAVJavaType type;
	
	/** The method. */
	protected Method method;
	
	/** The sources of the parameter values. */
	protected List paramsources;
	
	/** The variables, from which the method call depends. */
	protected List depvars;

	//-------- constructors --------
	
	/**
	 *  Create a new Java method call.
	 */
	public MethodCall(OAVJavaType type, Method method)
	{
		this(type, method, new ArrayList());
	}
		
	/**
	 *  Create a new Java method call.
	 */
	public MethodCall(OAVJavaType type, Method method, Object[] paramsources)
	{
		this(type, method, new ArrayList(Arrays.asList(paramsources)));
	}

	/**
	 *  Create a new Java method call.
	 */
	public MethodCall(OAVJavaType type, Method method, List paramsources)
	{
		this.type = type;
		this.method = method;
		this.paramsources = paramsources;
	}
	
	//-------- methods --------

	/**
	 *  Get the method.
	 *  @return The method.
	 */
	public Method getMethod()
	{
		return method;
	}
	
	/**
	 *  Get the java object type.
	 *  @return The type.
	 */
	public OAVJavaType getType()
	{
		return type;
	}
	
	/**
	 *  Add a new parameter source. Can be
	 *  - constant value
	 *  - variable (value)
	 *  - function call (value)
	 */
	public void addParameterSource(Object paramsource)
	{
		this.paramsources.add(paramsource);
	}
	
	/**
	 *  Get the parameter sources.
	 *  @return The param sources.
	 */
	public List getParameterSources()
	{
		return paramsources;
	}

	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		if(depvars==null)
		{
			depvars = new ArrayList();
			for(int i=0; i<paramsources.size(); i++)
			{
				depvars.addAll(Constraint.getVariablesForValueSource(paramsources.get(i)));
			}
		}
		
		return depvars;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer(method.getName());
		ret.append("(");
		for(int i=0; i<paramsources.size(); i++)
		{
			if(i>0)
				ret.append(", ");
			ret.append(paramsources.get(i).toString());
		}
		ret.append(")");
		return ret.toString();
	}
}
