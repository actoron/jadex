package jadex.extension.rs.publish;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ResourceConfig;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.SJavassist;
import jadex.extension.rs.invoke.RSJAXAnnotationHelper;
import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.javaparser.SJavaParser;
import jadex.micro.annotation.Binding;
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
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;


/**
 *  The default web service publish service.
 *  Publishes web services using the JDK Endpoint class.
 */
@Service
public abstract class AbstractRestServicePublishService implements IWebPublishService
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
	
//	/** The servers per service id. */
//	protected Map<IServiceIdentifier, Tuple2<HttpServer, HttpHandler>> sidservers;
	
//	/** The servers per uri. */
//	protected Map<URI, HttpServer> uriservers;
	
	/** The generator. */
	protected IRestMethodGenerator generator;
	
	/** The proxy classes. */
	protected LRU<Tuple2<Class<?>, Class<?>>, Class<?>> proxyclasses;
	
//	/** The webproxy refreshers. */
//	protected Map<IServiceIdentifier, Object> webproxyrefrehsers;
	
	//-------- constructors --------
	
	/**
	 *  Create a new publish service.
	 */
	public AbstractRestServicePublishService()
	{
		this(new DefaultRestMethodGenerator());
	}
	
	/**
	 *  Create a new publish service.
	 */
	public AbstractRestServicePublishService(IRestMethodGenerator generator)
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
		return IPublishService.PUBLISH_RS.equals(publishtype) ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Get or generate a proxy class.
	 */
	protected Class<?> getProxyClass(IServiceIdentifier serviceid, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops) throws Exception
	{
		Class<?> iface = serviceid.getServiceType().getType(classloader);
		Class<?> ret = proxyclasses.get(new Tuple2<Class<?>, Class<?>>(iface, baseclass));
		if(ret==null)
		{
			List<RestMethodInfo> rmis = generator.generateRestMethodInfos(serviceid, classloader, baseclass, mapprops);
			ret = createProxyClass(serviceid, classloader, baseclass, mapprops, rmis);
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
	public IFuture<Void> publishService(IServiceIdentifier serviceid, final PublishInfo pi)
	{
		final Future<Void> ret = new Future<Void>();
		
		ClassLoader cl = null;
		ILibraryService ls = SServiceProvider.getLocalService(component, ILibraryService.class, Binding.SCOPE_PLATFORM);
		if (serviceid.getProviderId().getPlatformName().equals(component.getComponentIdentifier().getPlatformName()))
		{
			// Local publish, get the component's classloader.
			IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, Binding.SCOPE_PLATFORM);
			IComponentDescription desc = cms.getComponentDescription(serviceid.getProviderId()).get();
			cl = ls.getClassLoader(desc.getResourceIdentifier()).get();
		}
		else
		{
			// Remote, use ALL classloader.
			cl = ls.getClassLoader(ls.getRootResourceIdentifier()).get();
		}
		
		System.out.println("start publish: "+pi.getPublishId());
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("end publish: "+pi.getPublishId());
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
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
					Object val = SJavaParser.getParsedValue(pi.getProperties().get(i), null, component!=null? 
						component.getFetcher(): null, component!=null? component.getClassLoader(): null);
					mapprops.put(pi.getProperties().get(i).getName(), val);
				}
			}
			
			// If no service type was specified it has to be generated.
			Class<?> iface = serviceid.getServiceType().getType(cl);
			Class<?> baseclazz = pi.getMapping()!=null? pi.getMapping().getType(cl): null;
			
			Class<?> rsimpl = getProxyClass(serviceid, cl, baseclazz, mapprops);
			
//			List<RestMethodInfo> rmis = generator.generateRestMethodInfos(service, cl, baseclazz, mapprops);
//			System.out.println("Produced methods: ");
//			for(int i=0; i<rmis.size(); i++)
//				System.out.println(rmis.get(i));
//			Class<?> rsimpl = createProxyClass(service, cl, baseclazz, mapprops, rmis);
			
			Map<String, Object> props = new HashMap<String, Object>();
