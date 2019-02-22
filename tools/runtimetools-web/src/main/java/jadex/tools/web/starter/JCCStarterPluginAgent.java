package jadex.tools.web.starter;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
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
	@Agent
	protected IInternalAccess agent;
	
	protected String component;
	
	/**
	 *  Get the plugin component (html).
	 *  @return The plugin code.
	 */
	public IFuture<String> getPluginComponent()
	{
		if(component==null)
			component = loadTag("jadex/tools/web/starter/starter.tag");
		
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
	public IFuture<Collection<String>> getComponentModels()
	{
		ILibraryService ls = agent.getLocalService(ILibraryService.class);
		URL[] urls = ls.getAllURLs().get().toArray(new URL[0]);
		
//		URL[] urls = PlatformAgent.getClasspathUrls(this.getClass().getClassLoader());
//		System.out.println("URLs1: "+Arrays.toString(urls));
		// Remove JVM jars
		urls = SUtil.removeSystemUrls(urls);
		
		Set<SClassReader.ClassInfo> cis = SReflect.scanForClassInfos(urls, null, new IFilter<SClassReader.ClassInfo>()
		{
			public boolean filter(SClassReader.ClassInfo ci)
			{
				boolean ret = false;
				AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
				if(ai!=null)
					ret = true;
				return ret;
			}
		});
		
		List<String> res = cis.stream().map(a -> a.getClassName()).collect(Collectors.toList());
				
		//System.out.println("Models found: "+res);
		return new Future<Collection<String>>(res);
	}
	
	/**
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(ClassInfo model)
	{
		IExternalAccess comp = agent.createComponent(new CreationInfo().setFilename(model.getTypeName()+".class")).get();
		return new Future<IComponentIdentifier>(comp.getId());
	}
	
	/**
	 *  Load a tag html code per resource name.
	 */
	public String loadTag(String name)
	{
		String ret;
		
		Scanner sc = null;
		try
		{
			InputStream is = SUtil.getResource0(name, agent.getClassLoader());
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
			
	//		System.out.println(ret);
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
