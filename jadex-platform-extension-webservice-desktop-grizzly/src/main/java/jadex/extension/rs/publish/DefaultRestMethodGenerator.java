package jadex.extension.rs.publish;

import jadex.bridge.service.IService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.extension.rs.invoke.RSJAXAnnotationHelper;
import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *  The default rest method generator. Analyses
 *  the Jadex service interface and possible the
 *  baseclass (which can be a concrete or abstract class or
 *  an interface).
 */
public class DefaultRestMethodGenerator implements IRestMethodGenerator
{
	/**
	 *  Generate the rest method infos.
	 *  @param service The Jadex service. 
	 *  @param classloader The classloader.
	 *  @param baseclass The (abstract or concrete) baseclass or interface.
	 *  @param mapprops Additional mapping properties.
	 *  @return The method infos.
	 *  @throws Exception
	 */
	public List<RestMethodInfo> generateRestMethodInfos(IService service, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops) throws Exception
	{
		List<RestMethodInfo> ret = new ArrayList<RestMethodInfo>();
		
		boolean gen = mapprops.get(AbstractRestServicePublishService.GENERATE)!=null? 
			((Boolean)mapprops.get(AbstractRestServicePublishService.GENERATE)).booleanValue(): true;
		boolean geninfo = mapprops.get(AbstractRestServicePublishService.GENERATE_INFO)!=null? 
			((Boolean)mapprops.get(AbstractRestServicePublishService.GENERATE_INFO)).booleanValue(): true;
		Class<?> iface = service.getServiceIdentifier().getServiceType().getType(classloader);
		
		MediaType[] formats = AbstractRestServicePublishService.DEFAULT_FORMATS;
		Object tmp = mapprops.get(AbstractRestServicePublishService.FORMATS);
		if(tmp instanceof String[])
		{
			String[] fms = (String[])tmp;
			formats = new MediaType[fms.length];
			for(int i=0; i<fms.length; i++)
			{
				formats[i] = MediaType.valueOf(fms[i]);
			}
		}
		else if(tmp instanceof MediaType[])
		{
			formats = (MediaType[])tmp;
		}

		// Generation can be either
		// a) on basis of original interface
		// b) on basis of rest interface/abstract/normal class
		
		// Determine methods to be generated
		
		// Remember path that are taken
		Set<String> paths = new HashSet<String>();
		
		Set<MethodWrapper> methods = new LinkedHashSet<MethodWrapper>();
			
		if(baseclass!=null)
		{
			// Add all methods if is specific interface
			if(baseclass.isInterface())
			{
				Method[] ims = baseclass.getMethods();
				for(int i=0; i<ims.length; i++)
				{
					addMethodWrapper(new MethodWrapper(ims[i]), methods);
				}
			}
			// Else check for abstract methods (others are user implemented and will not be touched)
			else
			{
				Class<?> clazz = baseclass;
				while(!clazz.equals(Object.class))
				{
					Method[] bms = baseclass.getMethods();
					for(int i=0; i<bms.length; i++)
					{
						if(Modifier.isAbstract(bms[i].getModifiers()))
						{
							addMethodWrapper(new MethodWrapper(bms[i]), methods);
						}
						else if(bms[i].isAnnotationPresent(Path.class))
						{
							String path = "";
							if(bms[i].isAnnotationPresent(Path.class))
								path = ((Path)bms[i].getAnnotation(Path.class)).value();
							addPath(path, paths);
						}
						else if(RSJAXAnnotationHelper.getDeclaredRestType(bms[i])!=null)
						{
							addPath("", paths);
						}
					}
					clazz = clazz.getSuperclass();
				}
			}
			
			// Add additional interface methods of original interface if not already implemented
			if(gen)
			{
				Method[] ims = iface.getMethods();
				for(int i=0; i<ims.length; i++)
				{
					try
					{
						// Add method only if not already present
						baseclass.getMethod(ims[i].getName(), ims[i].getParameterTypes());
					}
					catch(Exception e)
					{
						addMethodWrapper(new MethodWrapper(ims[i]), methods);
					}
				}
			}
		}
		// Add all interface methods
		else
		{
			Method[] ims = iface.getMethods();
			for(int i=0; i<ims.length; i++)
			{
				addMethodWrapper(new MethodWrapper(ims[i]), methods);
			}
		}
		
		for(Iterator<MethodWrapper> it = methods.iterator(); it.hasNext(); )
		{
			MethodWrapper mw = it.next();
			Method method = mw.getMethod();
			List<MediaType> consumed = new ArrayList<MediaType>();
			List<MediaType> produced = new ArrayList<MediaType>();
			
			// Determine rest method type.
			Class<?> resttype = RSJAXAnnotationHelper.getDeclaredRestType(method);
			// User defined method, use as is
			if(resttype!=null)
			{
				if(method.isAnnotationPresent(Consumes.class))
				{
					String[] cons = method.getAnnotation(Consumes.class).value();
					if(cons!=null)
					{
						for(int i=0; i<cons.length; i++)
						{
							consumed.add(MediaType.valueOf(cons[i]));
						}
					}
				}
				
				if(method.isAnnotationPresent(Produces.class))
				{
					String[] prods = method.getAnnotation(Produces.class).value();
					if(prods!=null)
					{
						for(int i=0; i<prods.length; i++)
						{
							produced.add(MediaType.valueOf(prods[i]));
						}
					}
				}
				
				MethodInfo methodmapper = null;
				if(method.isAnnotationPresent(MethodMapper.class))
				{
					MethodMapper mm = (MethodMapper)method.getAnnotation(MethodMapper.class);
					methodmapper = new MethodInfo(mm.value(), mm.parameters());
				}
				
				Value parametermapper = null;
				boolean automapping = false; 
				if(method.isAnnotationPresent(ParametersMapper.class))
				{
					ParametersMapper pm = (ParametersMapper)method.getAnnotation(ParametersMapper.class);
					if(!pm.automapping())
					{
						Class<?> clazz = pm.value().clazz();
						if(clazz!=null && !Object.class.equals(clazz))
						{
							parametermapper = new Value(clazz);
						}
						else
						{
							parametermapper = new Value(pm.value().value());
						}
					}
					automapping = pm.automapping();
				}
				
				Value resultmapper = null;
				if(method.isAnnotationPresent(ResultMapper.class))
				{
					ResultMapper pm = (ResultMapper)method.getAnnotation(ResultMapper.class);
					Class<?> clazz = pm.value().clazz();
					if(clazz!=null && !Object.class.equals(clazz))
						resultmapper = new Value(clazz);
					else
						resultmapper = new Value(pm.value().value());
				}
				
				String path = "";
				if(method.isAnnotationPresent(Path.class))
				{
					path = ((Path)method.getAnnotation(Path.class)).value();
				}
				else
				{
					path = mw.getName();
				}
				
				ret.add(new RestMethodInfo(method, mw.getName(), getPathName(path, paths), resttype, consumed, produced, 
					methodmapper, parametermapper, automapping, resultmapper,
					AbstractRestServicePublishService.class, "invoke"));
			}
			// Guess how method should be restified
			else
			{
				resttype = guessRestType(method);
				
				// Determine how many and which rest methods have to be created for the set of consumed media types.
				if(!GET.class.equals(resttype))
				{
					for(int j=0; j<formats.length; j++)
					{
						consumed.add(formats[j]);
					}
				}
				if(POST.class.equals(resttype))
				{
					consumed.add(MediaType.MULTIPART_FORM_DATA_TYPE);
				}
				if(GET.class.equals(resttype))
				{
					consumed.add(MediaType.TEXT_PLAIN_TYPE);
				}
				
				for(int j=0; j<formats.length; j++)
				{
					produced.add(formats[j]);
				}
				
				// store original method info
				MethodInfo methodmapper = new MethodInfo(method.getName(), method.getParameterTypes());
				
				ret.add(new RestMethodInfo(method, mw.getName(), getPathName(mw.getName(), paths), resttype, consumed, produced,
					methodmapper, null, false, null,
					AbstractRestServicePublishService.class, "invoke"));
			}
		}
		
		if(geninfo)
		{
			List<MediaType> consumed = new ArrayList<MediaType>();
			List<MediaType> produced = new ArrayList<MediaType>();
			produced.add(MediaType.TEXT_HTML_TYPE);
			ret.add(new RestMethodInfo(new Class[0], String.class, new Class[0], "getServiceInfo", getPathName("", paths), GET.class, 
				consumed, produced, null, null, false, null, 
				AbstractRestServicePublishService.class, "getServiceInfo"));
		}
		
//		System.out.println("paths: "+paths);
		
		return ret;
	}
	
