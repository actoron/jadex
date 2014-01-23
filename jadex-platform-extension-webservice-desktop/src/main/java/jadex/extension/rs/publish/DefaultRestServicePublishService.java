package jadex.extension.rs.publish;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.extension.SJavassist;
import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.DefaultParameterMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

//import com.sun.jersey.api.container.ContainerFactory;
//import com.sun.jersey.api.container.grizzly.GrizzlyServerFactory;
//import com.sun.jersey.api.core.PackagesResourceConfig;
//import com.sun.jersey.api.core.ResourceConfig;
//import com.sun.jersey.api.json.JSONConfiguration;
//import com.sun.jersey.multipart.FormDataParam;

/**
 *  The default web service publish service.
 *  Publishes web services using the JDK Endpoint class.
 */
@Service
public class DefaultRestServicePublishService implements IWebPublishService
{
	//-------- constants --------
	
	/** Constant for boolean flag if automatic generation should be used.*/ 
	public static final String GENERATE = "generate";
	
	/** Constant for String[] for supported parameter media types.*/ 
	public static final String FORMATS = "formats";
	
	/** Constant for boolean.*/ 
	public static final String GENERATE_INFO = "generateinfo";
	
	/** The default media formats. */
//	public static String[] DEFAULT_FORMATS = new String[]{"xml", "json"};
	public static final MediaType[] DEFAULT_FORMATS = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};

