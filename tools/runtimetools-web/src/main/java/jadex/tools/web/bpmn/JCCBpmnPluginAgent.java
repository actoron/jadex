package jadex.tools.web.bpmn;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Security web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="bpmnweb", type=IJCCBpmnService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCBpmnPluginAgent extends JCCPluginAgent implements IJCCBpmnService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	@AgentArgument
	protected File wfstore = new File("workflows");
	
	/** Flag if the workflow store has been added to the classpath/library service */
	protected boolean wfstoreInClasspath = false;
	
	/** Cache of known service interfaces. */
	protected String[] serviceinterfacecache;
	
	/** Libservice listener */
	protected ILibraryServiceListener liblis = new ILibraryServiceListener()
	{
		public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid)
		{
			serviceinterfacecache = null;
			return IFuture.DONE;
		}
		
		public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean removable)
		{
			serviceinterfacecache = null;
			return IFuture.DONE;
		}
	};
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("BPMN");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(90);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/bpmn/bpmn.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return new Future<>((byte[])null);
		//return loadResource("jadex/tools/web/chat/chat.png");
	}
	
	/**
	 * Initializes the agent.
	 * 
	 * @return Null, once done.
	 */
	@OnInit
	public IFuture<Void> init()
	{
		ILibraryService libserv = agent.getLocalService(ILibraryService.class);
		libserv.addLibraryServiceListener(liblis);
		
		return IFuture.DONE;
	}
	
	/**
	 * Stops the agent.
	 * 
	 * @return Null, once done.
	 */
	@OnEnd
	public IFuture<Void> stop()
	{
		ILibraryService libserv = agent.getLocalService(ILibraryService.class);
		libserv.removeLibraryServiceListener(liblis);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get all available BPMN models.
	 *  
	 *  @return The BPMN models.
	 */
	public IFuture<String[]> getBpmnModels()
	{
		if (hasWfStore())
		{
			URL wfstoreurl = (URL) SUtil.noExRet(() -> {return getWfStore().toURI().toURL();});
			String[] models = SReflect.scanForFiles(new URL[] { wfstoreurl }, new IFilter<Object>()
			{
				public boolean filter(Object obj)
				{
					if (obj instanceof File)
					{
						return ((File) obj).getName().endsWith(".bpmn");
					}
					return false;
				};
			});
			
			Path wfpath = (Path) SUtil.noExRet(() -> getWfStore().getCanonicalFile().toPath());
			models = Arrays.stream(models).map((s) -> {
				Path p = (Path) SUtil.noExRet(() -> (new File(s)).getCanonicalFile().toPath());
				p = wfpath.relativize(p);
				return p.toString().replace(File.separatorChar, '.');
			}).toArray(String[]::new);
			
			Arrays.stream(models).forEach((s) -> System.out.println("MODEL " + s));
			return new Future<>(models);
		}
		else
		{
			return new Future<>(new String[0]);
		}
	}
	
	/**
	 *  Get all available Jadex service interfaces.
	 *  @return List of Jadex service interfaces.
	 */
	public IFuture<String[]> getServiceInterfaces()
	{
		if (serviceinterfacecache == null)
		{
			ILibraryService libserv = agent.getLocalService(ILibraryService.class);
			ClassLoader rootloader = libserv.getClassLoader(null).get();
			
			Set<SClassReader.ClassInfo> cis = SReflect.scanForClassInfos(rootloader, null, new IFilter<SClassReader.ClassInfo>()
			{
				public boolean filter(ClassInfo ci)
				{
					if (ci.isInterface())
					{
						if (ci.hasAnnotation("jadex.bridge.service.annotation.Service"))
						{
							return true;
						}
					}
					return false;
				}
			}, true);
			
			serviceinterfacecache = cis.stream().map((ci) -> ci.getClassName()).toArray(String[]::new);
		}
		
		return new Future<>(serviceinterfacecache);
	}
	
	protected boolean hasWfStore()
	{
		return wfstore.exists();
	}
	
	protected File getWfStore()
	{
		if (!wfstore.exists())
		{
			if (!wfstore.mkdir())
			{
				SUtil.noEx(() -> {
					agent.getLogger().warning("BPMN: Unable to create workflow directory: " + wfstore.getCanonicalPath());
				});
				
			}
		}
		
		if (!wfstore.isDirectory())
		{
			SUtil.noEx(() -> {
				agent.getLogger().warning("BPMN: Workflow storage exists but not a directory: " + wfstore.getCanonicalPath());
			});
		}
		
		if (!wfstoreInClasspath)
		{
			URL wfstoreurl = (URL) SUtil.noExRet(() -> {return wfstore.toURI().toURL();});
			
			ILibraryService libser = agent.getLocalService(ILibraryService.class);
			libser.addTopLevelURL(wfstoreurl).get();
			wfstoreInClasspath = true;
		}
		
		return wfstore;
	}
}