	/**
	 *  Guess the http type (GET, POST, PUT, DELETE, ...) of a method.
	 *  @param method The method.
	 *  @return  The rs annotation of the method type to use 
	 */
	public Class<?> guessRestType(Method method)
	{
	    // Retrieve = GET (hasparams && hasret)
	    // Update = POST (hasparams && hasret)
	    // Create = PUT  return is pointer to new resource (hasparams? && hasret)
	    // Delete = DELETE (hasparams? && hasret?)

		Class<?> ret = GET.class;
		
		Class<?> rettype = SReflect.unwrapGenericType(method.getGenericReturnType());
		Class<?>[] paramtypes = method.getParameterTypes();
		
		boolean hasparams = paramtypes.length>0;
		boolean hasret = rettype!=null && !rettype.equals(Void.class) && !rettype.equals(void.class);
		
		// GET or POST if has both
		if(hasret)
		{
			if(hasparams)
			{
				if(hasStringConvertableParameters(method, rettype, paramtypes))
				{
					ret = GET.class;
				}
				else
				{
					ret = POST.class;
				}
			}
		}
		
		// todo: other types?
		
//		System.out.println("rest-type: "+ret.getName()+" "+method.getName()+" "+hasparams+" "+hasret);
		
		return ret;
//		return GET.class;
	}
	
