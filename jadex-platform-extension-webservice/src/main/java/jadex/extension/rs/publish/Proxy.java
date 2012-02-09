package jadex.extension.rs.publish;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceConfig;

/**
 *  Base class for generated web service proxies.
 *  
 *  Generated proxies implement a domain dependent web service interface
 *  by delegation methods that all call the invocation handler.
 *  In this way the proxy does the same as a Java dynamic proxy.
 *  
 *  The invoke method in this class is copied as body for all
 *  service methods, i.e. the invoke method is not called itself at any time.
 */
public class Proxy
{
	//-------- methods --------

	/**
	 *  Functionality blueprint for all service methods.
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public Object invoke(Object[] params)
	{
		Object ret = null;
		
//		System.out.println("called invoke: "+params);
		
		try
		{
			StackTraceElement[] s = Thread.currentThread().getStackTrace();
			String name = s[2].getMethodName();
//			for(int i=0;i<s.length; i++)
//			{
//				System.out.println(s[i].getMethodName());
//			}
//			String name = SReflect.getMethodName();
			Method[] methods = SReflect.getMethods(getClass(), name);
		    Method method = null;
			if(methods.length>1)
			{
			    for(int i=0; i<methods.length && method==null; i++)
			    {
			    	Class[] types = methods[i].getParameterTypes();
			    	if(types.length==params.length)
			    	{
			    		// check param types
			    		method = methods[i];
			    	}
			    }
			}
			else if(methods.length==1)
			{
				method = methods[0];
			}
//			System.out.println("call: "+this+" "+method+" "+args+" "+name);
			
			try
			{
				ResourceConfig rc = (ResourceConfig)getClass().getDeclaredField("__rc").get(this);
				Object service = rc.getProperty(DefaultRestServicePublishService.JADEXSERVICE);
				
				String mname = method.getName();
				if(mname.endsWith("XML"))
					mname = mname.substring(0, mname.length()-3);
				if(mname.endsWith("JSON"))
					mname = mname.substring(0, mname.length()-4);

				System.out.println("call: "+mname+" on "+service);
				
				Method m = service.getClass().getMethod(mname, method.getParameterTypes());
				ret = m.invoke(service, params);
				if(ret instanceof IFuture)
				{
					ret = ((IFuture)ret).get(new ThreadSuspendable());
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		
		return ret;
	}
	
	/**
	 *  Functionality blueprint for get service info.
	 *  @return The result.
	 */
	public Object getServiceInfo(Object[] params)
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<html><body>");
		ret.append("<h1>Service Info for: ");
		ret.append(SReflect.getUnqualifiedClassName(getClass()));
		ret.append("</h1>");
	
		try
		{
			UriInfo ui = (UriInfo)getClass().getDeclaredField("__ui").get(this);
			
			Method[] methods = getClass().getDeclaredMethods();
			if(methods!=null)
			{
				for(int i=0; i<methods.length; i++)
				{
					Annotation restmethod = methods[i].getAnnotation(GET.class);
					if(restmethod==null)
						restmethod =  methods[i].getAnnotation(POST.class);
					if(restmethod==null)
						restmethod =  methods[i].getAnnotation(PUT.class);
					if(restmethod==null)
						restmethod =  methods[i].getAnnotation(DELETE.class);
					if(restmethod==null)
						restmethod =  methods[i].getAnnotation(HEAD.class);
					if(restmethod==null)
						restmethod =  methods[i].getAnnotation(OPTIONS.class);
					if(restmethod!=null)
					{
						Path path = methods[i].getAnnotation(Path.class);
						Consumes consumes = methods[i].getAnnotation(Consumes.class);
						Produces produces = methods[i].getAnnotation(Produces.class);
						Class[] ptypes = methods[i].getParameterTypes();
						
						ret.append("<p>");
						ret.append("<i>");
						ret.append(methods[i].getName());
						ret.append("</i>");
						if(ptypes!=null && ptypes.length>0)
						{
							ret.append("(");
							for(int j=0; j<ptypes.length; j++)
							{
								ret.append(SReflect.getUnqualifiedClassName(ptypes[j]));
								if(j+1<ptypes.length)
									ret.append(", ");
							}
							ret.append(")");
						}
						ret.append("</br>");
						
						String resttype = SReflect.getUnqualifiedClassName(restmethod.annotationType());
						ret.append(resttype).append(" ");
						if(consumes!=null)
						{
							String[] cons = consumes.value();
							if(cons.length>0)
							{
								ret.append("Consumes: ");
								for(int j=0; j<cons.length; j++)
								{
									ret.append(cons[j]);
									if(j+1<cons.length)
										ret.append(" ,");
								}
								ret.append(" ");
							}
						}
						if(produces!=null)
						{
							String[] prods = produces.value();
							if(prods.length>0)
							{
								ret.append("Produces: ");
								for(int j=0; j<prods.length; j++)
								{
									ret.append(prods[j]);
									if(j+1<prods.length)
										ret.append(" ,");
								}
								ret.append(" ");
							}
						}
						ret.append("</br>");

						UriBuilder ub = ui.getBaseUriBuilder();
						if(path!=null)
							ub.path(path.value());
						String link = ub.build(null).toString();
						if(ptypes.length>0 || restmethod.annotationType().equals(POST.class))
						{
							ret.append("<form action=\"").append(link).append("\" method=\"")
//								.append(resttype.toLowerCase()).append("\" enctype=\"application/json\" >");
								.append(resttype.toLowerCase()).append("\" enctype=\"multipart/form-data\" >");
							for(int j=0; j<ptypes.length; j++)
							{
								ret.append("arg").append(j).append(": ");
								ret.append("<input name=\"arg").append(j).append("\" type=\"text\"/>");
							}
							ret.append("<input type=\"submit\" value=\"invoke\"/></form>");
						}
						else
						{
							ret.append("<a href=\"").append(link).append("\">").append(link).append("</a>");
						}
						ret.append("</p>");
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		ret.append("</body></html>");

		return ret.toString();
	}
	
	public static void main(String[] args)
	{
		Proxy p = new Proxy();
		p.getServiceInfo(null);
	}
}
