package jadex.micro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.ServiceCallInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.MultiCollection;
import jadex.micro.MicroModel.ServiceInjectionInfo;

/**
 * 
 */
public class InjectionInfoHolder
{
	/** The agent injection targets. */
	protected List<FieldInfo> agentinjections;

	/** The parent injection targets. */
	protected List<FieldInfo> parentinjections;

	/** The argument injection targets. */
	protected MultiCollection<String, Tuple2<FieldInfo, String>> argumentinjections;

	/** The result injection targets. */
	protected Map<String, Tuple3<FieldInfo, String, String>> resultinjections;
	
	/** The service injection targets. */
	protected MultiCollection<String, ServiceInjectionInfo> serviceinjections;
	
	/** The feature injection targets. */
	protected List<FieldInfo> featureinjections;
	
	/** The service value calls. */
	protected List<ServiceCallInfo> servicecalls;
	
	///** The required services map. */
	//protected Map<String, RequiredServiceInfo> requiredserviceinfos;
	
	/**
	 *  Add an injection field.
	 *  @param field The field. 
	 */
	public void addAgentInjection(FieldInfo field)
	{
		if(agentinjections==null)
			agentinjections = new ArrayList<FieldInfo>();
		agentinjections.add(field);
	}
	
	/**
	 *  Get the agent injection fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getAgentInjections()
	{
		return agentinjections==null? new FieldInfo[0]: (FieldInfo[])agentinjections.toArray(new FieldInfo[agentinjections.size()]);
	}
	
	/**
	 *  Add an injection field.
	 *  @param field The field. 
	 */
	public void addParentInjection(FieldInfo field)
	{
		if(parentinjections==null)
			parentinjections = new ArrayList<FieldInfo>();
		parentinjections.add(field);
	}
	
	/**
	 *  Get the parentinjections fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getParentInjections()
	{
		return parentinjections==null? new FieldInfo[0]: (FieldInfo[])parentinjections.toArray(new FieldInfo[parentinjections.size()]);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addArgumentInjection(String name, FieldInfo field, String convert)
	{
		if(argumentinjections==null)
			argumentinjections = new MultiCollection<String, Tuple2<FieldInfo, String>>();
		argumentinjections.add(name, new Tuple2<FieldInfo, String>(field, convert!=null && convert.length()==0? null: convert));
	}
	
	/**
	 *  Get the argument injection fields.
	 *  @return The fields.
	 */
	public Tuple2<FieldInfo, String>[] getArgumentInjections(String name)
	{
		Collection col = argumentinjections==null? null: (Collection)argumentinjections.get(name);
		return col==null? new Tuple2[0]: (Tuple2<FieldInfo, String>[])col.toArray(new Tuple2[col.size()]);
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
	public void addResultInjection(String name, FieldInfo field, String convert, String convback)
	{
		if(resultinjections==null)
			resultinjections = new HashMap<String, Tuple3<FieldInfo, String, String>>();
		resultinjections.put(name, new Tuple3<FieldInfo, String, String>(field, 
			convert!=null && convert.length()==0? null: convert,
			convback!=null && convback.length()==0? null: convback));
	}
	
	/**
	 *  Get the result injection field.
	 *  @return The fields.
	 */
	public Tuple3<FieldInfo, String, String> getResultInjection(String name)
	{
		return resultinjections==null? null: (Tuple3<FieldInfo, String, String>)resultinjections.get(name);
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
	public void addServiceInjection(String name, ServiceInjectionInfo si)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, si);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 * /
	public void addServiceInjection(String name, FieldInfo field, boolean lazy, boolean query)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(field, lazy, query));
	}*/
	
	/**
	 *  Add an injection method.
	 *  @param name The name.
	 *  @param method The method. 
	 * /
	public void addServiceInjection(String name, MethodInfo method)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(method, false));
	}*/
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param method The method. 
	 * /
	public void addServiceInjection(String name, MethodInfo method, boolean query)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(method, query));
	}*/
	
	/**
	 *  Get the service injection fields.
	 *  @return The field or method infos.
	 */
	public ServiceInjectionInfo[] getServiceInjections(String name)
	{
		Collection<ServiceInjectionInfo> col = serviceinjections==null? null: serviceinjections.get(name);
		return col==null? new ServiceInjectionInfo[0]: (ServiceInjectionInfo[])col.toArray(new ServiceInjectionInfo[col.size()]);
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
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addFeatureInjection(String name, FieldInfo field)
	{
		if(featureinjections==null)
			featureinjections = new ArrayList<FieldInfo>();
		featureinjections.add(field);
	}
	
	/**
	 *  Get the feature injection fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getFeatureInjections()
	{
		return featureinjections==null? new FieldInfo[0]: (FieldInfo[])featureinjections.toArray(new FieldInfo[featureinjections.size()]);
	}
	
	/**
	 *  Add an call field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addServiceCall(ServiceCallInfo call)
	{
		if(servicecalls==null)
			servicecalls = new ArrayList<ServiceCallInfo>();
		servicecalls.add(call);
	}
	
	/**
	 *  Get the service call fields.
	 *  @return The field or method infos.
	 */
	public List<ServiceCallInfo> getServiceCalls()
	{
		return servicecalls;
	}
	
	/**
	 *  Set the service calls.
	 */
	public void setServiceCalls(List<ServiceCallInfo> servicecalls)
	{
		this.servicecalls = servicecalls;
	}

	/**
	 * @return the requiredServiceInfos
	 * /
	public Map<String, RequiredServiceInfo> getRequiredServiceInfos()
	{
		return requiredserviceinfos;
	}*/

	/**
	 * @param requiredServiceInfos the requiredServiceInfos to set
	 * /
	public void setRequiredServiceInfos(Map<String, RequiredServiceInfo> requiredServiceInfos)
	{
		this.requiredserviceinfos = requiredServiceInfos;
	}*/
	
}