//	/** The format -> media type mapping. */
//	public static Map<String, String> formatmap = SUtil.createHashMap(DEFAULT_FORMATS, 
//		new String[]{MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON});
	
	/** The service constant. */
	public static final String JADEXSERVICE = "jadexservice"; 
	
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** The servers per service id. */
	protected Map<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>> sidservers;
	
	/** The servers per uri. */
	protected Map<URI, HttpServer> uriservers;
	
	/** The generator. */
	protected IRestMethodGenerator generator;
	
	/** The proxy classes. */
	protected LRU<Tuple2<Class<?>, Class<?>>, Class<?>> proxyclasses;
	
	//-------- constructors --------
	
	/**
	 *  Create a new publish service.
	 */
	public DefaultRestServicePublishService()
	{
		this(new DefaultRestMethodGenerator());
	}
	
	/**
	 *  Create a new publish service.
	 */
	public DefaultRestServicePublishService(IRestMethodGenerator generator)
	{
		this.generator = generator;
		this.proxyclasses = new LRU<Tuple2<Class<?>, Class<?>>, Class<?>>(50);
	}
	
	//-------- methods --------
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
		return new Future<Boolean>(IPublishService.PUBLISH_RS.equals(publishtype));
	}
	
	/**
	 *  Get or generate a proxy class.
	 */
	protected Class<?> getProxyClass(IService service, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops) throws Exception
	{
		Class<?> iface = service.getServiceIdentifier().getServiceType().getType(classloader);
		Class<?> ret = proxyclasses.get(new Tuple2<Class<?>, Class<?>>(iface, baseclass));
		if(ret==null)
		{
			List<RestMethodInfo> rmis = generator.generateRestMethodInfos(service, classloader, baseclass, mapprops);
			ret = createProxyClass(service, classloader, baseclass, mapprops, rmis);
			proxyclasses.put(new Tuple2<Class<?>, Class<?>>(iface, baseclass), ret);
		}
		return ret;
	}
	
	/**
	 *  Publish a service.
	 *  @param cl The classloader.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(ClassLoader cl, IService service, PublishInfo pi)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			// Jaxb seems to use the context classloader so it needs to be set :-(
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			URI uri = new URI(pi.getPublishId());
						
			// Note: the expression evaluation is done on another component so that no original imports and classes can be used 
			// Should not be a problem because only basic properties are used (String, boolean)
			Map<String, Object> mapprops = new HashMap<String, Object>();
			if(pi.getProperties()!=null)
			{
				for(int i=0; i<pi.getProperties().size(); i++)
				{
					Object val = SJavaParser.getParsedValue(pi.getProperties().get(i), null, component.getFetcher(), component.getClassLoader());
					mapprops.put(pi.getProperties().get(i).getName(), val);
				}
			}
			
			// If no service type was specified it has to be generated.
//			Class<?> iface = service.getServiceIdentifier().getServiceType().getType(cl);
			Class<?> baseclazz = pi.getMapping()!=null? pi.getMapping().getType(cl): null;
			
			Class<?> rsimpl = getProxyClass(service, cl, baseclazz, mapprops);
			
//			List<RestMethodInfo> rmis = generator.generateRestMethodInfos(service, cl, baseclazz, mapprops);
//			System.out.println("Produced methods: ");
//			for(int i=0; i<rmis.size(); i++)
//				System.out.println(rmis.get(i));
//			Class<?> rsimpl = createProxyClass(service, cl, baseclazz, mapprops, rmis);
			
			Map<String, Object> props = new HashMap<String, Object>();
//			String jerseypack = PackagesResourceConfig.PROPERTY_PACKAGES;
//			props.put(uri.toString(), service);
			StringBuilder strb = new StringBuilder("jadex.extension.rs.publish"); // Add Jadex XML body reader/writer
			// Must not add package because a baseclass could be contained with the same path
			// This leads to an error in jersey
//			String pack = baseclazz!=null && baseclazz.getPackage()!=null? 
//				baseclazz.getPackage().getName(): iface.getPackage()!=null? iface.getPackage().getName(): null;
//			if(pack!=null)
//				strb.append(", ").append(pack);
			
//			props.put(jerseypack, strb.toString());
//			props.put(PackagesResourceConfig.FEATURE_REDIRECT, Boolean.TRUE);
			props.put("com.sun.jersey.api.container.grizzly.AllowEncodedSlashFeature", Boolean.TRUE);
//			props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			props.put(JADEXSERVICE, service);
//			PackagesResourceConfig config = new PackagesResourceConfig(props);
//			config.getClasses().add(rsimpl);
			
			final ResourceConfig rc = new ResourceConfig();
			rc.addProperties(props);
			rc.register(rsimpl);
			
			rc.register(new AbstractBinder()
			{
				protected void configure()
				{
					bind(ResourceConfig.class).in(Singleton.class);
					bind(rc).to(ResourceConfig.class);
				}
			});
			
//			URI baseuri = uri;
			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			
			HttpServer server = uriservers==null? null: uriservers.get(baseuri);
			HttpHandler handler = null;
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPath());
//				String pub = uri.toString();
//				if(pub.endsWith("/"))
//					pub.substring(0, pub.length()-1);
//					pub += "/";
				server = GrizzlyHttpServerFactory.createHttpServer(uri, rc);
				server.start();
//				handler = server.getHttpHandler();
				Map<HttpHandler, String[]> handlers = server.getServerConfiguration().getHttpHandlers();
				for(HttpHandler hand: handlers.keySet())
				{
					Set<String> set = SUtil.arrayToSet(handlers.get(hand));
					if(set.contains(uri.getPath()))
					{
						handler = hand;
					}
				}
				if(handler==null)
				{
					ret.setException(new RuntimeException("Publication error, failed to get http handler: "+uri.getPath()));
				}
				
				if(uriservers==null)
					uriservers = new HashMap<URI, HttpServer>();
				uriservers.put(baseuri, server);
			}
			else
			{
				System.out.println("Adding http handler to server: "+uri.getPath());
				handler = ContainerFactory.createContainer(HttpHandler.class, rc);
				ServerConfiguration sc = server.getServerConfiguration();
				sc.addHttpHandler(handler, uri.getPath());
//				Map h = sc.getHttpHandlers();
//				System.out.println("handlers: "+h);
			}
			
//			System.out.println("handler: "+handler+" "+server.getServerConfiguration().getHttpHandlers());
			
			if(sidservers==null)
				sidservers = new HashMap<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>>();
			sidservers.put(service.getServiceIdentifier(), new Tuple2<HttpServer, HttpHandler>(server, handler));

			Thread.currentThread().setContextClassLoader(ccl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public HttpServer getHttpServer(URI uri, ResourceConfig config)
	{
		HttpServer server = null;
		
		try
		{
			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
			server = uriservers==null? null: uriservers.get(baseuri);
			
			if(server==null)
			{
				System.out.println("Starting new server: "+uri.getPath());
				
//				String pub = uri.toString();
	//			if(pub.endsWith("/"))
	//				pub.substring(0, pub.length()-1);
	//				pub += "/";
				server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
				server.start();
	//			handler = server.getHttpHandler();
	//			Map<HttpHandler, String[]> handlers = server.getServerConfiguration().getHttpHandlers();
	//			for(HttpHandler hand: handlers.keySet())
	//			{
	//				Set<String> set = SUtil.arrayToSet(handlers.get(hand));
	//				if(set.contains(uri.getPath()))
	//				{
	//					handler = hand;
	//				}
	//			}
	//			if(handler==null)
	//			{
	//				throw new RuntimeException("Publication error, failed to get http handler: "+uri.getPath()));
	//			}
				
				if(uriservers==null)
					uriservers = new HashMap<URI, HttpServer>();
				uriservers.put(baseuri, server);
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return server;
	}
	
	/**
	 * 
	 */
	protected ResourceConfig createConfig()
	{
		Map<String, Object> props = new HashMap<String, Object>();
//		String jerseypack = PackagesResourceConfig.PROPERTY_PACKAGES;
//		props.put(uri.toString(), service);
		StringBuilder strb = new StringBuilder("jadex.extension.rs.publish"); // Add Jadex XML body reader/writer
		// Must not add package because a baseclass could be contained with the same path
		// This leads to an error in jersey
//		String pack = baseclazz!=null && baseclazz.getPackage()!=null? 
//			baseclazz.getPackage().getName(): iface.getPackage()!=null? iface.getPackage().getName(): null;
//		if(pack!=null)
//			strb.append(", ").append(pack);
//		props.put(jerseypack, strb.toString());
//		props.put(PackagesResourceConfig.FEATURE_REDIRECT, Boolean.TRUE);
		props.put("com.sun.jersey.api.container.grizzly.AllowEncodedSlashFeature", Boolean.TRUE);
//		props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//		props.put(JADEXSERVICE, service);
		ResourceConfig config = new ResourceConfig();
		config.addProperties(props);
		config.register(DummyResource.class);
		return config;
	}
	
