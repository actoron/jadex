package jadex.tools.web.bpmn;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
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
	
	@OnInit
	public IFuture<Void> agentStart()
	{
		Future<Void> ret = new Future<>();
		
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
		
		URL wfstoreurl = (URL) SUtil.noExRet(() -> {return wfstore.toURI().toURL();});
		
		ILibraryService libser = agent.getLocalService(ILibraryService.class);
		libser.addTopLevelURL(wfstoreurl).addResultListener(new DelegationResultListener<>(ret));
		
		return ret;
	}
	
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
	 *  Get all available BPMN models.
	 *  
	 *  @return The BPMN models.
	 */
	public IFuture<String[]> getBpmnModels()
	{
		URL wfstoreurl = (URL) SUtil.noExRet(() -> {return wfstore.toURI().toURL();});
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
		
		Path wfpath = (Path) SUtil.noExRet(() -> wfstore.getCanonicalFile().toPath());
		models = Arrays.stream(models).map((s) -> {
			Path p = (Path) SUtil.noExRet(() -> (new File(s)).getCanonicalFile().toPath());
			p = wfpath.relativize(p);
			return p.toString().replace(File.separatorChar, '.');
		}).toArray(String[]::new);
		
		Arrays.stream(models).forEach((s) -> System.out.println("MODEL " + s));
		return new Future<>(models);
	}
}
