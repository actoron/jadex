package jadex.micro.examples.helpline;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 
 */
public class HelplineAgent extends MicroAgent
{
	/** The map of information. */
	protected MultiCollection infos;
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		this.infos = new MultiCollection(new HashMap(), TreeSet.class);
		addService(new HelplineService(getExternalAccess()));
	}
	
	/**
	 *  Get all information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IFuture getInformation(final String name)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IHelpline.class, true, true)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null)
				{
					Collection coll = (Collection)result;
					CollectionResultListener crl = new CollectionResultListener(
						coll.size(), true, new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							if(result!=null)
							{
								Collection tmp = (Collection)result;
								Iterator it = tmp.iterator();
								TreeSet all = (TreeSet)it.next();
								for(; it.hasNext(); )
								{
									TreeSet part = (TreeSet)it.next();
									all.addAll(part);
								}
								ret.setResult(all);
							}
							else
							{
								ret.setResult(null);
							}
						}
					});
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						IHelpline hl = (IHelpline)it.next();
						hl.getLocalInformation(name).addResultListener(crl);
					}
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		}));
			
		return ret;
	}
	
	/**
	 *  Add an information about a person.
	 *  @param name The person's name.
	 *  @param info The information.
	 */
	public void addInformation(final String name, final String info)
	{
		SServiceProvider.getService(getServiceProvider(), IClockService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IClockService cs = (IClockService)result;
				infos.put(name, new InformationEntry(name, info, cs.getTime()));
			}
		}));
	}
	
	/**
	 *  Get all locally stored information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public TreeSet getLocalInformation(String name)
	{
		return (TreeSet)infos.get(name);
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent offers a helpline for getting information about missing persons.", null, 
			null, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.micro.examples.helpline.HelplineViewerPanel"}));
	}

}
