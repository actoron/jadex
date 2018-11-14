package jadex.micro.examples.helplinemega;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Helpline component for a single person of interest. 
 */
@Description("This agent provides a helpline service for managing information about a missing person.")
@ProvidedServices(@ProvidedService(type=IHelpline.class, scope=RequiredServiceInfo.SCOPE_NETWORK))
@Agent
public class HelplineAgent	implements IHelpline
{
	// for debugging
	static AtomicInteger	ai	= new AtomicInteger(1);
	public HelplineAgent()
	{
		int num	= ai.incrementAndGet();
		if(num%100==0)
			System.out.println("###### helplines: "+num);
	}
	
	
	//-------- attributes --------
	
	/** The name of the person of interest. */
	@AgentArgument
	protected String	person;
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The information about the person. */
	protected Set<InformationEntry> infos	= new LinkedHashSet<InformationEntry>();
	
	//-------- IHelpline methods --------
	
	/**
	 *  Add new information about a person, e.g. from GUI.
	 *  @param info The information text.
	 */
	public IFuture<Void>	addInformation(String info)
	{
		// Create and store information record.
		InformationEntry	entry	= new InformationEntry(person, info, agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
		infos.add(entry);

		// forward information to other interested services.
		postInformation(entry);
		return IFuture.DONE;
	}
	
	/**
	 *  Receive all locally stored information about a person (i.e. pull).
	 *  @return Future that contains all currently known information in a set of records.
	 */
	public IFuture<Set<InformationEntry>>	getInformation()
	{
		return new Future<Set<InformationEntry>>(infos);
	}
	
	/**
	 *  Forward existing information to this service, e.g. from other helpline nodes (i.e. push).
	 *  @param entry The information record.
	 */
	public IFuture<Void>	forwardInformation(InformationEntry entry)
	{
		System.out.println("Helpline "+agent+" received: "+entry);
		infos.add(entry);	// only new information is added due to set.
		return IFuture.DONE;
	}
	
	/**
	 *  Search for information about a person in the network (i.e. pull).
	 *  The information is stored locally.
	 *  @return All information that can currently be found.
	 */
	public IIntermediateFuture<InformationEntry>	searchInformation()
	{
		final IntermediateFuture<InformationEntry>	ret	= new IntermediateFuture<InformationEntry>();
		for(InformationEntry entry: infos)
		{
			ret.addIntermediateResult(entry);
		}
		
		agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK).setServiceTags(person)).
			addResultListener(new IntermediateDefaultResultListener<IHelpline>()
		{
			boolean finished	= false;
			int cnt	= 0;
			
			@Override
			public void intermediateResultAvailable(IHelpline helpline)
			{
				cnt++;
				helpline.getInformation().addResultListener(new IResultListener<Set<InformationEntry>>()
				{
					@Override
					public void exceptionOccurred(Exception exception)
					{
						cnt--;
						checkFinished();
					}
					
					@Override
					public void resultAvailable(Set<InformationEntry> results)
					{
						for(InformationEntry entry: results)
						{
							// Add entry and notify if new.
							if(infos.add(entry))
							{
								ret.addIntermediateResult(entry);
							}
						}

						cnt--;
						checkFinished();
					}
				});
			}
			
			@Override
			public void finished()
			{
				finished	= true;
				checkFinished();
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				finished	= true;
				checkFinished();
			}
			
			protected void checkFinished()
			{
				if(finished && cnt==0)
				{
					ret.setFinished();
				}
			}
		});
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Asynchronously post new information to interested parties.
	 *  @param entry	The new information.
	 */
	protected void	postInformation(final InformationEntry entry)
	{
		// Todo: use queue for forwarding, if information frequency is high?
		agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK).setServiceTags(person)).
					addResultListener(new IntermediateDefaultResultListener<IHelpline>()
				{
					@Override
					public void intermediateResultAvailable(IHelpline helpline)
					{
						helpline.forwardInformation(entry);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						// ignore but print message
						System.out.println("Problem during forwarding of information about "+person+": "+exception);
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
}
