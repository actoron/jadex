package jadex.micro.examples.helpline;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Helpline micro agent. 
 */
@Description("This agent offers a helpline for getting information about missing persons.")
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class),
	@RequiredService(name="remotehelplineservices", type=IHelpline.class, scope=ServiceScope.NETWORK), //multiple=true,
	@RequiredService(name="localhelplineservices", type=IHelpline.class, scope=ServiceScope.PLATFORM) //multiple=true,
})
@ProvidedServices(@ProvidedService(type=IHelpline.class, implementation=@Implementation(HelplineService.class), scope=ServiceScope.NETWORK))
@GuiClass(HelplineViewerPanel.class)
@Agent
public class HelplineAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The map of information. */
	protected MultiCollection<String, InformationEntry> infos;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	//@AgentCreated
	@OnInit
	public IFuture<Void>	agentCreated()
	{
//		this.infos = new MultiCollection(new HashMap(), TreeSet.class);
		this.infos = new MultiCollection<String, InformationEntry>();
		Object ini = agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("infos");
		if(ini!=null && SReflect.isIterable(ini))
		{
			for(Iterator it=SReflect.getIterator(ini); it.hasNext(); )
			{
				InformationEntry ie = (InformationEntry)it.next();
				infos.add(ie.getName(), ie);
			}
		}
//		addService(new HelplineService(this));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				HelplinePanel.createHelplineGui((IExternalAccess)agent.getExternalAccess());
			}
		});
		return IFuture.DONE;
	}
		
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(final String name, final String info)
	{
//		getServiceProvider().searchService( new ServiceQuery<>( IClockService.class))
		IFuture<IClockService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("clockservice");
//			.addResultListener(createResultListener(new DefaultResultListener()
		fut.addResultListener(new DefaultResultListener<IClockService>() // not needed as decoupled service
		{
			public void resultAvailable(IClockService cs)
			{
				infos.add(name, new InformationEntry(name, info, cs.getTime()));
			}
		});
	}
	
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public Collection<InformationEntry> getInformation(String name)
	{
		Collection<InformationEntry> ret	= infos.get(name); 
		return ret!=null ? ret : Collections.EMPTY_LIST;
	}

	/**
	 *  Get the agent.
	 *  @return The agent
	 */
	public IInternalAccess getAgent()
	{
		return agent;
	}
	
	//-------- static methods --------

//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent offers a helpline for getting information about missing persons.", null, 
//			new IArgument[]{new Argument("infos", "Initial information records.", "InformationEntry[]")}
//			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.helpline.HelplineViewerPanel"}),
//			new RequiredServiceInfo[]{new RequiredServiceInfo("clockservice", IClockService.class, ServiceScope.PLATFORM),
//			new RequiredServiceInfo("remotehelplineservices", IHelpline.class, true, true, ServiceScope.GLOBAL),
//			new RequiredServiceInfo("localhelplineservices", IHelpline.class, true, true, ServiceScope.PLATFORM)}, 
//			new ProvidedServiceInfo[]{new ProvidedServiceInfo(IHelpline.class)});
//	}
}
