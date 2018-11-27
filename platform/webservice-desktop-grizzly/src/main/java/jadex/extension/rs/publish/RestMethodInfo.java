package jadex.extension.rs.publish;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import jadex.commons.MethodInfo;
import jadex.commons.Tuple2;

/**
 *  Info struct for building a rest method. 
 */
public class RestMethodInfo
{
	//-------- attributes --------
	
	/** The original method. */
	protected Method method;
		
	/** The signature. */
	protected String signature;
	
	/** The parameter types. */
	protected Class<?>[] parametertypes;
	
	/** The return type. */
	protected Type returntype;

	/** The return type. */
	protected Class<?>[] exceptiontypes;
	

	/** The method name that represents the path. */
	protected String name;
	
	/** The method path. */
	protected String path;
	
	/** The rest method type. */
	protected Class<?> resttype;
	
	/** The consumed media types. */
	protected List<MediaType> consumed;
	
	/** The produced media types. */
	protected List<MediaType> produced;
	
	/** The original service method. */
	protected MethodInfo methodmapper;
	
	/** The parameter mapper info. */
	protected Value parametermapper;
	protected boolean automapping;
	
	/** The result mapper info. */
	protected Value resultmapper;

	
	/** Delegation class. */
	protected Class<?> dclazz;
	
	/** The delegation (prototype) method. */
	protected String dmname;
	
	/** The parameter annotations. */
	protected List<List<Tuple2<String, Map<String, Object>>>> parameterannos;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rest method info.
	 */
	public RestMethodInfo(Method method, 
		String name, String path, Class<?> resttype, List<MediaType> consumed, List<MediaType> produced,
		MethodInfo methodmapper, Value parametermapper, boolean automapping, Value resultmapper, 
		Class<?> dclazz, String dmname)
	{
		if(method==null)
			throw new IllegalArgumentException("Method must not null.");
		this.method = method;
		
		this.name = name;
		this.path = path;
		this.resttype = resttype;
		this.consumed = consumed;
		this.produced = produced;
		this.methodmapper = methodmapper;
		this.parametermapper = parametermapper;
		this.automapping = automapping;
		this.resultmapper = resultmapper;
		
		this.dclazz = dclazz;
		this.dmname = dmname;
		
		getAnnotationInfo();
	}
	
	/**
	 *  Create a new rest method info.
	 */
	public RestMethodInfo(Class<?>[] parametertypes, Type returntype, Class<?>[] exceptiontypes, 
		String name, String path, Class<?> resttype, List<MediaType> consumed, List<MediaType> produced,
		MethodInfo methodmapper, Value parametermapper, boolean automapping, Value resultmapper, 
		Class<?> dclazz, String dmname)
	{
		this.name = name;
		this.parametertypes = parametertypes;
		this.returntype = returntype;
		this.exceptiontypes = exceptiontypes;
		
		this.path = path;
		this.resttype = resttype;
		this.consumed = consumed;
		this.produced = produced;
		this.methodmapper = methodmapper;
		this.parametermapper = parametermapper;
		this.automapping = automapping;
		this.resultmapper = resultmapper;
		
		this.dclazz = dclazz;
		this.dmname = dmname;
	}

	//-------- methods --------
	
//	/**
//	 *  Get the method.
//	 *  @return the method.
//	 */
//	public Method getMethod()
//	{
//		return method;
//	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the parameter types.
	 */
	public Class<?>[] getParameterTypes()
	{
		return parametertypes!=null? parametertypes: method!=null? method.getParameterTypes(): null;
	}

	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypes(Class< ? >[] parametertypes)
	{
		this.parametertypes = parametertypes;
	}
	
	/**
	 *  Get the return type.
	 */
	public Type getReturnType()
	{
		return returntype!=null? returntype: method!=null? method.getGenericReturnType(): null;
	}
	
	/**
	 *  Set the returntype.
	 *  @param returntype The returntype to set.
	 */
	public void setReturnType(Type returntype)
	{
		this.returntype = returntype;
	}
	
	/**
	 *  Get the exception types.
	 */
	public Class<?>[] getExceptionTypes()
	{
		return exceptiontypes!=null? exceptiontypes: method!=null? method.getExceptionTypes(): null;
	}
	
	/**
	 *  Set the exceptiontypes.
	 *  @param exceptiontypes The exceptiontypes to set.
	 */
	public void setExceptionTypes(Class< ? >[] exceptiontypes)
	{
		this.exceptiontypes = exceptiontypes;
	}
	
	
	
	/**
	 *  Get the path.
	 *  @return the path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 *  Set the path.
	 *  @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 *  Get the resttype.
	 *  @return the resttype.
	 */
	public Class<?> getRestType()
	{
		return resttype;
	}

	/**
	 *  Set the resttype.
	 *  @param resttype The resttype to set.
	 */
	public void setRestType(Class< ? > resttype)
	{
		this.resttype = resttype;
	}

	/**
	 *  Get the consumed.
	 *  @return the consumed.
	 */
	public List<MediaType> getConsumed()
	{
		return consumed;
	}