	/**
	 *  A a method wrapper.
	 *  @param mw The method mapper.
	 *  @param methods The set of method wrapper.
	 */
	protected static void addMethodWrapper(MethodWrapper mw, Set<MethodWrapper> methods)
	{
		if(methods.contains(mw))
		{
			String basename = mw.getName();
			for(int j=0; methods.contains(mw); j++)
			{
				mw.setName(basename+j);
			}
		}
		methods.add(mw);
	}
	
	/**
	 *  Get a path name based on a start name.
	 *  @param path The path name.
	 *  @param paths The already taken paths.
	 *  @return The available path.
	 */
	protected static String getPathName(String path, Set<String> paths)
	{
		if(paths.contains(path))
		{
			String basename = path;
			for(int i=0; paths.contains(path); i++)
			{
				path = basename+i;
			}
		}
		addPath(path, paths);
		return path;
	}
	
	/**
	 *  Add a path to the set of paths.
	 *  Add the path plus the path with trailing slash.
	 *  @param path The path.
	 *  @param paths The paths.
	 */
	protected static void addPath(String path, Set<String> paths)
	{
		String p = path;
		if(path.endsWith("/"))
			p = path.substring(0, path.length()-1);
		else
			p = path + "/";
		paths.add(path);
		paths.add(p);
	}
	
	/**
	 *  Test if a method has parameters that are all convertible from string.
	 *  @param method The method.
	 *  @param rettype The return types (possibly unwrapped from future type).
	 *  @param paramtypes The parameter types.
	 *  @return True, if is convertible.
	 */
	public boolean hasStringConvertableParameters(Method method, Class<?> rettype, Class<?>[] paramtypes)
	{
		boolean ret = true;
		
		for(int i=0; i<paramtypes.length && ret; i++)
		{
			ret = isStringConvertableType(paramtypes[i]);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a class is convertible from string.
	 *  Tests if is simple type (string, boolean, int, double, etc.)
	 *  or if it contains a static method 'fromString' or 'valueOf'
	 *  for Jersey.
	 */
	public static boolean isStringConvertableType(Class<?> type)
	{
		boolean ret = true;
		if(!SReflect.isStringConvertableType(type))
		{
			try
			{
				Method m = type.getMethod("fromString", new Class[]{String.class});
			}
			catch(Exception e)
			{
				try
				{
					Method m = type.getMethod("valueOf", new Class[]{String.class});
				}
				catch(Exception e2)
				{
					ret = false;
				}
			}
		}
		return ret;
	}
	

}
