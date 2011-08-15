package jadex.micro;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.kernelbase.CacheableKernelModel;

/**
 * 
 */
public class MicroModel extends CacheableKernelModel
{
	/** The agent injection targets. */
	protected List agentinjections;

	/** The argument injection targets. */
	protected Map argumentinjections;

	/** The service injection targets. */
	protected Map serviceinjections;

	
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
	public void addArgumentInjection(String name, Field field)
	{
		if(argumentinjections==null)
			argumentinjections = new MultiCollection();
		argumentinjections.put(name, field);
	}
	
	/**
	 *  Get the argument injection fields.
	 *  @return The fields.
	 */
	public Field[] getArgumentInjections(String name)
	{
		Collection col = argumentinjections==null? null: (Collection)argumentinjections.get(name);
		return col==null? new Field[0]: (Field[])col.toArray(new Field[col.size()]);
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
}