//	/**
//	 * 
//	 */
//	public IFuture<Void> publishServet(URI uri, Object servlet)
//	{
//		HttpServer server = getHttpServer(uri, createConfig());
//		ServletConfigImpl conf = new ServletConfigImpl();
//		ServletHandler handler = new ServletHandler(conf);
//		handler.setContextPath(uri.getPath());
//        ServerConfiguration sc = server.getServerConfiguration();
//		sc.addHttpHandler(handler, uri.getPath());
//		
//		WebappContext ctx = new WebappContext(uri.getPath());
//		ServletRegistration reg = ctx.addServlet(SReflect.getInnerClassName(servlet.getClass()), servlet);
//		reg.addMapping(alias);
//		ctx.deploy(server);
//		
//		return IFuture.DONE;
//	}
	
	/**
	 *  Publish an html page.
	 */
	public IFuture<Void> publishHMTLPage(URI uri, final String html)
	{
		HttpServer server = getHttpServer(uri, createConfig());
		
        ServerConfiguration sc = server.getServerConfiguration();
		sc.addHttpHandler(new HttpHandler() 
		{
            public void service(Request request, Response resp) 
            {
            	try
            	{
	            	// Set response content type
	                resp.setContentType("text/html");
	
	                // Actual logic goes here.
	                Writer out = resp.getWriter();
	                out.write(html);
            	}
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            }
        }, uri.getPath());
		
		return IFuture.DONE;
	}
	
