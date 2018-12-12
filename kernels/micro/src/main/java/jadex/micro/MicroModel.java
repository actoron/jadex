package jadex.micro;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.ServiceCallInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.MultiCollection;
import jadex.kernelbase.CacheableKernelModel;

/**
 *  The micro agent model. 
 */
public class MicroModel extends CacheableKernelModel
{
	/** The micro agent class. */
	protected ClassInfo pojoclass;
	
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
	
	/** The class loader. */
	protected ClassLoader classloader;
	
	/** The agent methods for given annotations (if any). */
	protected Map<Class<? extends Annotation>, MethodInfo>	agentmethods;
	
	/** The service value calls. */
	protected List<ServiceCallInfo> servicecalls;
	
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
	public void addServiceInjection(String name, FieldInfo field, boolean lazy, boolean query)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(field, lazy, query));
	}
	
	/**
	 *  Add an injection method.
	 *  @param name The name.
	 *  @param method The method. 
	 */
	public void addServiceInjection(String name, MethodInfo method)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(method, false));
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param method The method. 
	 */
	public void addServiceInjection(String name, MethodInfo method, boolean query)
	{
		if(serviceinjections==null)
			serviceinjections = new MultiCollection<String, ServiceInjectionInfo>();
		serviceinjections.add(name, new ServiceInjectionInfo(method, query));
	}
	
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
	 *  Get the pojo class.
	 *  @return The pojoclass.
	 */
	public ClassInfo getPojoClass()
	{
		return pojoclass;
	}

	/**
	 *  Set the pojo class.
	 *  @param pojoclass The pojoclass to set
	 */
	public void setPojoClass(ClassInfo pojoclass)
	{
		this.pojoclass = pojoclass;
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

	/**
	 *  Set an agent method.
	 */
	public void setAgentMethod(Class<? extends Annotation> ann, MethodInfo mi)
	{
		if(agentmethods==null)
			agentmethods = new HashMap<Class<? extends Annotation>, MethodInfo>();
		
		if(!agentmethods.containsKey(ann))
		{
			agentmethods.put(ann, mi);
		}
		else
		{
			MethodInfo prev = agentmethods.get(ann);
			if(SUtil.equals(mi.getClassName(), prev.getClassName()))
			{
				throw new RuntimeException("Only one @"+ann.getSimpleName()+" method allowed in "+mi.getClassName());
			}
		}
	}
	
	/**
	 *  Get an agent method.
	 */
	public MethodInfo getAgentMethod(Class<? extends Annotation> ann)
	{
		return agentmethods!=null ? agentmethods.get(ann) : null;
	}
	
	/**
	 *  Struct for injection info.
	 */
	public static class ServiceInjectionInfo
	{
		/** The fieldinfo. */
		protected FieldInfo fieldInfo;
		
		/** The methodinfo. */
		protected MethodInfo methodInfo;
		
		/** The lazy flag. */
		protected boolean lazy;
		
		/** The query flag. */
		protected boolean query;

		/**
		 *  Create a new injection info.
		 */
		public ServiceInjectionInfo(FieldInfo fieldInfo, boolean lazy, boolean query)
		{
			this.fieldInfo = fieldInfo;
			this.lazy = lazy;
			this.query = query;
		}
		
		/**
		 *  Create a new injection info.
		 */
		public ServiceInjectionInfo(MethodInfo methodInfo, boolean query)
		{
			this.methodInfo = methodInfo;
			this.query = query;
		}

		/**
		 *  Get the fieldInfo.
		 *  @return the fieldInfo
		 */
		public FieldInfo getFieldInfo()
		{
			return fieldInfo;
		}

		/**
		 *  Set the fieldInfo.
		 *  @param fieldInfo The fieldInfo to set
		 */
		public void setFieldInfo(FieldInfo fieldInfo)
		{
			this.fieldInfo = fieldInfo;
		}

		/**
		 *  Get the methodInfo.
		 *  @return the methodInfo
		 */
		public MethodInfo getMethodInfo()
		{
			return methodInfo;
		}

		/**
		 *  Set the methodInfo.
		 *  @param methodInfo The methodInfo to set
		 */
		public void setMethodInfo(MethodInfo methodInfo)
		{
			this.methodInfo = methodInfo;
		}

		/**
		 *  Get the lazy.
		 *  @return the lazy
		 */
		public boolean isLazy()
		{
			return lazy;
		}

		/**
		 *  Set the lazy.
		 *  @param lazy The lazy to set
		 */
		public void setLazy(boolean lazy)
		{
			this.lazy = lazy;
		}

		/**
		 *  Get the query.
		 *  @return the query
		 */
		public boolean isQuery()
		{
			return query;
		}

		/**
		 *  Set the query.
		 *  @param query The query to set
		 */
		public void setQuery(boolean query)
		{
			this.query = query;
		}
	}
}