	/**
	 *  Set the consumed.
	 *  @param consumed The consumed to set.
	 */
	public void setConsumed(List<MediaType> consumed)
	{
		this.consumed = consumed;
	}

	/**
	 *  Get the produced.
	 *  @return the produced.
	 */
	public List<MediaType> getProduced()
	{
		return produced;
	}

	/**
	 *  Set the produced.
	 *  @param produced The produced to set.
	 */
	public void setProduced(List<MediaType> produced)
	{
		this.produced = produced;
	}

	/**
	 *  Get the methodmapper.
	 *  @return the methodmapper.
	 */
	public MethodInfo getMethodMapper()
	{
		return methodmapper;
	}

	/**
	 *  Set the methodmapper.
	 *  @param methodmapper The methodmapper to set.
	 */
	public void setMethodMapper(MethodInfo methodmapper)
	{
		this.methodmapper = methodmapper;
	}

	/**
	 *  Get the parametermapper.
	 *  @return the parametermapper.
	 */
	public Value getParameterMapper()
	{
		return parametermapper;
	}

	/**
	 *  Set the parametermapper.
	 *  @param parametermapper The parametermapper to set.
	 */
	public void setParameterMapper(Value parametermapper)
	{
		this.parametermapper = parametermapper;
	}

	/**
	 *  Get the resultmapper.
	 *  @return the resultmapper.
	 */
	public Value getResultMapper()
	{
		return resultmapper;
	}

	/**
	 *  Set the resultmapper.
	 *  @param resultmapper The resultmapper to set.
	 */
	public void setResultMapper(Value resultmapper)
	{
		this.resultmapper = resultmapper;
	}

	
	
	/**
	 *  Get the dclazz.
	 *  @return the dclazz.
	 */
	public Class<?> getDelegateClazz()
	{
		return dclazz;
	}

	/**
	 *  Set the dclazz.
	 *  @param dclazz The dclazz to set.
	 */
	public void setDelegateClazz(Class<?> dclazz)
	{
		this.dclazz = dclazz;
	}

	/**
	 *  Get the dmname.
	 *  @return the dmname.
	 */
	public String getDelegateMethodName()
	{
		return dmname;
	}

	/**
	 *  Set the dmname.
	 *  @param dmname The dmname to set.
	 */
	public void setDelegateMethodName(String dmname)
	{
		this.dmname = dmname;
	}
	
	/**
	 *  Get the method signature.
	 */
	public String getSignature()
	{
		if(signature==null)
			signature = buildSignature(getName(), getParameterTypes());
		return signature;
	}
	
	/**
	 *  Set the signature. 
	 *  @param signature The signature to set.
	 */
	public void setSignature(String signature)
	{
		this.signature = signature;
	}
	
	/**
	 *  Get the automapping.
	 *  @return The automapping.
	 */
	public boolean isAutomapping()
	{
		return automapping;
	}

	/**
	 *  Set the automapping.
	 *  @param automapping The automapping to set.
	 */
	public void setAutomapping(boolean automapping)
	{
		this.automapping = automapping;
	}

	/**
	 *  Get the signature.
	 *  @param name
	 *  @param paramtypes
	 *  @return
	 */
	public static String buildSignature(String name, Class<?>[] paramtypes)
	{
		StringBuilder buf = new StringBuilder();
		buf.append(name).append(" ");
		if(paramtypes!=null)
		{
			for(Class<?> pt: paramtypes)
			{
				buf.append(pt.getName()).append(" ");
			}
		}
		return buf.toString();
	}

	/**
	 *  Get the annotations of a method as list of list of tuples.
	 *  A tuple saves the class name and the parameter values as hashmap.
	 */
	public List<List<Tuple2<String, Map<String, Object>>>> getAnnotationInfo()
	{
		if(method!=null && parameterannos==null)
		{
			parameterannos = new ArrayList<List<Tuple2<String,Map<String,Object>>>>();

			Annotation[][] annos = method.getParameterAnnotations();
			
			for(Annotation[] ans: annos)
			{
				List<Tuple2<String,Map<String,Object>>> list = new ArrayList<Tuple2<String,Map<String,Object>>>();

				for(Annotation an: ans)
				{
					Map<String, Object> vals = new HashMap<String, Object>();
					Class<?> anc = an.annotationType();
					Method[] ms = anc.getDeclaredMethods();
//					System.out.println("an: "+an+" "+ms.length);
					for(Method m: ms)
					{
						try
						{
							Object val = m.invoke(an);
							vals.put(m.getName(), val);
						}
						catch(Exception e)
						{
							
						}
					}
					Tuple2<String, Map<String, Object>> tup = new Tuple2<String, Map<String,Object>>(anc.getName(), vals);
					list.add(tup);
				}
				
				parameterannos.add(list);
			}
		}
		
		return parameterannos;
	}
	
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RestMethodInfo(name=" + name + ", resttype=" + resttype
				+ ", consumed=" + consumed + ", produced=" + produced + ")";
	}
	
}