//			String jerseypack = PackagesResourceConfig.PROPERTY_PACKAGES;
//			props.put(uri.toString(), service);
//			StringBuilder strb = new StringBuilder("jadex.extension.rs.publish"); // Add Jadex XML body reader/writer
			// Must not add package because a baseclass could be contained with the same path
			// This leads to an error in jersey
			String pack = baseclazz!=null && baseclazz.getPackage()!=null? 
				baseclazz.getPackage().getName(): iface.getPackage()!=null? iface.getPackage().getName(): null;
//			if(pack!=null)
//				strb.append(", ").append(pack);
			
//			props.put(jerseypack, strb.toString());
//			props.put(PackagesResourceConfig.FEATURE_REDIRECT, Boolean.TRUE);
			props.put("com.sun.jersey.api.container.grizzly.AllowEncodedSlashFeature", Boolean.TRUE);
//			props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			props.put(JADEXSERVICE, serviceid);
			
			// Add info for web proxy 
//			String wpurl = (String)mapprops.get(PublishInfo.WP_URL);
//			if(wpurl!=null)
//			{
//				props.put(PublishInfo.WP_URL, wpurl);
//				props.put(PublishInfo.WP_APPNAME, mapprops.get(PublishInfo.WP_APPNAME));
//				props.put(PublishInfo.WP_TARGET, uri.toString());
//			}
			
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
			
			// Enable json support
//			rc.packages("org.glassfish.jersey.examples.jackson").register(JacksonFeature.class);
			
			MoxyJsonConfig mc = new MoxyJsonConfig();
		    Map<String, String> m = new HashMap<String, String>(1);
		    m.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		    mc.setNamespacePrefixMapper(m).setNamespaceSeparator(':');
			rc.packages(pack).register(mc.resolver());
//			rc.register(new MoxyJsonFeature());
			rc.register(MultiPartFeature.class);
			
			internalPublishService(uri, rc, serviceid, pi);
//			System.out.println("handler: "+handler+" "+server.getServerConfiguration().getHttpHandlers());

//			String wpurl = (String)mapprops.get(PublishInfo.WP_URL);
//			if(wpurl!=null)
//			{
//				props.put(PublishInfo.WP_URL, wpurl);
//				props.put(PublishInfo.WP_APPNAME, mapprops.get(PublishInfo.WP_APPNAME));
//				props.put(PublishInfo.WP_TARGET, uri.toString());
//				props.put(PublishInfo.WP_USER, wpurl);
//				props.put(PublishInfo.WP_PASS, wpurl);
//			}
			
			final String url = (String)mapprops.get(PublishInfo.WP_URL);
			if(url!=null)
			{
				String name = (String)mapprops.get(PublishInfo.WP_APPNAME);
				String target = uri.toString();
				String user = (String)mapprops.get(PublishInfo.WP_USER);
				String pass = (String)mapprops.get(PublishInfo.WP_PASS);
				initWebProxyRefresh(url, name, target, user, pass, serviceid);
			}
			
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
	 *  Unpublish a service.
	 *  @param sid The service identifier.
	 */
	public abstract IFuture<Void> unpublishService(IServiceIdentifier sid);

	/**
	 * 
	 */
	public abstract void internalPublishService(URI uri, ResourceConfig rc, IServiceIdentifier sid, PublishInfo info);
	