//	/**
//	 *  Publish a resource.
//	 */
////	public IFuture<Void> publishResource(URI uri, final ResourceInfo ri)
//	public IFuture<Void> publishResource(URI uri, final String type, final String filename)
//	{
//		HttpServer server = getHttpServer(uri, createConfig());
//		
//        ServerConfiguration sc = server.getServerConfiguration();
//		sc.addHttpHandler(new HttpHandler() 
//		{
//            public void service(Request request, Response resp) 
//            {
//            	try
//            	{
//	            	// Set response content type
//	                resp.setContentType(type!=null? type: "text/html");
//	
//	                InputStream is = null;
//	                try
//	        		{
//	        			is = SUtil.getResource0(filename, null);
//	        			OutputStream os = resp.getOutputStream();
//	        			SUtil.copyStream(is, os);
//	        		}
//	        		finally
//	        		{
//	        			try
//	        			{
//	        				if(is!=null)
//	        					is.close();
//	        			}
//	        			catch(Exception e)
//	        			{
//	        			}
//	        		}
//            	}
//            	catch(Exception e)
//            	{
//            		e.printStackTrace();
//            	}
//            }
//        }, uri.getPath());
//		
//		return IFuture.DONE;
//	}
	
	/**
	 *  Publish resources via a rel path.
	 */
	public IFuture<Void> publishResources(URI uri, String path)
	{
		HttpServer server = getHttpServer(uri, createConfig());
        ServerConfiguration sc = server.getServerConfiguration();
        path = path.endsWith("/")? path: path+"/";
		sc.addHttpHandler(new CLStaticHttpHandler(component.getClassLoader(), path)
		{
			public void service(Request request, Response resp) throws Exception
			{
				super.service(request, resp);
			}
		}, uri.getPath());
		
		return IFuture.DONE;
	}
	
	/**
	 *  Unpublish a service.
	 *  @param sid The service identifier.
	 */
	public IFuture<Void> unpublishService(IServiceIdentifier sid)
	{
		Future<Void> ret = new Future<Void>();
		boolean stopped = false;
		if(sidservers!=null)
		{
			Tuple2<HttpServer, HttpHandler> tup = sidservers.remove(sid);
			if(tup!=null)
			{
				HttpServer server = tup.getFirstEntity();
			    ServerConfiguration config = server.getServerConfiguration();
			    config.removeHttpHandler(tup.getSecondEntity());
			    if(config.getHttpHandlers().size()==0)
			    	server.stop();
			    stopped = true;
				ret.setResult(null);
			}
		}
		if(!stopped)
			ret.setException(new RuntimeException("Published service could not be stopped: "+sid));
		return ret;
	}

	
	/**
	 *  Create a service proxy class.
	 *  @param service The Jadex service.
	 *  @param classloader The classloader.
	 *  @param type The web service interface type.
	 *  @return The proxy object.
	 */
	protected Class<?> createProxyClass(IService service, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops, List<RestMethodInfo> geninfos) throws Exception
	{
		Class<?> ret = null;

		Class<?> iface = service.getServiceIdentifier().getServiceType().getType(classloader);
//		System.out.println("Creating new proxy class: "+SReflect.getInnerClassName(iface));
		
		// The name of the class has to ensure that it represents the different class properties:
		// - the package+"Proxy"+name of the baseclass or (if not available) interface
		// - the hashcode of the properties
		// - only same implclass name + same props => same generated classname
		
		StringBuilder builder = new StringBuilder();
//		Class<?> iface = service.getServiceIdentifier().getServiceType().getType(classloader);
		Class<?> nameclazz = baseclass!=null? baseclass: iface;
		if(nameclazz.getPackage()!=null)
			builder.append(nameclazz.getPackage().getName());
		builder.append(".Proxy");
		builder.append(nameclazz.getSimpleName());
//			if(mapprops!=null && mapprops.size()>0)
//				builder.append(mapprops.hashCode());
		builder.append(geninfos.hashCode());
		String name = builder.toString();

		try
		{
			ret = classloader.loadClass(name);
//			ret = SReflect.classForName0(name, classloader); // does not work because SReflect cache saves that not found!
		}
		catch(Exception e)
		{
//			System.out.println("Not found, creating new: "+name);
			ClassPool pool = new ClassPool(null);
			pool.appendSystemPath();
			
			CtClass proxyclazz = pool.makeClass(name, baseclass==null || baseclass.isInterface()? 
				null: SJavassist.getCtClass(baseclass, pool));
			ClassFile cf = proxyclazz.getClassFile();
			ConstPool constpool = cf.getConstPool();
	
			// Add field for functionsjs
			CtField fjs = new CtField(SJavassist.getCtClass(String.class, pool), "__functionsjs", proxyclazz);
			proxyclazz.addField(fjs);

			// Add field for stylecss
			CtField scss = new CtField(SJavassist.getCtClass(String.class, pool), "__stylecss", proxyclazz);
			proxyclazz.addField(scss);
			
			// Add field with annotation for resource context
			CtField rc = new CtField(SJavassist.getCtClass(ResourceConfig.class, pool), "__rc", proxyclazz);
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(constpool, SJavassist.getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			rc.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(rc);
			
			// Add field with annotation for uriinfo context
			CtField ui = new CtField(SJavassist.getCtClass(UriInfo.class, pool), "__ui", proxyclazz);
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, SJavassist.getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			ui.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(ui);
			
			// Add methods 
			for(Iterator<RestMethodInfo> it=geninfos.iterator(); it.hasNext(); )
			{
				RestMethodInfo rmi = it.next();
				
				CtMethod invoke = SJavassist.getCtClass(rmi.getDelegateClazz(), pool)
					.getDeclaredMethod(rmi.getDelegateMethodName());
				
				// Do not generate method if user has implemented it by herself
//				System.out.println("return type: "+rmi.getName()+" "+rmi.getReturnType());
				Class<?> rt = SReflect.unwrapGenericType(rmi.getReturnType());
				if(rt==null && rmi.getReturnType() instanceof Class) 
					rt = (Class<?>)rmi.getReturnType();
				CtClass rettype = SJavassist.getCtClass(rt, pool);
				CtClass[] paramtypes = SJavassist.getCtClasses(rmi.getParameterTypes(), pool);
				CtClass[] exceptions = SJavassist.getCtClasses(rmi.getExceptionTypes(), pool);
				
//				CtMethod.ConstParameter cop = CtMethod.ConstParameter.string(rmi.getSignature());
//				if(rmi.getSignature()==null)
//					System.out.println("sig is "+rmi.getSignature()+" "+rmi.getDelegateMethodName()+" "+rmi.getPath());
				CtMethod.ConstParameter cop = CtMethod.ConstParameter.string(rmi.getSignature());
				CtMethod m = CtNewMethod.wrapped(rettype, rmi.getName(),
					paramtypes, exceptions, invoke, cop, proxyclazz);
						
				// path.
				attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				annot = new Annotation(constpool, SJavassist.getCtClass(rmi.getRestType(), pool));
				attr.addAnnotation(annot);
				annot = new Annotation(constpool, SJavassist.getCtClass(Path.class, pool));
				annot.addMemberValue("value", new StringMemberValue(rmi.getPath(), constpool));
				attr.addAnnotation(annot);
						
				// consumes.
				List<MediaType> consumed = rmi.getConsumed();
				if(!consumed.isEmpty())
				{
					List<MemberValue> cons = new ArrayList<MemberValue>();
					for(Iterator<MediaType> it2=consumed.iterator(); it2.hasNext(); )
					{
						MediaType mt = it2.next();
						cons.add(new StringMemberValue(mt.toString(), constpool));
					}
					annot = new Annotation(constpool, SJavassist.getCtClass(Consumes.class, pool));
					ArrayMemberValue vals = new ArrayMemberValue(new StringMemberValue(constpool), constpool);
					vals.setValue(cons.toArray(new MemberValue[0]));
					annot.addMemberValue("value", vals);
					attr.addAnnotation(annot);
				}
				
				// produces.
				List<MediaType> produced = rmi.getProduced();
				if(!produced.isEmpty())
				{
					List<MemberValue> prods = new ArrayList<MemberValue>();
					for(Iterator<MediaType> it2=produced.iterator(); it2.hasNext(); )
					{
						MediaType mt = it2.next();
						prods.add(new StringMemberValue(mt.toString(), constpool));
					}
					ArrayMemberValue vals = new ArrayMemberValue(new StringMemberValue(constpool), constpool);
					vals.setValue(prods.toArray(new MemberValue[0]));
					annot = new Annotation(constpool, SJavassist.getCtClass(Produces.class, pool));
					annot.addMemberValue("value", vals);
					attr.addAnnotation(annot);
				}
				
				MethodInfo methodmapper = rmi.getMethodMapper();
				if(methodmapper!=null)
				{
					annot = new Annotation(constpool, SJavassist.getCtClass(MethodMapper.class, pool));
					annot.addMemberValue("value", new StringMemberValue(methodmapper.getName(), constpool));
					Class<?>[] ptypes = methodmapper.getParameterTypes(classloader);
					ArrayMemberValue vals = new ArrayMemberValue(new ClassMemberValue(constpool), constpool);
					MemberValue[] mvals = new MemberValue[methodmapper.getParameterTypeInfos().length];
					for(int i=0; i<mvals.length; i++)
					{
						mvals[i] = new ClassMemberValue(ptypes[i].getName(), constpool);
					}
					vals.setValue(mvals);
					annot.addMemberValue("parameters", vals);
					attr.addAnnotation(annot);
				}
				
				Value parametermapper = rmi.getParameterMapper();
				if(parametermapper!=null || rmi.isAutomapping())
				{
					annot = new Annotation(constpool, SJavassist.getCtClass(ParametersMapper.class, pool));
					Annotation value = new Annotation(constpool, SJavassist.getCtClass(jadex.bridge.service.annotation.Value.class, pool));
					if(parametermapper!=null && parametermapper.getExpression()!=null && parametermapper.getExpression().length()==0)
						value.addMemberValue("value", new StringMemberValue(parametermapper.getExpression(), constpool));
					if(parametermapper!=null && parametermapper.getClazz()!=null && !parametermapper.getClazz().equals(Object.class))
						value.addMemberValue("clazz", new ClassMemberValue(parametermapper.getClazz().getName(), constpool));
					annot.addMemberValue("value", new AnnotationMemberValue(value, constpool));
					annot.addMemberValue("automapping", new BooleanMemberValue(rmi.isAutomapping(), constpool));
					attr.addAnnotation(annot);
				}
				
				Value resultmapper = rmi.getResultMapper();
				if(resultmapper!=null)
				{
					annot = new Annotation(constpool, SJavassist.getCtClass(ResultMapper.class, pool));
					Annotation value = new Annotation(constpool, SJavassist.getCtClass(jadex.bridge.service.annotation.Value.class, pool));
					if(resultmapper.getExpression()!=null && resultmapper.getExpression().length()==0)
						value.addMemberValue("value", new StringMemberValue(resultmapper.getExpression(), constpool));
					if(resultmapper.getClazz()!=null && !resultmapper.getClazz().equals(Object.class))
						value.addMemberValue("clazz", new ClassMemberValue(resultmapper.getClazz().getName(), constpool));
					annot.addMemberValue("value", new AnnotationMemberValue(value, constpool));
					attr.addAnnotation(annot);
				}
				
				m.getMethodInfo().addAttribute(attr);
				proxyclazz.addMethod(m);
						
				// add @QueryParam if get
				// this means that each parameter of the method is automatically
				// mapped to arg0, arg1 etc of the request
				if(GET.class.equals(rmi.getRestType()))
				{
					int pcnt = rmi.getParameterTypes().length;
					if(pcnt>0)
					{
						Annotation[][] annos = new Annotation[pcnt][];
						ConstPool cp = m.getMethodInfo().getConstPool();
						for(int k=0; k<annos.length; k++)
						{
							Annotation anno = new Annotation(cp, SJavassist.getCtClass(QueryParam.class, pool));
							anno.addMemberValue("value", new StringMemberValue("arg"+k, cp));
							annos[k] = new Annotation[]{anno};
						}
						SJavassist.addMethodParameterAnnotation(m, annos, pool);
					}
				}
				// add @FormDataParam if post
				else if(POST.class.equals(rmi.getRestType()))
				{
					int pcnt = rmi.getParameterTypes().length;
					if(pcnt>0)
					{
						Annotation[][] annos = new Annotation[pcnt][];
						ConstPool cp = m.getMethodInfo().getConstPool();
						for(int k=0; k<annos.length; k++)
						{
							Annotation anno = new Annotation(cp, SJavassist.getCtClass(FormDataParam.class, pool));
							anno.addMemberValue("value", new StringMemberValue("arg"+k, cp));
							annos[k] = new Annotation[]{anno};
						}
						SJavassist.addMethodParameterAnnotation(m, annos, pool);
					}
				}
//						System.out.println("m: "+m.getName()+" "+SUtil.arrayToString(m.getParameterTypes()));
			}
			
			// Add the path annotation 
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, SJavassist.getCtClass(Path.class, pool));
			annot.addMemberValue("value", new StringMemberValue("", constpool));
			attr.addAnnotation(annot);
			cf.addAttribute(attr);
			
			// Add method mapping table
//			CtField fjs = new CtField(SJavassist.getCtClass(Map.class, pool), "__methodmap", proxyclazz);
//			proxyclazz.addField(fjs, CtField.Initializer.byExpr("jadex.commons.SUtil.createHashMap(new String[]{\"a\"}, new String[]{\"a\"})"));
							
			ret = proxyclazz.toClass(classloader, iface.getProtectionDomain());
			proxyclazz.freeze();
			
//			System.out.println("create proxy class: "+ret.getName());
//			Method[] ms = ret.getMethods();
//			for(Method m: ms)
//			{
//				System.out.println("m: "+m);
//			}
		}
		
		return ret;
	}
	
	/**
	 *  Method that is invoked when rest service is called.
	 * 
	 *  Functionality blueprint for all service methods.
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public Object invoke(Object[] params, String sig)
	{
		Object ret = null;
		
//		System.out.println("called invoke: "+sig+" "+Arrays.toString(params));
		
		try
		{
			// find own method
			
			Method[] ms = getClass().getDeclaredMethods();
			Method method = null;
			for(Method m: ms)
			{
				if(RestMethodInfo.buildSignature(m.getName(), m.getParameterTypes()).equals(sig))
				{
					method = m;
					break;
				}
			}
			if(method==null)
			{
				System.out.println("methods: "+Arrays.toString(ms));
				throw new RuntimeException("No method '"+sig+"' on class: "+getClass());
			}
			
//			StackTraceElement[] s = Thread.currentThread().getStackTrace();
//			String name = s[2].getMethodName();
			
//			System.out.println("name is: "+name);

//			for(int i=0;i<s.length; i++)
//			{
//				System.out.println(s[i].getMethodName());
//			}
//			String name = SReflect.getMethodName();
//			Method[] methods = SReflect.getMethods(getClass(), name);
//		    Method method = null;
//			if(methods.length>1)
//			{
//			    for(int i=0; i<methods.length && method==null; i++)
//			    {
//			    	Class<?>[] types = methods[i].getParameterTypes();
//			    	if(types.length==params.length)
//			    	{
//			    		// check param types
//			    		method = methods[i];
//			    	}
//			    }
//			}
//			else if(methods.length==1)
//			{
//				method = methods[0];
//			}
//			else
//			{
//				throw new RuntimeException("No method '"+name+"' on class: "+getClass());
//			}
//			System.out.println("call: "+this+" "+method+" "+SUtil.arrayToString(params)+" "+name);
			
			// check if mappers are there
			ResourceConfig rc = (ResourceConfig)getClass().getDeclaredField("__rc").get(this);
			
//			Object service = rc.getProperty(JADEXSERVICE);
			Object service = rc.getProperty("jadexservice");
//			System.out.println("jadex service is: "+service);

			Method targetmethod = null;
			if(method.isAnnotationPresent(MethodMapper.class))
			{
				MethodMapper mm = method.getAnnotation(MethodMapper.class);
				targetmethod = SReflect.getMethod(service.getClass(), mm.value(), mm.parameters());
			}
			else
			{
				String mname = method.getName();
				if(mname.endsWith("XML"))
					mname = mname.substring(0, mname.length()-3);
				if(mname.endsWith("JSON"))
					mname = mname.substring(0, mname.length()-4);
				targetmethod = service.getClass().getMethod(mname, method.getParameterTypes());
			}
			
//			System.out.println("target: "+targetmethod);
			
			Object[] targetparams = params;
			if(method.isAnnotationPresent(ParametersMapper.class))
			{
//				System.out.println("foundmapper");
				ParametersMapper mm = method.getAnnotation(ParametersMapper.class);
				if(!mm.automapping())
				{
					Class<?> clazz = mm.value().clazz();
					Object mapper;
					if(!Object.class.equals(clazz))
					{
						mapper = clazz.newInstance();
					}
					else
					{
						mapper = SJavaParser.evaluateExpression(mm.value().value(), null);
					}
					if(mapper instanceof IValueMapper)
						mapper = new DefaultParameterMapper((IValueMapper)mapper);
					
					targetparams = ((IParameterMapper)mapper).convertParameters(params);
				}
				else
				{
//					System.out.println("automapping detected");
					Class<?>[] ts = targetmethod.getParameterTypes();
					targetparams = new Object[ts.length];
					if(ts.length==1 && SReflect.isSupertype(ts[0], Map.class))
					{
						UriInfo ui = (UriInfo)getClass().getDeclaredField("__ui").get(this);
						MultivaluedMap<String, String> vals = ui.getQueryParameters();
						Map<String, String> ps = new HashMap<String, String>();
						for(Map.Entry<String, List<String>> e: vals.entrySet())
						{
							List<String> val = e.getValue();
							ps.put(e.getKey(), val!=null && !val.isEmpty()? val.get(0): null);
						}
						targetparams[0] = ps;
					}
				}
			}
	
//			System.out.println("method: "+method.getName()+" "+method.getDeclaringClass().getName());
//			System.out.println("targetparams: "+SUtil.arrayToString(targetparams));
			System.out.println("call: "+targetmethod.getName()+" paramtypes: "+SUtil.arrayToString(targetmethod.getParameterTypes())+" on "+service+" "+Arrays.toString(targetparams));
//			
			ret = targetmethod.invoke(service, targetparams);
			if(ret instanceof IFuture)
			{
				ret = ((IFuture<?>)ret).get(new ThreadSuspendable());
			}
			
			if(method.isAnnotationPresent(ResultMapper.class))
			{
				ResultMapper mm = method.getAnnotation(ResultMapper.class);
				Class<?> clazz = mm.value().clazz();
				IValueMapper mapper;
//				System.out.println("res mapper: "+clazz);
				if(!Object.class.equals(clazz))
				{
					mapper = (IValueMapper)clazz.newInstance();
				}
				else
				{
					mapper = (IValueMapper)SJavaParser.evaluateExpression(mm.value().value(), null);
				}
				
				ret = mapper.convertValue(ret);
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		
		return ret;
	}
	
	/**
	 *  Functionality blueprint for get service info web method.
	 *  Creates a html page with css for style and javascript for ajax post requests.
	 *  The service info site contains a section for each published method. 
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public Object getServiceInfo(Object[] params, String sig)
	{
		StringBuffer ret = new StringBuffer();
		
		try
		{
//			System.out.println("huhu: "+getClass().getDeclaredField("__ui").get(this));
			ResourceConfig rc = (ResourceConfig)getClass().getDeclaredField("__rc").get(this);
//			System.out.println("resconf: "+rc);
			Object service = rc.getProperty("jadexservice");
//			System.out.println("jadex service is: "+service);

			Field fjs = getClass().getDeclaredField("__functionsjs");
			String functionsjs = (String)fjs.get(this);
			if(functionsjs==null)
			{
				try
				{
					InputStream is = SUtil.getResource0("jadex/extension/rs/publish/functions.js", 
						Thread.currentThread().getContextClassLoader());
					functionsjs = new Scanner(is).useDelimiter("\\A").next();
					fjs.set(this, functionsjs);
//					System.out.println(functionsjs);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			
			Field scss = getClass().getDeclaredField("__stylecss");
			String stylecss = (String)scss.get(this);
			if(stylecss==null)
			{
				try
				{
					InputStream is = SUtil.getResource0("jadex/extension/rs/publish/style.css", 
						Thread.currentThread().getContextClassLoader());
					stylecss = new Scanner(is).useDelimiter("\\A").next();
					scss.set(this, stylecss);
//					System.out.println(functionsjs);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		
			ret.append("<html>");
			ret.append("<head>");
			ret.append(stylecss);
			ret.append(functionsjs);
	//		ret.append("<script src=\"functions.js\" type=\"text/javascript\"/>");
			ret.append("<head>");
			ret.append("</head>");
			ret.append("<body>");
			
			ret.append("<div class=\"header\">");
			ret.append("<h1>");//Service Info for: ");
			String ifacename = ((IService)service).getServiceIdentifier().getServiceType().getTypeName();
			ret.append(SReflect.getUnqualifiedTypeName(ifacename));
			ret.append("</h1>");
			ret.append("</div>");

			ret.append("<div class=\"middle\">");
			
			UriInfo ui = (UriInfo)getClass().getDeclaredField("__ui").get(this);
			
			Class<?> clazz = getClass();
			List<Method> methods = new ArrayList<Method>();
			while(!clazz.equals(Object.class))
			{
				List<Method> l = SUtil.arrayToList(clazz.getDeclaredMethods());
				methods.addAll(l);
				clazz = clazz.getSuperclass();
			}
			
			Collections.sort(methods, new MethodComparator());
			
			if(methods!=null)
			{
				for(int i=0; i<methods.size(); i++)
				{
					Method method = methods.get(i);
					Class<?> restmethod = RSJAXAnnotationHelper.getDeclaredRestType(method);
					if(restmethod!=null)
					{
//						System.out.println("method: "+method.getName()+" "+SUtil.arrayToString(methods));
//						java.lang.annotation.Annotation[][] ans = method.getParameterAnnotations();
//						for(int j=0; j<ans.length; j++)
//						{
//							System.out.println(SUtil.arrayToString(ans[j]));
//						}
						Path path = method.getAnnotation(Path.class);
						Consumes consumes = method.getAnnotation(Consumes.class);
						Produces produces = method.getAnnotation(Produces.class);
						Class<?>[] ptypes = method.getParameterTypes();
						
						ret.append("<div class=\"method\">");
						
						ret.append("<div class=\"methodname\">");
//						ret.append("<i><b>");
						ret.append(method.getName());
//						ret.append("</b></i>");
						
						ret.append("(");
						if(ptypes!=null && ptypes.length>0)
						{
							for(int j=0; j<ptypes.length; j++)
							{
								ret.append(SReflect.getUnqualifiedClassName(ptypes[j]));
								if(j+1<ptypes.length)
									ret.append(", ");
							}
						}
						ret.append(")");
						ret.append("</div>");
//						ret.append("</br>");
						
						ret.append("<div class=\"restproperties\">");
						String resttype = SReflect.getUnqualifiedClassName(restmethod);
						ret.append(resttype).append(" ");
						
						if(consumes!=null)
						{
							String[] cons = consumes.value();
							if(cons.length>0)
							{
								ret.append("<i>");
								ret.append("Consumes: ");
								ret.append("</i>");
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
								ret.append("<i>");
								ret.append("Produces: ");
								ret.append("</i>");
								for(int j=0; j<prods.length; j++)
								{
									ret.append(prods[j]);
									if(j+1<prods.length)
										ret.append(" ,");
								}
								ret.append(" ");
							}
						}
//						ret.append("</br>");
						ret.append("</div>");

						UriBuilder ub = ui.getBaseUriBuilder();
						if(path!=null)
							ub.path(path.value());
						String link = ub.build((Object[])null).toString();
						
						if(ptypes.length>0)
						{
							ret.append("<div class=\"servicelink\">");
							ret.append(link);
							ret.append("</div>");
							
							// For post set the media type of the arguments.
							ret.append("<form class=\"arguments\" action=\"").append(link).append("\" method=\"")
								.append(resttype.toLowerCase()).append("\" enctype=\"multipart/form-data\" ");
							
							if(restmethod.equals(POST.class))
								ret.append("onSubmit=\"return extract(this)\"");
							ret.append(">");
							
							for(int j=0; j<ptypes.length; j++)
							{
								ret.append("arg").append(j).append(": ");
								ret.append("<input name=\"arg").append(j).append("\" type=\"text\" />");
//									.append(" accept=\"").append(cons[0]).append("\" />");
							}
							
							ret.append("<select name=\"mediatype\">");
							if(consumes!=null)
							{
								String[] cons = consumes.value();
								if(cons!=null && cons.length>0)
								{
//									ret.append("<select name=\"mediatype\">");
									for(int j=0; j<cons.length; j++)
									{
										// todo: hmm? what about others?
										if(!MediaType.MULTIPART_FORM_DATA.equals(cons[j]) &&
											!MediaType.APPLICATION_FORM_URLENCODED.equals(cons[j]))
										{
											ret.append("<option>").append(cons[j]).append("</option>");
										}
									}
								}
							}
							else
							{
								ret.append("<option>").append(MediaType.TEXT_PLAIN).append("</option>");
							}
							ret.append("</select>");
							
							ret.append("<input type=\"submit\" value=\"invoke\"/>");
							ret.append("</form>");
						}
						else
						{
							ret.append("<div class=\"servicelink\">");
							ret.append("<a href=\"").append(link).append("\">").append(link).append("</a>");
							ret.append("</div>");
						}
						
						ret.append("</div>");
					}
				}
			}
			
			ret.append("</div>");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		ret.append("</body></html>");

		return ret.toString();
	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args) throws Exception
//	{
//		URI uri = new URI("http://localhost:8080/bank");
////		URI newuri = new URI(uri.getScheme(), uri.getAuthority(), null);
//		URI newuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
//		System.out.println(newuri);
//	}
}
