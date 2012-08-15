package jadex.micro;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.MultiCollection;
import jadex.kernelbase.CacheableKernelModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class MicroModel extends CacheableKernelModel
{
	/** The agent injection targets. */
	protected List agentinjections;

	/** The argument injection targets. */
	protected Map argumentinjections;

	/** The result injection targets. */
	protected Map<String, Tuple3<Field, String, String>> resultinjections;
	
	/** The service injection targets. */
	protected Map serviceinjections;
	
	/** The class loader. */
	protected ClassLoader classloader;

	
	/**
	 *  Create a new model.
	 */
	public MicroModel(IModelInfo modelinfo)
	{
		super(modelinfo);
	}

	/**
	 *  Add an injection field.
	 *  @param field The field. 
	 */
	public void addAgentInjection(Field field)
	{
		if(agentinjections==null)
			agentinjections = new ArrayList();
		agentinjections.add(field);
	}
	
	/**
	 *  Get the agent injection fields.
	 *  @return The fields.
	 */
	public Field[] getAgentInjections()
	{
		return agentinjections==null? new Field[0]: (Field[])agentinjections.toArray(new Field[agentinjections.size()]);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addArgumentInjection(String name, Field field, String convert)
	{
		if(argumentinjections==null)
			argumentinjections = new MultiCollection();
		argumentinjections.put(name, new Tuple2<Field, String>(field, convert!=null && convert.length()==0? null: convert));
	}
	
	/**
	 *  Get the argument injection fields.
	 *  @return The fields.
	 */
	public Tuple2<Field, String>[] getArgumentInjections(String name)
	{
		Collection col = argumentinjections==null? null: (Collection)argumentinjections.get(name);
		return col==null? new Tuple2[0]: (Tuple2<Field, String>[])col.toArray(new Tuple2[col.size()]);
	}
	
	/**
	 *  Get the argument injection names.
	 *  @return The names.
	 */
	public String[] getArgumentInjectionNames()
	{
		return argumentinjections==null? SUtil.EMPTY_STRING_ARRAY: 
			(String[])argumentinjections.keySet().toArray(new String[argumentinjections.size()]);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addResultInjection(String name, Field field, String convert, String convback)
	{
		if(resultinjections==null)
			resultinjections = new HashMap<String, Tuple3<Field, String, String>>();
		resultinjections.put(name, new Tuple3<Field, String, String>(field, 
			convert!=null && convert.length()==0? null: convert,
			convback!=null && convback.length()==0? null: convback));
	}
	
	/**
	 *  Get the result injection field.
	 *  @return The fields.
	 */
	public Tuple3<Field, String, String> getResultInjection(String name)
	{
		return resultinjections==null? null: (Tuple3<Field, String, String>)resultinjections.get(name);
	}
	
	/**
	 *  Get the Result injection names.
	 *  @return The names.
	 */
	public String[] getResultInjectionNames()
	{
		return resultinjections==null? SUtil.EMPTY_STRING_ARRAY: 
			(String[])resultinjections.keySet().toArray(new String[resultinjections.size()]);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addServiceInjection(String name, Field field)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection();
		serviceinjections.put(name, field);
	}
	
	/**
	 *  Get the service injection fields.
	 *  @return The fields.
	 */
	public Field[] getServiceInjections(String name)
	{
		Collection col = serviceinjections==null? null: (Collection)serviceinjections.get(name);
		return col==null? new Field[0]: (Field[])col.toArray(new Field[col.size()]);
	}
	
	/**
	 *  Get the service injection names.
	 *  @return The names.
	 */
	public String[] getServiceInjectionNames()
	{
		return serviceinjections==null? SUtil.EMPTY_STRING_ARRAY: 
			(String[])serviceinjections.keySet().toArray(new String[serviceinjections.size()]);
	}

	/**
	 *  Get the classloader.
	 *  @return the classloader.
	 */
	public ClassLoader getClassloader()
	{
		return classloader;
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassloader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}
}