//	/**
//	 *  Get or start an api to the http server.
//	 */
//	public HttpServer getHttpServer(URI uri)
//	{
//		HttpServer server = null;
//		
//		try
//		{
//			URI baseuri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
//			server = uriservers==null? null: uriservers.get(baseuri);
//			
//			if(server==null)
//			{
//				System.out.println("Starting new server: "+uri.getPath());
//				
//				server = GrizzlyHttpServerFactory.createHttpServer(uri);
//				server.start();
//				
//				if(uriservers==null)
//					uriservers = new HashMap<URI, HttpServer>();
//				uriservers.put(baseuri, server);
//			}
//		}
//		catch(RuntimeException e)
//		{
//			throw e;
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//		
//		return server;
//	}
	
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
	public abstract IFuture<Void> publishHMTLPage(String uri, String vhost, String html);
	
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
	 *  Publish resources via a rel jar path.
	 *  The resources are searched with respect to the
	 *  component classloader (todo: allow for specifiying RID).
	 */
	public abstract IFuture<Void> publishResources(URI uri, String path);
	
	/**
	 *  Publish file resources from the file system.
	 */
	public abstract IFuture<Void> publishExternal(URI uri, String rootpath);

	
	/**
	 *  Create a service proxy class.
	 *  @param service The Jadex service.
	 *  @param classloader The classloader.
	 *  @param type The web service interface type.
	 *  @return The proxy object.
	 */
	protected Class<?> createProxyClass(IServiceIdentifier serviceid, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops, List<RestMethodInfo> geninfos) throws Exception
	{
		Class<?> ret = null;

		Class<?> iface = serviceid.getServiceType().getType(classloader);
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
			
			// Buaaahhhhhh grizzly does not allow injection of httpservlet request
			CtField req = new CtField(SJavassist.getCtClass(org.glassfish.grizzly.http.server.Request.class, pool), "__greq", proxyclazz);
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, SJavassist.getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			req.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(req);
			
			req = new CtField(SJavassist.getCtClass(ContainerRequest.class, pool), "__creq", proxyclazz);
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, SJavassist.getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			req.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(req);
			
			req = new CtField(SJavassist.getCtClass(HttpServletRequest.class, pool), "__req", proxyclazz);
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(constpool, SJavassist.getCtClass(Context.class, pool));
			attr.addAnnotation(annot);
			req.getFieldInfo().addAttribute(attr);
			proxyclazz.addField(req);
			
			// if webproxy refresh is need add an init method that initializes refresh
			// problem: init annotation (Singleton/PostCreate) does not work
//			CtMethod init = SJavassist.getCtClass(AbstractRestServicePublishService.class, pool)
//				.getDeclaredMethod("initWebProxyRefresh");
//			CtMethod met = CtNewMethod.wrapped(CtClass.voidType, "init",
//				null, null, init, null, proxyclazz);
//			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
//			annot = new Annotation(constpool, SJavassist.getCtClass(Singleton.class, pool));
//			attr.addAnnotation(annot);
//			met.getMethodInfo().addAttribute(attr);
//			proxyclazz.addMethod(met);
			
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
//						if(ptypes[i].isArray())
//						{
//							mvals[i] = new ClassMemberValue("[B;", constpool);
//							System.out.println("2");
//						}
//						else
//						{
							// does not work for arrays currently (seems javassist bug) 
							mvals[i] = new ClassMemberValue(ptypes[i].getName(), constpool);
//						}
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
						
				int pcnt = rmi.getParameterTypes().length;
				Annotation[][] annos = new Annotation[pcnt][];
				
				List<List<Tuple2<String, Map<String, Object>>>> aninfs = rmi.getAnnotationInfo();
				if(aninfs!=null && !aninfs.isEmpty())
				{
					ConstPool cp = m.getMethodInfo().getConstPool();
					
					for(int k=0; k<annos.length; k++)
					{
						List<Tuple2<String, Map<String, Object>>> paninfs = aninfs.get(k);
						Annotation[] ans = new Annotation[paninfs.size()];
						annos[k] = ans;
						
						for(int l=0; l<paninfs.size(); l++)
						{
							Tuple2<String, Map<String, Object>> tup = paninfs.get(l);
							ans[l] = new Annotation(cp, SJavassist.getCtClass(tup.getFirstEntity(), pool));
							Map<String, Object> vals = tup.getSecondEntity();
							for(Map.Entry<String, Object> entry: vals.entrySet())
							{
								MemberValue mv = null;
								if(entry.getValue() instanceof String)
								{
									mv = new StringMemberValue((String)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Integer)
								{
									mv = new IntegerMemberValue((Integer)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Double)
								{
									mv = new DoubleMemberValue((Double)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Float)
								{
									mv = new FloatMemberValue((Float)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Boolean)
								{
									mv = new BooleanMemberValue((Boolean)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Short)
								{
									mv = new ShortMemberValue((Short)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Long)
								{
									mv = new LongMemberValue((Long)entry.getValue(), cp);
								}
								else if(entry.getValue() instanceof Character)
								{
									mv = new CharMemberValue((Character)entry.getValue(), cp);
								}
								
								// todo: support annotations with arrays and annotation types!?
								
								if(mv!=null)
								{
									ans[l].addMemberValue(entry.getKey(), mv);
								}
								else
								{
									System.out.println("Annotation member value currently not supported: "+entry.getValue());
								}
							}
						}
					}
				}
								
				// add @QueryParam if get
				// this means that each parameter of the method is automatically
				// mapped to arg0, arg1 etc of the request
				if(GET.class.equals(rmi.getRestType()))
				{
					if(pcnt>0)
					{
						ConstPool cp = m.getMethodInfo().getConstPool();
						for(int k=0; k<annos.length; k++)
						{
							Annotation[] ans = annos[k];
							boolean hasq = false;
							for(Annotation an: ans)
							{
								if(an.getTypeName().equals(QueryParam.class.getName()))
								{
									hasq = true;
									break;
								}
							}
							
							if(!hasq)
							{
								Annotation anno = new Annotation(cp, SJavassist.getCtClass(QueryParam.class, pool));
								anno.addMemberValue("value", new StringMemberValue("arg"+k, cp));
								Annotation[] newans = new Annotation[ans.length+1];
								System.arraycopy(ans, 0, newans, 0, ans.length);
								newans[newans.length-1] = anno;
								annos[k] = newans;
							}
						}
					}
				}
				// add @FormDataParam if post
				else if(POST.class.equals(rmi.getRestType()))
				{
					if(pcnt>0)
					{
						ConstPool cp = m.getMethodInfo().getConstPool();
						for(int k=0; k<annos.length; k++)
						{
							Annotation[] ans = annos[k];
							boolean hasq = false;
							for(Annotation an: ans)
							{
								if(an.getTypeName().equals(FormDataParam.class.getName()))
								{
									hasq = true;
									break;
								}
							}
							
							if(!hasq)
							{
								Annotation anno = new Annotation(cp, SJavassist.getCtClass(FormDataParam.class, pool));
								anno.addMemberValue("value", new StringMemberValue("arg"+k, cp));
								Annotation[] newans = new Annotation[ans.length+1];
								System.arraycopy(ans, 0, newans, 0, ans.length);
								newans[newans.length-1] = anno;
								annos[k] = newans;
							}
						}
					}
				}
				
				SJavassist.addMethodParameterAnnotation(m, annos, pool);
				
//				System.out.println("m: "+m.getName()+" "+SUtil.arrayToString(m.getParameterTypes()));
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
	 * 
	 */
//	public Object initWebProxyRefresh(Object[] args)
	public Void initWebProxyRefresh(final String url, final String name, final String target, 
		final String user, final String pass, final IServiceIdentifier sid)
	{
		if(url!=null && name!=null && target!=null)
		{
			System.out.println("Init web proxy refresh: "+url+" "+name+" "+target);
			final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
			Runnable run= new Runnable()
			{
				protected String cookie = null;
				public void run()
				{
					if(isPublished(sid))
					{
						if(cookie==null)
						{
							cookie = login(url, user, pass);
						}
						if(cookie!=null)
						{
							Integer lt = addMapping(url, name, target, cookie);
							if(lt==null)
							{
								System.out.println("Web proxy problems");
								ses.schedule(this, 2, TimeUnit.MINUTES);
							}
							else
							{
								cookie = null;
								long dur = (long)(lt.intValue()*1000*60*0.9);
								ses.schedule(this, dur, TimeUnit.MILLISECONDS);
							}
						}
					}
					else
					{
						System.out.println("Webproxy refresh ends.");
					}
				}
			};
			run.run();
		}
		return null;
	}
	
	/**
	 * 
	 */
	protected String login(String url, String user, String pass)
	{
		String ret = null;
		HttpURLConnection con = null;
		try 
		{
			if(url.indexOf("https")!=-1)
			{
				URL urlc = new URL(url+"/login?name="+user+"&pass="+pass);
				con = (HttpURLConnection)urlc.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Accept", "application/json");
				con.connect();
				if(HttpServletResponse.SC_OK==con.getResponseCode())
				{
					ret = con.getHeaderField("Set-Cookie");
//					Map<String, String> vals = parseHeader(ck, ",;");
//					ret = vals.get("JSESSIONID");
				}
			}
			else
			{
				URL urlc = new URL(url+"/login");
				con = (HttpURLConnection)urlc.openConnection();
				con.connect();
				
				String auth = con.getHeaderField("WWW-Authenticate");
				if(auth.startsWith("Digest "))
				{
					Map<String, String> vals = parseHeader(auth, ",");
					String realm = vals.get("realm");
					String nonce = vals.get("nonce");
					String ha1 = hex(digest(user+":"+realm+":"+pass));
					String ha2 = hex(digest(con.getRequestMethod()+":"+con.getURL().getPath()));
					String ha3 = hex(digest(ha1+":"+nonce+":"+ha2));
	
					StringBuilder sb = new StringBuilder();
					sb.append("Digest ");
					sb.append("username").append("=\"").append(user).append("\",");
					sb.append("realm").append("=\"").append(realm).append("\",");
					sb.append("nonce").append("=\"").append(nonce).append("\",");
					sb.append("uri").append("=\"").append(con.getURL().getPath()).append("\",");
					// sb.append("qop" ).append('=' ).append("auth" ).append(",");
					sb.append("response").append("=\"").append(ha3).append("\"");
					HttpURLConnection con2 = (HttpURLConnection)con.getURL().openConnection();
					con2.addRequestProperty("Authorization", sb.toString());
					con2.setRequestProperty("Accept", "application/json");
					if(HttpServletResponse.SC_OK==con2.getResponseCode())
					{
						ret = con2.getHeaderField("Set-Cookie");
//						vals = parseHeader(ck, ",;");
//						ret = vals.get("JSESSIONID");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected Integer addMapping(String url, String name, String target, String cookie)
	{
		Integer ret = null;
		HttpURLConnection con = null;
		try 
		{
			URL urlc = new URL(url+"/addMapping?name="+name+"&target="+target);
			con = (HttpURLConnection)urlc.openConnection();
			con.setRequestProperty("Accept", "application/json");
//			con.setRequestProperty("Cookie", "JSESSIONID="+sessionid);
			con.setRequestProperty("Cookie", cookie);
			con.connect();
			if(HttpServletResponse.SC_OK==con.getResponseCode())
			{
				// todo: fixme
				
//				JSONObject jo = (JSONObject)JSONValue.parse(con.getInputStream());
//				ret = (Integer)jo.get("leasetime");
//				if(ret==null)
//				{
//					ret = Integer.valueOf(0);
//				}
				ret = 30;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		return SInvokeHelper.invoke(params, sig, this);
		
//		Object ret = null;
//		
////		System.out.println("called invoke: "+sig+" "+Arrays.toString(params));
//		
//		try
//		{
//			// find own method
//			
//			Method[] ms = getClass().getDeclaredMethods();
//			Method method = null;
//			for(Method m: ms)
//			{
//				if(RestMethodInfo.buildSignature(m.getName(), m.getParameterTypes()).equals(sig))
//				{
//					method = m;
//					break;
//				}
//			}
//			if(method==null)
//			{
//				System.out.println("methods: "+Arrays.toString(ms));
//				throw new RuntimeException("No method '"+sig+"' on class: "+getClass());
//			}
//			
////			StackTraceElement[] s = Thread.currentThread().getStackTrace();
////			String name = s[2].getMethodName();
//			
////			System.out.println("name is: "+name);
//
////			for(int i=0;i<s.length; i++)
////			{
////				System.out.println(s[i].getMethodName());
////			}
////			String name = SReflect.getMethodName();
////			Method[] methods = SReflect.getMethods(getClass(), name);
////		    Method method = null;
////			if(methods.length>1)
////			{
////			    for(int i=0; i<methods.length && method==null; i++)
////			    {
////			    	Class<?>[] types = methods[i].getParameterTypes();
////			    	if(types.length==params.length)
////			    	{
////			    		// check param types
////			    		method = methods[i];
////			    	}
////			    }
////			}
////			else if(methods.length==1)
////			{
////				method = methods[0];
////			}
////			else
////			{
////				throw new RuntimeException("No method '"+name+"' on class: "+getClass());
////			}
////			System.out.println("call: "+this+" "+method+" "+SUtil.arrayToString(params)+" "+name);
//			
////			Request req = (Request)getClass().getDeclaredField("__greq").get(this);
//////			System.out.println("call: "+this+" "+method+" "+req);
////			for(String name: req.getHeaderNames())
////			{
////				System.out.println("header: "+name+": "+req.getHeader(name));
////			}
//			
//			// check if mappers are there
//			ResourceConfig rc = (ResourceConfig)getClass().getDeclaredField("__rc").get(this);
//			
////			Object service = rc.getProperty(JADEXSERVICE);
//			Object service = rc.getProperty("jadexservice");
////			System.out.println("jadex service is: "+service);
//
//			HttpServletRequest req = (HttpServletRequest)getClass().getDeclaredField("__req").get(this);
//			Request greq = (Request)getClass().getDeclaredField("__greq").get(this);
//			ContainerRequest creq = (ContainerRequest)getClass().getDeclaredField("__creq").get(this);
//
//			
//			Method targetmethod = null;
//			if(method.isAnnotationPresent(MethodMapper.class))
//			{
//				MethodMapper mm = method.getAnnotation(MethodMapper.class);
//				targetmethod = SReflect.getMethod(service.getClass(), mm.value(), mm.parameters());
//			}
//			else
//			{
//				String mname = method.getName();
//				if(mname.endsWith("XML"))
//					mname = mname.substring(0, mname.length()-3);
//				if(mname.endsWith("JSON"))
//					mname = mname.substring(0, mname.length()-4);
//				targetmethod = service.getClass().getMethod(mname, method.getParameterTypes());
//			}
//			
////			System.out.println("target: "+targetmethod);
//			
//			Object[] targetparams = params;
//			if(method.isAnnotationPresent(ParametersMapper.class))
//			{
////				System.out.println("foundmapper");
//				ParametersMapper mm = method.getAnnotation(ParametersMapper.class);
//				if(!mm.automapping())
//				{
//					Class<?> clazz = mm.value().clazz();
//					Object mapper;
//					if(!Object.class.equals(clazz))
//					{
//						mapper = clazz.newInstance();
//					}
//					else
//					{
//						mapper = SJavaParser.evaluateExpression(mm.value().value(), null);
//					}
//					if(mapper instanceof IValueMapper)
//						mapper = new DefaultParameterMapper((IValueMapper)mapper);
//					
//					targetparams = ((IParameterMapper)mapper).convertParameters(params, req!=null? req: greq);
//				}
//				else
//				{
//					// In case of GET autmap the query parameters
//					if(method.isAnnotationPresent(GET.class))
//					{
//	//					System.out.println("automapping detected");
//						Class<?>[] ts = targetmethod.getParameterTypes();
//						targetparams = new Object[ts.length];
//						if(ts.length==1)
//						{
//							if(SReflect.isSupertype(ts[0], Map.class))
//							{
//								UriInfo ui = (UriInfo)getClass().getDeclaredField("__ui").get(this);
//								MultivaluedMap<String, String> vals = ui.getQueryParameters();
//								targetparams[0] = SInvokeHelper.convertMultiMap(vals);
//							}
//							else if(SReflect.isSupertype(ts[0], MultivaluedMap.class))
//							{
//								UriInfo ui = (UriInfo)getClass().getDeclaredField("__ui").get(this);
//								targetparams[0] = SInvokeHelper.convertMultiMap(ui.getQueryParameters());
//							}
//						}
//					}
//					else //if(method.isAnnotationPresent(POST.class))
//					{
//						Class<?>[] ts = targetmethod.getParameterTypes();
//						targetparams = new Object[ts.length];
////						System.out.println("automapping detected: "+SUtil.arrayToString(ts));
//						if(ts.length==1)
//						{
//							if(SReflect.isSupertype(ts[0], Map.class))
//							{
//								if(greq!=null)
//								{
////									SInvokeHelper.debug(greq);
//									// Hack to make grizzly allow parameter parsing
//									// Jersey calls getInputStream() hindering grizzly parsing params
//									try
//									{
//										Request r = (Request)greq;
//										Field f = r.getClass().getDeclaredField("usingInputStream");
//										f.setAccessible(true);
//										f.set(r, Boolean.FALSE);
////										System.out.println("params: "+r.getParameterNames());
//									}
//									catch(Exception e)
//									{
//										e.printStackTrace();
//									}
//									targetparams[0] = SInvokeHelper.convertMultiMap(greq.getParameterMap());
//								}
//								else if(req!=null)
//								{
//									targetparams[0] = SInvokeHelper.convertMultiMap(req.getParameterMap());
//								}
//							}
//							else if(SReflect.isSupertype(ts[0], MultivaluedMap.class))
//							{
//								targetparams[0] = SInvokeHelper.convertToMultiMap(req.getParameterMap());
//							}
//						}
//					}
//				}
//			}
//	
////			System.out.println("method: "+method.getName()+" "+method.getDeclaringClass().getName());
////			System.out.println("targetparams: "+SUtil.arrayToString(targetparams));
////			System.out.println("call: "+targetmethod.getName()+" paramtypes: "+SUtil.arrayToString(targetmethod.getParameterTypes())+" on "+service+" "+Arrays.toString(targetparams));
////			
//			ret = targetmethod.invoke(service, targetparams);
//			if(ret instanceof IFuture)
//			{
//				ret = ((IFuture<?>)ret).get(new ThreadSuspendable());
//			}
//			
//			if(method.isAnnotationPresent(ResultMapper.class))
//			{
//				ResultMapper mm = method.getAnnotation(ResultMapper.class);
//				Class<?> clazz = mm.value().clazz();
//				IValueMapper mapper;
////				System.out.println("res mapper: "+clazz);
//				if(!Object.class.equals(clazz))
//				{
//					mapper = (IValueMapper)clazz.newInstance();
//				}
//				else
//				{
//					mapper = (IValueMapper)SJavaParser.evaluateExpression(mm.value().value(), null);
//				}
//				
//				ret = mapper.convertValue(ret);
//			}
//		}
//		catch(Throwable t)
//		{
//			throw new RuntimeException(t);
//		}
//		
//		return ret;
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
				Scanner sc = null;
				try
				{
					InputStream is = SUtil.getResource0("jadex/extension/rs/publish/functions.js", 
						Thread.currentThread().getContextClassLoader());
					sc = new Scanner(is);
					functionsjs = sc.useDelimiter("\\A").next();
					fjs.set(this, functionsjs);
//					System.out.println(functionsjs);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				finally
				{
					if(sc!=null)
					{
						sc.close();
					}
				}
			}
			
			Field scss = getClass().getDeclaredField("__stylecss");
			String stylecss = (String)scss.get(this);
			if(stylecss==null)
			{
				Scanner sc = null;
				try
				{
					InputStream is = SUtil.getResource0("jadex/extension/rs/publish/style.css", 
						Thread.currentThread().getContextClassLoader());
					sc = new Scanner(is);
					stylecss = sc.useDelimiter("\\A").next();
					
					String	stripes	= SUtil.loadBinary("jadex/extension/rs/publish/jadex_stripes.png");
					stylecss	= stylecss.replace("$stripes", stripes);
					
					scss.set(this, stylecss);
//					System.out.println(functionsjs);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				finally
				{
					if(sc!=null)
					{
						sc.close();
					}
				}
			}
			
			ret.append("<html>");
			ret.append("\n");
			ret.append("<head>");
			ret.append("\n");
			ret.append(stylecss);
			ret.append("\n");
			ret.append(functionsjs);
			ret.append("\n");
	//		ret.append("<script src=\"functions.js\" type=\"text/javascript\"/>");
			ret.append("</head>");
			ret.append("\n");
			ret.append("<body>");
			ret.append("\n");
			
			ret.append("<div class=\"header\">");
			ret.append("\n");
			ret.append("<h1>");//Service Info for: ");
			String ifacename = ((IService)service).getServiceIdentifier().getServiceType().getTypeName();
			ret.append(SReflect.getUnqualifiedTypeName(ifacename));
			ret.append("</h1>");
			ret.append("\n");
			ret.append("</div>");
			ret.append("\n");

			ret.append("<div class=\"middle\">");
			ret.append("\n");
			
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
						ret.append("\n");
						
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
						ret.append("\n");
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
						ret.append("\n");

						UriBuilder ub = ui.getBaseUriBuilder();
						if(path!=null)
							ub.path(path.value());
//						System.out.println("path1: "+path);
						String link = ub.build((Map)Collections.EMPTY_MAP).toString();
//						System.out.println("path2: "+path);
						
						if(ptypes.length>0)
						{
							ret.append("<div class=\"servicelink\">");
							ret.append(link);
							ret.append("</div>");
							ret.append("\n");
							
							// For post set the media type of the arguments.
							ret.append("<form class=\"arguments\" action=\"").append(link).append("\" method=\"")
								.append(resttype.toLowerCase()).append("\" enctype=\"multipart/form-data\" ");
							
							if(restmethod.equals(POST.class))
								ret.append("onSubmit=\"return extract(this)\"");
							ret.append(">");
							ret.append("\n");
							
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
							ret.append("\n");
							
							ret.append("<input type=\"submit\" value=\"invoke\"/>");
							ret.append("</form>");
							ret.append("\n");
						}
						else
						{
							ret.append("<div class=\"servicelink\">");
							ret.append("<a href=\"").append(link).append("\">").append(link).append("</a>");
							ret.append("</div>");
							ret.append("\n");
						}
						
						ret.append("</div>");
						ret.append("\n");
					}
				}
			}
			
			ret.append("</div>");
			ret.append("\n");
			
			ret.append("<div class=\"powered\"> <span class=\"powered\">powered by</span> <span class=\"jadex\">Jadex Active Components</span> <a class=\"jadexurl\" href=\"http://www.activecomponents.org\">http://www.activecomponents.org</a> </div>\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		ret.append("</body>\n</html>\n");

		return ret.toString();
	}
	
	/**
	 *  Test if a service is published.
	 */
	public abstract boolean isPublished(IServiceIdentifier sid);

	/**
	 *  Convert to hex value.
	 */ 
	public static String hex(byte[] data)
	{
		return SUtil.hex(data, false);
	}
	
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] digest(String input)
	{
		return digest(input.getBytes());
	}
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] digest(byte[] input)
	{
//		System.out.println("build digest: "+timestamp+" "+secret);
		try
		{
			MessageDigest	md	= MessageDigest.getInstance("MD5");
			byte[]	output	= md.digest(input);
			return output;
		}
		catch(NoSuchAlgorithmException e)
		{
			// Shouldn't happen?
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Convert header to key value pairs.
	 */
	protected Map<String, String> parseHeader(String header, String delims)
	{
		HashMap<String, String> values = new HashMap<String, String>();
		StringTokenizer stok = new StringTokenizer(header, delims);
		while(stok.hasMoreTokens())
		{
			String keyval = stok.nextToken();
			if(keyval.contains("="))
			{
				String key = keyval.substring(0, keyval.indexOf("="));
				String value = keyval.substring(keyval.indexOf("=") + 1);
				values.put(key.trim(), value.replaceAll("\"", "").trim());
			}
		}
		return values;
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
