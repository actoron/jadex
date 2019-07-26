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
	
	/** The class loader. */
	protected ClassLoader classloader;
	
	/** The agent methods for given annotations (if any). */
	protected Map<Class<? extends Annotation>, MethodInfo>	agentmethods;
	
	/** The injection info. */
	protected InjectionInfoHolder ii;
	
	/**
	 *  Create a new model.
	 */
	public MicroModel(IModelInfo modelinfo)
	{
		super(modelinfo);
		this.ii = new InjectionInfoHolder();
	}
	
	/**
	 *  Get the injection info holder.
	 *  @return The injection info.
	 */
	public InjectionInfoHolder getInjectionInfoHolder()
	{
		return ii;
	}

	/**
	 *  Add an injection field.
	 *  @param field The field. 
	 */
	public void addAgentInjection(FieldInfo field)
	{
		ii.addAgentInjection(field);
	}
	
	/**
	 *  Get the agent injection fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getAgentInjections()
	{
		return ii.getAgentInjections();
	}
	
	/**
	 *  Add an injection field.
	 *  @param field The field. 
	 */
	public void addParentInjection(FieldInfo field)
	{
		ii.addParentInjection(field);
	}
	
	/**
	 *  Get the parentinjections fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getParentInjections()
	{
		return ii.getParentInjections();
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addArgumentInjection(String name, FieldInfo field, String convert)
	{
		ii.addArgumentInjection(name, field, convert);
	}
	
	/**
	 *  Get the argument injection fields.
	 *  @return The fields.
	 */
	public Tuple2<FieldInfo, String>[] getArgumentInjections(String name)
	{
		return ii.getArgumentInjections(name);
	}
	
	/**
	 *  Get the argument injection names.
	 *  @return The names.
	 */
	public String[] getArgumentInjectionNames()
	{
		return ii.getArgumentInjectionNames();
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addResultInjection(String name, FieldInfo field, String convert, String convback)
	{
		ii.addResultInjection(name, field, convert, convback);
	}
	
	/**
	 *  Get the result injection field.
	 *  @return The fields.
	 */
	public Tuple3<FieldInfo, String, String> getResultInjection(String name)
	{
		return ii.getResultInjection(name);
	}
	
	/**
	 *  Get the Result injection names.
	 *  @return The names.
	 */
	public String[] getResultInjectionNames()
	{
		return ii.getResultInjectionNames();
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addServiceInjection(String name, FieldInfo field, boolean lazy, boolean query)
	{
		ii.addServiceInjection(name, field, lazy, query);
	}
	
	/**
	 *  Add an injection method.
	 *  @param name The name.
	 *  @param method The method. 
	 */
	public void addServiceInjection(String name, MethodInfo method)
	{
		ii.addServiceInjection(name, method);
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param method The method. 
	 */
	public void addServiceInjection(String name, MethodInfo method, boolean query)
	{
		ii.addServiceInjection(name, method, query);
	}
	
	/**
	 *  Get the service injection fields.
	 *  @return The field or method infos.
	 */
	public ServiceInjectionInfo[] getServiceInjections(String name)
	{
		return ii.getServiceInjections(name);
	}
	
	/**
	 *  Get the service injection names.
	 *  @return The names.
	 */
	public String[] getServiceInjectionNames()
	{
		return ii.getServiceInjectionNames();
	}
	
	/**
	 *  Add an injection field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addFeatureInjection(String name, FieldInfo field)
	{
		ii.addFeatureInjection(name, field);
	}
	
	/**
	 *  Get the feature injection fields.
	 *  @return The fields.
	 */
	public FieldInfo[] getFeatureInjections()
	{
		return ii.getFeatureInjections();
	}
	
	/**
	 *  Add an call field.
	 *  @param name The name.
	 *  @param field The field. 
	 */
	public void addServiceCall(ServiceCallInfo call)
	{
		ii.addServiceCall(call);
	}
	
	/**
	 *  Get the service call fields.
	 *  @return The field or method infos.
	 */
	public List<ServiceCallInfo> getServiceCalls()
	{
		return ii.getServiceCalls();
	}
	
	/**
	 *  Set the service calls.
	 */
	public void setServiceCalls(List<ServiceCallInfo> servicecalls)
	{
		ii.setServiceCalls(servicecalls);
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
