package jadex.bdi.planlib;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.IPerceptProcessor;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector1;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Double;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;

import java.util.HashMap;
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
	
	/** The unset action (sets a belief fact to null). */
	public static String UNSET = "unset";
	
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
	public void processPercept(final IEnvironmentSpace space, final String type, final Object percept, final IComponentIdentifier agent, final ISpaceObject avatar)
	{
		boolean	invoke	= false;
		final String[][] metainfos = getMetaInfos(type);
		for(int i=0; !invoke && metainfos!=null && i<metainfos.length; i++)
		{
			invoke	= ADD.equals(metainfos[i][0])
				|| REMOVE.equals(metainfos[i][0])
				|| SET.equals(metainfos[i][0])
				|| UNSET.equals(metainfos[i][0])
				|| REMOVE_OUTDATED.equals(metainfos[i][0]) && percept.equals(avatar);
		}
		
		if(invoke)
		{
			IComponentManagementService ces = (IComponentManagementService)space.getContext().getServiceContainer().getService(IComponentManagementService.class);
			IFuture fut = ces.getExternalAccess(agent);
			fut.addResultListener(new IResultListener()
			{
				public void exceptionOccurred(Object source, Exception exception)
				{
	//				exception.printStackTrace();
				}
				public void resultAvailable(Object source, Object result)
				{
					final IBDIExternalAccess exta = (IBDIExternalAccess)result;
					exta.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								for(int i=0; i<metainfos.length; i++)
								{
									IParsedExpression	cond	= metainfos[i].length==2 ? null
										: (IParsedExpression)getProperty(metainfos[i][2]);
									SimpleValueFetcher	fetcher	= null;
									if(cond!=null)
									{
										fetcher	= new SimpleValueFetcher();
										fetcher.setValue("$space", space);
										fetcher.setValue("$percept", percept);
										fetcher.setValue("$avatar", avatar);
										fetcher.setValue("$type", type);
										fetcher.setValue("$aid", agent);
										fetcher.setValue("$scope", exta);
									}

									if(ADD.equals(metainfos[i][0]))
									{
										IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
										if(cond!=null)
											fetcher.setValue("$facts", belset.getFacts());
										if(!belset.containsFact(percept) && (cond==null || evaluate(cond, fetcher)))
										{
											belset.addFact(percept);
//											System.out.println("added: "+percept+" to: "+belset);
										}
									}
									else if(REMOVE.equals(metainfos[i][0]))
									{
										IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
										if(cond!=null)
											fetcher.setValue("$facts", belset.getFacts());
										if(belset.containsFact(percept) && (cond==null || evaluate(cond, fetcher)))
										{
											belset.removeFact(percept);
//											System.out.println("removed: "+percept+" from: "+belset);
										}
									}
									else if(SET.equals(metainfos[i][0]))
									{
										IBelief bel = exta.getBeliefbase().getBelief(metainfos[i][1]);
										if(cond!=null)
											fetcher.setValue("$fact", bel.getFact());
										if(cond==null || evaluate(cond, fetcher))
											bel.setFact(percept);
//										System.out.println("set: "+percept+" in bel: "+bel);
									}
									else if(UNSET.equals(metainfos[i][0]))
									{
										IBelief bel = exta.getBeliefbase().getBelief(metainfos[i][1]);
										if(cond!=null)
											fetcher.setValue("$fact", bel.getFact());
										if(cond==null || evaluate(cond, fetcher))
											bel.setFact(null);
//										System.out.println("unset: "+percept+" in bel: "+bel);
									}
									else if(REMOVE_OUTDATED.equals(metainfos[i][0]) && percept.equals(avatar))
									{
										IBeliefSet belset = exta.getBeliefbase().getBeliefSet(metainfos[i][1]);
										if(cond!=null)
											fetcher.setValue("$facts", belset.getFacts());
										if(cond==null || evaluate(cond, fetcher))
										{
											IVector1 vision	= getRange(avatar);
											Space2D	space2d	= (Space2D)space;
											IVector2	mypos	= (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
											ISpaceObject[]	known	= (ISpaceObject[])belset.getFacts();
											Set	seen	= space2d.getNearObjects(mypos, vision);
											for(int j=0; j<known.length; j++)
											{
												IVector2	knownpos	= (IVector2)known[j].getProperty(Space2D.PROPERTY_POSITION);
												// Hack!!! Shouldn't react to knownpos==null
												if(!seen.contains(known[j]) && (knownpos==null || !vision.less(space2d.getDistance(mypos, knownpos))))
												{
//													System.out.println("Removing disappeared object: "+percept+", "+known[j]);
													belset.removeFact(known[j]);
												}
											}
										}
									}
								}
							}
							catch(Exception e)
							{
								// try catch for the case that the agent is not yet inited and
								// the belief value is not accessible
								// Todo: fix agent init.
								// Exception might be thrown, when agent not yet initialized
								// -> AgentRules.findValue() fails due to missing initparents,
								// when belief is initialized on demand.
								// -> CMS should not provide external access to agent when not yet inited.  
							}
						}
					});
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
	 *  Get meta infos about a percept type.
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
				String[][]	newmis	= per.length==3 ? new String[][]{{per[1], per[2]}} : new String[][]{{per[1], per[2], per[3]}};
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
	
	/**
	 *  Evaluate a condition.
	 *  @param exp	The expression.
	 *  @param fetcher	The value fetcher.
	 */
	protected boolean	evaluate(IParsedExpression exp, IValueFetcher fetcher)
	{
		boolean	ret	= false;
		try
		{
			ret	= ((Boolean)exp.getValue(fetcher)).booleanValue();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
}