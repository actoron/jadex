package jadex.tools.web.starter;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Starter web jcc plugin.
 */
@ProvidedServices(
{
	@ProvidedService(name="starterweb", type=IJCCStarterService.class)
})
@Agent(autostart=Boolean3.TRUE)
public class JCCStarterPluginAgent implements IJCCStarterService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The plugin component string. */
	protected String component;
	
	/**
	 *  Get the plugin component (html).
	 *  @return The plugin code.
	 */
	public IFuture<String> getPluginComponent()
	{
		if(component==null)
			component = internalLoadResource("jadex/tools/web/starter/starter.tag");
		
		return new Future<String>(component);
	}
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("starter");
	}
	
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 */
	public IFuture<Collection<String[]>> getComponentModels()
	{
		ILibraryService ls = agent.getLocalService(ILibraryService.class);
		URL[] urls = ls.getAllURLs().get().toArray(new URL[0]);
		
//		URL[] urls = PlatformAgent.getClasspathUrls(this.getClass().getClassLoader());
//		System.out.println("URLs1: "+Arrays.toString(urls));
		// Remove JVM jars
		urls = SUtil.removeSystemUrls(urls);
		
		Set<SClassReader.ClassFileInfo> cis = SReflect.scanForClassFileInfos(urls, null, new IFilter<SClassReader.ClassFileInfo>()
		{
			public boolean filter(SClassReader.ClassFileInfo ci)
			{
				boolean ret = false;
				AnnotationInfo ai = ci.getClassInfo().getAnnotation(Agent.class.getName());
				if(ai!=null)
					ret = true;
				return ret;
			}
		});
		
		// Collect filenames of models to load the models without knowing the rid (can then be extracted)
		List<String[]> res = cis.stream().map(a -> new String[]{a.getFilename(), a.getClassInfo().getClassName()}).collect(Collectors.toList());
				
		//System.out.println("Models found: "+res);
		return new Future<Collection<String[]>>(res);
	}
	
	/**
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(String filename)
	{
//		System.out.println("webjcc start: "+filename);
		
		IExternalAccess comp = agent.createComponent(new CreationInfo().setFilename(filename)).get();
		return new Future<IComponentIdentifier>(comp.getId());
	}
	
	/**
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(CreationInfo ci)
	{
		System.out.println("webjcc start: "+ci+", "+Thread.currentThread());
		
		IExternalAccess comp = agent.getExternalAccess(agent.getId().getRoot()).createComponent(ci).get();
		return new Future<IComponentIdentifier>(comp.getId());
	}
	
	/**
	 *  Load a component model.
	 *  @param filename The filename.
	 *  @return The component model.
	 */
	public IFuture<IModelInfo> loadComponentModel(String filename)
	{
		return SComponentFactory.loadModel(agent.getExternalAccess(), filename, null);
	}
	
	/**
	 *  Load a string-based ressource (style or js).
	 *  @param filename The filename.
	 *  @return The text from the file.
	 */
	public IFuture<Response> loadResource(String filename)
	{
		String res = internalLoadResource(filename);
		String mt = SUtil.guessContentTypeByFilename(filename);
		Response r = Response.ok(res).header("Content-Type", mt).build();
		return new Future<Response>(r);
	}
	
	/**
	 *  Load a resource per resource name.
	 */
	public String internalLoadResource(String name)
	{
		String ret;
		
		Scanner sc = null;
		try
		{
			InputStream is = SUtil.getResource0(name, agent.getClassLoader());
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
			
			System.out.println("loading: "+name+" "+ret.length());
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
		
		return ret;
	}
}
