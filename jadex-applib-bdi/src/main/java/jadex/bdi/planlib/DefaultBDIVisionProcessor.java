package jadex.bdi.planlib;

import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	/** The remove_outdated action (checks all entries in the belief set, if they should be seen, but are no longer there). */
	public static String REMOVE_OUTDATED = "remove_outdated";
	
	/** The set action. */
	public static String SET = "set";
	
	/** The maxrange property. */
	public static String PROPERTY_MAXRANGE = "range";

	/** The maxrange property. */
	public static String PROPERTY_RANGE = "range_property";

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
	 *  @param agent The avatar of the agent (if any).
	 */
	public void processPercept(final ISpace space, final String type, final Object percept, IAgentIdentifier agent, final ISpaceObject avatar)
	{
		boolean	invoke	= false;
		final String[][] metainfos = getMetaInfos(type);
		for(int i=0; !invoke && metainfos!=null && i<metainfos.length; i++)
		{
			invoke	= ADD.equals(metainfos[i][0])
				|| REMOVE.equals(metainfos[i][0])
				|| SET.equals(metainfos[i][0])
				|| REMOVE_OUTDATED.equals(metainfos[i][0]) && percept.equals(avatar);
		}
		
		if(invoke)
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
						
						
						for(int i=0; i<metainfos.length; i++)
						{
							if(ADD.equals(metainfos[i][0]))
							{
								IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
								if(!belset.containsFact(percept))
								{
									belset.addFact(percept);
//									System.out.println("added: "+percept+" to: "+belset);
								}
							}
							else if(REMOVE.equals(metainfos[i][0]))
							{
								IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
								if(belset.containsFact(percept))
								{
									belset.removeFact(percept);
//									System.out.println("removed: "+percept+" from: "+belset);
								}
							}
							else if(SET.equals(metainfos[i][0]))
							{
								IBelief bel = exta.getBeliefbase().getBelief(metainfos[i][1]);
								bel.setFact(percept);
//								System.out.println("set: "+percept+" in bel: "+bel);
							}
							else if(REMOVE_OUTDATED.equals(metainfos[i][0]) && percept.equals(avatar))
							{
								IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
								IVector1 vision	= getRange(avatar);
								Space2D	space2d	= (Space2D)space;
								IVector2	mypos	= (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
								ISpaceObject[]	known	= (ISpaceObject[])belset.getFacts();
								Set	seen	= new HashSet(Arrays.asList(space2d.getNearObjects(mypos, vision, null)));
								for(int j=0; j<known.length; j++)
								{
									if(!seen.contains(known[j]) && !vision.less(space2d.getDistance(mypos, (IVector2)known[j].getProperty(Space2D.PROPERTY_POSITION))))
									{
//										System.out.println("Removing disappeared object: "+percept+", "+known[j]);
										belset.removeFact(known[i]);
									}
								}
							}
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
	protected String[][] getMetaInfos(String percepttype)
	{
		if(percepttypes==null)
		{
			this.percepttypes = new HashMap();
			Object[] percepttypes = getPerceptTypes();
			for(int i=0; i<percepttypes.length; i++)
			{
				String[]	per = (String[])percepttypes[i];
				String[][]	newmis	= new String[][]{{per[1], per[2]}};
				String[][]	oldmis	= (String[][])this.percepttypes.get(per[0]);
				if(oldmis!=null)
					newmis	= (String[][])SUtil.joinArrays(oldmis, newmis);
				this.percepttypes.put(per[0], newmis);
			}
		}
		return (String[][])percepttypes.get(percepttype);
	}

	// Todo: unify range handling!?
	
	/**
	 *  Get the range.
	 *  @return The range.
	 */
	protected IVector1 getRange(ISpaceObject avatar)
	{
		Object tmp = avatar.getProperty(getRangePropertyName());
		return tmp==null? getDefaultRange(): tmp instanceof Number? new Vector1Double(((Number)tmp).doubleValue()): (IVector1)tmp;
	}

	/**
	 *  Get the default range.
	 *  @return The range.
	 */
	protected IVector1 getDefaultRange()
	{
		Object tmp = getProperty(PROPERTY_MAXRANGE);
		return tmp==null? Vector1Double.ZERO: tmp instanceof Number? new Vector1Double(((Number)tmp).doubleValue()): (IVector1)tmp;
	}
	
	/**
	 *  Get the range property name.
	 *  @return The range property name.
	 */
	protected String getRangePropertyName()
	{
		Object tmp = getProperty(PROPERTY_RANGE);
		return tmp==null? "range": (String)tmp;
	}
}