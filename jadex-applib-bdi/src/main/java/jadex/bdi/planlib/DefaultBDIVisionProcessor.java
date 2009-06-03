package jadex.bdi.planlib;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 *  Default bdi agent vision processor.
 *  Updates the agent's beliefsets according to the percepts of new/disappeared waste.
 */
public class DefaultBDIVisionProcessor extends SimplePropertyObject implements IPerceptProcessor
{
	//-------- constants --------

	/** The percept types property. */
	public static String PROPERTY_PERCEPTTYPES = "percepttypes";
	
	
	/** The add action. */
	public static String ADD = "add";
	
	/** The remove action. */
	public static String REMOVE = "remove";
	
	/** The set action. */
	public static String SET = "set";
	
	//-------- attributes --------
	
	/** The percepttypes infos. */
	protected Map percepttypes;
	
	//-------- methods --------
	
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 */
	public void processPercept(final ISpace space, final String type, final Object percept, IAgentIdentifier agent)
	{
		IAMS ams = (IAMS)((IApplicationContext)space.getContext()).getPlatform().getService(IAMS.class);
		ams.getExternalAccess(agent, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
//				exception.printStackTrace();
			}
			public void resultAvailable(Object result)
			{
				try
				{
					IExternalAccess exta = (IExternalAccess)result;
					
					String[] metainfo = getMetaInfo(type);
					
					if(ADD.equals(metainfo[0]))
					{
						IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfo[1]);
						if(!belset.containsFact(percept))
						{
							belset.addFact(percept);
							System.out.println("added: "+percept+" to: "+belset);
						}
					}
					else if(REMOVE.equals(metainfo[0]))
					{
						IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfo[1]);
						if(belset.containsFact(percept))
						{
							belset.removeFact(percept);
							System.out.println("removed: "+percept+" from: "+belset);
						}
					}
					else if(SET.equals(metainfo[0]))
					{
						IBelief bel = exta.getBeliefbase().getBelief(metainfo[1]);
						bel.setFact(percept);
					}
				}
				catch(Exception e)
				{
					// try catch for the case that the agent is not yet inited and
					// the belief value is not accessible
				}
			}
		});
	}
	
	/**
	 *  Get the percept types defined for this generator.
	 *  @return The percept types.
	 */
	protected Object[] getPerceptTypes()
	{
		return (Object[])getProperty(PROPERTY_PERCEPTTYPES);
	}
	
	/**
	 * 
	 */
	protected String[] getMetaInfo(String percepttype)
	{
		if(percepttypes==null)
		{
			this.percepttypes = new HashMap();
			Object[] percepttypes = getPerceptTypes();
			for(int i=0; i<percepttypes.length; i++)
			{
				String[] per = (String[])percepttypes[i];
				this.percepttypes.put(per[0], new String[]{per[1], per[2]});
			}
		}
		return (String[])percepttypes.get(percepttype);
	}
}