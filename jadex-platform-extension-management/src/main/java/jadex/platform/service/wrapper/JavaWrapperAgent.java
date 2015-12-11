package jadex.platform.service.wrapper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.jar.JarFile;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Wrapper for executing Java programs.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IJavaWrapperService.class,
	implementation=@Implementation(expression="$pojoagent")))
@RequiredServices({
	@RequiredService(name="tpservice", type=IThreadPoolService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)),
	@RequiredService(name="libservice", type=ILibraryService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Service
public class JavaWrapperAgent	implements	IJavaWrapperService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The thread pool service. */
	@AgentService
	protected IThreadPoolService	tpservice;
	
	/** The library service. */
	@AgentService
	protected ILibraryService	libservice;
	
	//-------- IJavaWrapperService interface --------
	
	/**
	 *  Execute a plain Java program
	 *  as given by its main class.
	 *  @param clazz	The class to be executed as Java program.
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution (result: null)
	 *    or failure (exception).
	 */
	public IFuture<Void>	executeJava(Class<?> clazz, String[] args)
	{
		final Future<Void>	ret	= new Future<Void>();
		final String[]	fargs	= args!=null ? args : SUtil.EMPTY_STRING_ARRAY;
		
		try
		{
			final Method	main	= clazz.getMethod("main", new Class[]{String[].class});
			
			// Todo: allow termination of execution from outside
			tpservice.execute(new Runnable()
			{
				public void run()
				{
					try
					{
						main.invoke(null, new Object[]{fargs});
						ret.setResult(null);
					}
					catch(InvocationTargetException e)
					{
						ret.setException(e.getTargetException() instanceof Exception
							? (Exception)e.getTargetException()
							: new RuntimeException(e.getTargetException()));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Execute a plain Java program from a jar
	 *  as given by a file name.
	 *  Uses the main class name as specified in the manifest.
	 *  @param jarfile	File name of a jar file.
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution (result: null)
	 *    or failure (exception).
	 */
	public IFuture<Void>	executeJava(String jarfile, String[] args)
	{
		IFuture<Void>	ret;
		try
		{
			File	file	= new File(jarfile).getCanonicalFile();
			IResourceIdentifier	rid	= new ResourceIdentifier(
				new LocalResourceIdentifier(agent.getComponentIdentifier().getRoot(), file.toURI().toURL()), null);
			ret	= executeJava(rid, args);
		}
		catch(Exception e)
		{
			ret	= new Future<Void>(e);
		}
		return ret;
	}
	
	/**
	 *  Execute a plain Java program from a jar
	 *  as given by a resource identifier.
	 *  Uses the main class name as specified in the manifest.
	 *  @param rid	The resource identifier for the jar
	 *    (global rid for maven artifact id, local rid for local file url).
	 *  @param args	The arguments to the main method.
	 *  @return A future indication successful execution (result: null)
	 *    or failure (exception).
	 */
	public IFuture<Void>	executeJava(IResourceIdentifier rid, final String[] args)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		libservice.addResourceIdentifier(null, rid, false)
			.addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
		{
			public void customResultAvailable(final IResourceIdentifier rid)
			{
				if(rid.getLocalIdentifier()==null)
				{
					ret.setException(new RuntimeException("Cannot resolve: "+rid));
				}
				else
				{
					libservice.getClassLoader(rid).addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
					{
						public void customResultAvailable(ClassLoader cl)
						{
							try
							{
								JarFile	jar	= new JarFile(SUtil.getFile(SUtil.toURL(rid.getLocalIdentifier().getUri())));
								String	main	= jar.getManifest().getMainAttributes().getValue("Main-Class");
								jar.close();
								Class<?>	clazz	= SReflect.classForName(main, cl);
								executeJava(clazz, args)
									.addResultListener(new DelegationResultListener<Void>(ret));
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
					});
				}
			}
		});
		
		return ret;
	}
}
